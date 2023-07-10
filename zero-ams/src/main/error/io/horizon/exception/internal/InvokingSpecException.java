package io.horizon.exception.internal;

import io.horizon.annotations.Development;
import io.horizon.eon.error.ErrorCode;
import io.horizon.exception.InternalException;
import io.horizon.util.HUt;

import java.lang.reflect.Method;

/**
 * @author lang : 2023-07-10
 */
public class InvokingSpecException extends InternalException {

    public InvokingSpecException(final Class<?> caller, final Method method) {
        super(caller, HUt.fromMessage(ErrorCode._11011.M(), method.getName()));
    }

    @Override
    protected int getCode() {
        return ErrorCode._11011.V();
    }

    @Development("IDE视图专用")
    private int __11011() {
        return this.getCode();
    }

}
