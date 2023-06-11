package io.modello.specification.atom;

import io.modello.atom.reference.RQuery;
import io.modello.atom.reference.RQuote;
import io.modello.atom.reference.RReference;
import io.modello.atom.reference.RResult;

import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="http://www.origin-x.cn">Lang</a>
 */
public interface HReference {

    RReference refData(String name);

    ConcurrentMap<String, RQuote> refInput();

    ConcurrentMap<String, RQuery> refQr();

    ConcurrentMap<String, RResult> refOutput();
}
