package io.horizon.uca.boot;

import io.horizon.eon.VMessage;
import io.horizon.eon.em.EmBoot;
import io.horizon.exception.AbstractException;
import io.horizon.exception.internal.BootIoMissingException;
import io.horizon.specification.boot.HLauncher;
import io.horizon.spi.BootIo;
import io.horizon.uca.log.LogAs;
import io.horizon.util.HUt;
import io.macrocosm.specification.app.HPre;
import io.macrocosm.specification.boot.HOn;
import io.macrocosm.specification.config.HConfig;
import io.macrocosm.specification.config.HEnergy;

import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

/**
 * 「启动管理器」
 * 直接启动，不需要任何配置，方便主函数
 *
 * @author lang : 2023-05-30
 */
@SuppressWarnings("all")
public class KLauncher<T> {
    private static KLauncher INSTANCE;

    private final HLauncher<T> launcher;
    private final HEnergy energy;

    private final String[] arguments;

    private KLauncher(final Class<?> bootCls, final String[] args) {
        /*  提取SPI部分，严格模式  */
        final BootIo io = HUt.service(BootIo.class);
        if (Objects.isNull(io)) {
            throw new BootIoMissingException(getClass());
        }
        /*  配置验证 */
        this.energy = io.energy(bootCls, args);
        this.launcher = io.launcher();
        LogAs.Boot.info(this.getClass(), VMessage.BootIo.LAUNCHER_COMPONENT, this.launcher.getClass());
        this.arguments = args;
        LogAs.Boot.info(this.getClass(), VMessage.HOn.COMPONENT_ARGS, args.length, HUt.fromJoin(args));
    }

    public static <T> KLauncher<T> create(final Class<?> bootCls, final String[] args) {
        if (INSTANCE == null) {
            INSTANCE = new KLauncher<>(bootCls, args);
        }
        return (KLauncher<T>) INSTANCE;
    }

    public <CONFIG extends HConfig> void start(final BiConsumer<T, CONFIG> consumer) {
        // 1. 环境变量连接，执行环境变量初始化
        KEnvironment.initialize();

        // 2. 提取自配置的 HOn 组件，执行启动前的初始化
        final HOn on = this.configureOn();


        /**
         * 此处 {@link HOn} 已执行完 configure 的第一个周期
         * 直接使用 HOn 和 Consumer 配合完成启动流程
         * <pre><code>
         *     1. 环境变量已连接
         *     2. 启动扫描已完成
         *     3. 文件目录已检查
         *     4. 可直接初始化 {@link T} 部分
         * </code></pre>
         */
        this.launcher.start(on, server -> {
            final CONFIG configuraion = (CONFIG) on.store();
            final Class<?> preCls = this.energy.component(EmBoot.LifeCycle.PRE);


            // Pre 1：针对容器初始化完成之后的第一步初始化流程
            Optional.ofNullable(preCls).ifPresent(pClass -> {
                // 配置绑定
                configuraion.pre(pClass);
                final HPre<T> pre = HUt.singleton(pClass);
                pre.beforeStart(server, configuraion.options());
            });


            /**
             * 此处是穿透效果，直接外层调用
             * <pre><code>
             *     (server,config) -> {
             *         server -> 服务器引用（初始化好的框架部分）
             *         config -> 配置引用（初始化好的配置部分）
             *     }
             * </code></pre>
             */
            consumer.accept(server, configuraion);
        });
    }

    /**
     * {@link HOn} 启动周期核心配置组件，执行生命周期组件中的
     * <pre><code>
     *     boot:
     *        component:
     *           on:
     *           pre:
     *           off:
     *           run:
     * </code></pre>
     *
     * @return {@link HOn}
     */
    private HOn configureOn() {
        final Class<?> implOn = this.energy.component(EmBoot.LifeCycle.ON);
        if (Objects.isNull(implOn)) {
            // 未配置组件，直接跳过
            return null;
        }
        HOn on = HUt.singleton(implOn);
        if (Objects.isNull(on)) {
            // 组件初始化失败，直接跳过
            return null;
        }
        on = on.args(this.arguments);

        final HConfig configuration = this.energy.config(on.getClass());

        // 初始化，返回结果
        try {
            if (Objects.nonNull(configuration)) {
                on.configure(configuration);
                LogAs.Boot.info(this.getClass(), VMessage.HOn.COMPONENT, on.getClass(), configuration.getClass());
                LogAs.Boot.info(this.getClass(), VMessage.HOn.COMPONENT_CONFIG, configuration.options().encodePrettily());
            }
            return on;
        } catch (AbstractException error) {
            error.printStackTrace();
            throw error;
        }
    }
}
