package io.modello.specification.uca;

import io.horizon.atom.program.Kv;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface OExpression {

    Object after(Kv<String, Object> kv);
}
