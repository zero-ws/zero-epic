package io.horizon.exception;

import io.horizon.atom.program.KFailure;
import io.horizon.eon.VString;
import io.horizon.eon.error.ErrorMessage;
import io.horizon.spi.HorizonIo;

/**
 * 和资源文件绑定的启动异常类，通常在容器启动中抛出该信息
 * 1. 模型校验
 * 2. 容器启动
 */
public abstract class BootingException extends AbstractException {

    private final KFailure failure;

    public BootingException(final Class<?> clazz, final Object... args) {
        super(VString.EMPTY);
        // KFailure 构造
        this.failure = KFailure.of(clazz, args)         // caller, params
            .bind(this.getCode())                       // error code
            .bind(ErrorMessage.EXCEPTION_BOOTING);      // [ ERR{} ] ( {} ) Booting Error: {}
    }

    @Override
    public abstract int getCode();

    @Override
    public String getMessage() {
        return this.failure.message();
    }

    @Override
    public Class<?> caller() {
        return this.failure.caller();
    }

    @Override
    public BootingException io(final HorizonIo io) {
        this.failure.bind(io);
        return this;
    }
}
