package io.horizon.uca.boot;

import io.horizon.eon.VMessage;
import io.horizon.eon.em.Environment;
import io.horizon.eon.em.app.OsType;
import io.horizon.eon.spec.VBoot;
import io.horizon.runtime.Macrocosm;
import io.horizon.uca.log.LogAs;
import io.horizon.util.HUt;

import java.util.Properties;
import java.util.concurrent.ConcurrentMap;

/**
 * 「内置环境变量准备」
 * 开发环境综述，用于处理开发环境专用的启动器，在 KLauncher 中会被调用，主要检查
 * .env.development 文件，初始化当前环境的系统环境变量，执行完成后处理下一步骤
 * 关于 {@link io.horizon.eon.em.Environment} 的计算流程如下
 * <pre><code>
 *     1. 默认情况下，如果检测到 .env.development 文件应该是
 *        {@link io.horizon.eon.em.Environment#Development}
 *     2. 但为了在开发环境中可直接使用生产环境 / 模拟环境相关环境变量，会在文件存在时检查 `ZERO_ENV` 的值，若不存在该值则不考虑环境变量的切换，若存在此环境变量则直接将环境变量强制转换成对应的值
 *     3. 生产环境中不会将 .env.development 打包，且不会命中此文件开启开发模式
 *
 * </code></pre>
 *
 * @author lang : 2023-05-30
 */
class KEnvironment {
    /**
     * 环境变量初始化验证，内置启动会被 {@link io.horizon.uca.boot.KLauncher} 调用
     */
    static void initialize() {
        /*
         * 判断是否开启了开发环境，如果开启了开发环境，那么就会读取 .env.development 文件
         * 加载文件中的环境变量到系统层（只适用于开发）
         */
        if (HUt.ioExist(VBoot._ENV_DEVELOPMENT)) {
            // 1. 环境变量设置
            final OsType os = HUt.envOs();
            LogAs.Boot.warn(KEnvironment.class, VMessage.KEnvironment.DEVELOPMENT,
                os.name(), VBoot._ENV_DEVELOPMENT);
            final Properties properties = HUt.ioProperties(VBoot._ENV_DEVELOPMENT);
            // 1.1. 环境变量注入
            if (!properties.containsKey(Macrocosm.ZERO_ENV)) {
                properties.put(Macrocosm.ZERO_ENV, Environment.Development.name());
            }
            /*
             * 开发环境需要带上启动参数，否则会报错，这里是为了解决 JDK 9 以上版本的问题
             * --add-opens java.base/java.util=ALL-UNNAMED
             * --add-opens java.base/java.lang=ALL-UNNAMED
             */
            final ConcurrentMap<String, String> written = HUt.envOut(properties);

            // 2. 环境变量打印
            final String environments = HUt.envString(written);
            LogAs.Boot.info(KEnvironment.class, VMessage.KEnvironment.ENV, environments);
        }
    }
}
