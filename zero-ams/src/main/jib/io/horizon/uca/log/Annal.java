package io.horizon.uca.log;

import io.horizon.annotations.Memory;
import io.horizon.specification.uca.HLogger;
import io.horizon.spi.HorizonIo;
import io.horizon.uca.cache.Cc;
import io.horizon.uca.log.internal.BridgeAnnal;
import io.horizon.util.HUt;

import java.util.Objects;

/**
 * Unite Logging system connect to vert.x, io.vertx.zero.io.vertx.zero.io.vertx.up.io.vertx.up.io.vertx.up.util kit of Vertx-Zero
 * 旧代码：
 * final Class<?> cacheKey = Objects.isNull(clazz) ? Log4JAnnal.class : clazz;
 * return CACHE.CC_ANNAL_EXTENSION.pick(() -> new BridgeAnnal(clazz), cacheKey);
 * 此处会引起  java.lang.IllegalStateException: Recursive update 的问题
 * 假设底层已提供实现 Logger logger = LoggerFactory.getLogger，所以就不考虑线程安全
 * 问题，现阶段获取日志器会引起很大的线程安全问题，此处不能如此执行，特别针对全局模式下的操作尤其需要小心
 */
public interface Annal extends HLogger {
    /**
     * 在 OSGI Bundle 内的处理模式
     *
     * @param clazz OSGI Bundle 内的类
     * @param io    HorizonIo SPI 在 Bundle 内提取（提取时方式和原始方式不同）
     *
     * @return {@link Annal}
     */
    static Annal get(final Class<?> clazz, final HorizonIo io) {
        if (Objects.isNull(io)) {
            return get(clazz);
        }
        return new BridgeAnnal(clazz, io);
    }

    /**
     * 常用的日志处理模式
     *
     * @param clazz 普通类
     *
     * @return {@link Annal}
     */
    static Annal get(final Class<?> clazz) {
        final HorizonIo io = HUt.service(HorizonIo.class);
        return new BridgeAnnal(clazz, io);
    }

}

interface CACHE {
    /**
     * 按类分配的日志缓存池
     * 内部使用的按 hasCode 分配的日志缓存池
     * 旧代码：
     * <pre><code>
     * @Memory(Annal.class)
     * Cc<Class < ?>, Annal> CC_ANNAL_EXTENSION = Cc.open();
     * </code></pre>
     */
    @Memory(Annal.class)
    Cc<Integer, Annal> CC_ANNAL_INTERNAL = Cc.open();
}