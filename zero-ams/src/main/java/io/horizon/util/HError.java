package io.horizon.util;

import io.horizon.exception.WebException;
import io.horizon.exception.web._500InternalCauseException;
import io.horizon.exception.web._500InternalServerException;
import io.horizon.fn.HFn;

import java.util.Objects;

/**
 * @author lang : 2023/4/30
 */
class HError {

    // 异常专用信息
    static WebException failWeb(final Class<?> clazz, final Throwable error,
                                final boolean isCause) {
        return HFn.runOr(error instanceof WebException,
            // Throwable 异常本身是 WebException，直接转出
            () -> {
                assert error instanceof WebException;
                return (WebException) error;
            },
            // Throwable 异常不是 WebException，封装成 500 默认异常转出
            () -> {
                final Class<?> target = Objects.isNull(clazz) ? HError.class : clazz;
                // 传入 Throwable 是否为空
                if (Objects.isNull(error)) {
                    return new _500InternalServerException(target, "Throwable is null");
                }
                if (isCause) {
                    // 调用 getCause() 模式
                    final Throwable cause = error.getCause();
                    if (Objects.isNull(cause)) {
                        return new _500InternalCauseException(target, error);
                    }

                    // 递归调用
                    return failWeb(clazz, cause, true);
                } else {
                    // 直接模式
                    assert !(error instanceof WebException);
                    return new _500InternalServerException(target, error.getMessage());
                }
            }
        );
    }

    static WebException failWeb(final Class<? extends WebException> clazz, final Object... args) {
        // Fix：此处必须追加 <WebException> 泛型，否则会抛出转型异常
        return HFn.<WebException>runOr(Objects.isNull(clazz),
            // 特殊情况，编程过程中忘了传入 clazz
            () -> new _500InternalServerException(clazz, "WebException class is null"),
            // 正常情况，传入 clazz
            () -> HInstance.instance(clazz, args)
        );
    }
}
