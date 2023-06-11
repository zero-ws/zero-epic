package io.horizon.atom.program;

import io.horizon.fn.HFn;
import io.vertx.core.Future;

/*
 * [Data Structure]
 * Single Rxjava container for null dot that is not used in
 * Rxjava2.
 * For usage such as:
 *
 * compose(KRef::future)  // stored
 * compose(xxx)
 * compose(x -> Refere.get()) // pick up
 *
 * When some steps skipped, this object is usage for reference stored
 */
@SuppressWarnings("all")
public final class KRef {

    private Object reference;

    public <T> KRef add(final T reference) {
        this.reference = reference;
        return this;
    }

    /*
     * For vert.x compose method only.
     */
    public <T> Future<T> future(final T reference) {
        this.reference = reference;
        return Future.succeededFuture(reference);
    }

    public <T> Future<T> future() {
        return Future.succeededFuture((T) this.reference);
    }

    public boolean successed() {
        return null != this.reference;
    }

    @SuppressWarnings("unchecked")
    public <T> T get() {
        return HFn.runOr(() -> (T) this.reference, this.reference);
    }
}
