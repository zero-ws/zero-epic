package io.horizon.atom.datamation;

import io.horizon.util.HUt;
import io.vertx.core.json.JsonArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
 * 1) Tabular related data
 * 2）Assist related data
 * 3）Category related data
 * Configuration for three critical data to define rule here.
 * Rules definition here ( Multi rule definition )
 * [
 *     {
 *         "source":"CATEGORY / TABULAR / ASSIST",
 *         "types": [
 *              "xxx": "xxx"
 *         ],
 *         "key": "assistKey",
 *         "sourceComponent": "class",
 *         "filters": {
 *         }
 *     }
 * ]
 */
public class KDictConfig implements Serializable {

    private final List<KDictSource> source = new ArrayList<>();
    private final ConcurrentMap<String, KDictUse> consumer = new ConcurrentHashMap<>();
    private Class<?> component;

    public KDictConfig(final String literal) {
        if (HUt.isJArray(literal)) {
            final JsonArray parameters = new JsonArray(literal);
            HUt.itJArray(parameters)
                .map(KDictSource::new)
                .forEach(this.source::add);
        }
    }

    public KDictConfig(final JsonArray input) {
        if (Objects.nonNull(input)) {
            HUt.itJArray(input)
                .map(KDictSource::new)
                .forEach(this.source::add);
        }
    }

    public KDictConfig bind(final Class<?> component) {
        if (Objects.isNull(component)) {
            /*
             * When component not found,
             * clear source data cache to empty list.
             * It's force action here to clear source instead of others
             * 1) If you don't bind Class<?> component, the source will be cleared
             * 2) If you want to bind Class<?> component, it means that all the inited dict
             * will be impact
             */
            this.source.clear();
            this.consumer.clear();
        } else {
            this.component = component;
        }
        return this;
    }

    public KDictConfig bind(final ConcurrentMap<String, KDictUse> epsilon) {
        if (Objects.nonNull(epsilon)) {
            this.consumer.putAll(epsilon);
        }
        return this;
    }

    public boolean validSource() {
        return !this.source.isEmpty();
    }

    public boolean valid() {
        /*
         * When current source is not empty,
         * The `dictComponent` is required here.
         */
        return this.validSource() && Objects.nonNull(this.component);
    }

    public Class<?> configComponent() {
        return this.component;
    }

    public ConcurrentMap<String, KDictUse> configUse() {
        return this.consumer;
    }

    public List<KDictSource> configSource() {
        return this.source;
    }
}
