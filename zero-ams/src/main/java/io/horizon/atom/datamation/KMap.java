package io.horizon.atom.datamation;

import io.horizon.eon.em.EmAop;
import io.horizon.util.HUt;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.Serializable;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/*
 * [Data Structure]
 * 1) mapping stored `from -> to`
 * 2) revert stored `to -> from`
 * This data structure will store two mappings between configuration file here.
 * It could be used in some places to process ( ----> / <---- )
 *
 * Two mapping categories:
 * A) Single Mapping: String = String
 * B) Multi Mapping: String = JsonObject
 */
public class KMap implements Serializable {

    /*
     * Root ( Single )
     * String = String
     * Map ( Multi )
     * String = JsonObject
     */
    private final KMapping root = new KMapping();
    private final ConcurrentMap<String, KMapping> mapping =
        new ConcurrentHashMap<>();
    /*
     * Configured `MappingMode` and `Class<?>`
     */
    private EmAop.Effect mode = EmAop.Effect.NONE;
    private Class<?> component;

    public KMap() {
    }

    public KMap(final JsonObject input) {
        this.init(input);
    }

    public KMap init(final JsonObject input) {
        if (HUt.isNotNil(input)) {
            /*
             * Mix data structure for
             * {
             *     "String": {},
             *     "String": "String",
             *     "String": {}
             * }
             */
            this.root.init(input);
            /*
             * Content mapping `Map` here
             */
            input.fieldNames().stream()
                /* Only stored JsonObject value here */
                .filter(field -> input.getValue(field) instanceof JsonObject)
                .forEach(field -> {
                    final JsonObject fieldValue = input.getJsonObject(field);
                    /* Init here */
                    if (HUt.isNotNil(fieldValue)) {
                        /* Json mapping here */
                        final KMapping item = new KMapping(fieldValue);
                        this.mapping.put(field, item);
                    }
                });
        }
        return this;
    }

    /*
     * 1) MappingMode
     * 2) Class<?>
     * 3) DualMapping ( Bind Life Cycle )
     * 4) valid() -> boolean Check whether the mapping is enabled.
     */
    public EmAop.Effect getMode() {
        return this.mode;
    }

    public Class<?> getComponent() {
        return this.component;
    }

    public KMap bind(final EmAop.Effect mode) {
        this.mode = mode;
        return this;
    }

    public KMap bind(final Class<?> component) {
        this.component = component;
        return this;
    }

    public boolean valid() {
        return EmAop.Effect.NONE != this.mode;
    }

    // -------------  Get by identifier ----------------------------
    /*
     * Child get here
     */
    public KMapping child(final String key) {
        final KMapping selected = this.mapping.get(key);
        if (Objects.isNull(selected) || selected.isEmpty()) {
            return this.root;
        } else {
            return selected;
        }
    }

    public KMapping child() {
        return this.root;
    }

    // -------------  Root Method here for split instead -----------
    /*
     * from -> to, to values to String[]
     */
    public String[] to() {
        return this.root.to();
    }

    public String[] from() {
        return this.root.from();
    }

    public Set<String> to(final JsonArray keys) {
        return this.root.to(keys);
    }

    public Set<String> from(final JsonArray keys) {
        return this.root.from(keys);
    }

    /*
     * Get value by from key, get to value
     */
    public String to(final String from) {
        return this.root.to(from);
    }

    public Class<?> toType(final String from) {
        return this.root.toType(from);
    }

    public boolean fromKey(final String key) {
        return this.root.fromKey(key);
    }

    public String from(final String to) {
        return this.root.from(to);
    }

    public Class<?> fromType(final String to) {
        return this.root.fromType(to);
    }

    public boolean toKey(final String key) {
        return this.root.toKey(key);
    }

    /*
     * from -> to, from keys
     */
    public Set<String> keys() {
        return this.root.keys();
    }

    public Set<String> values() {
        return this.root.values();
    }

    @Override
    public String toString() {
        return "DualMapping{" +
            "root=" + this.root +
            ", mapping=" + this.mapping +
            ", mode=" + this.mode +
            ", component=" + this.component +
            '}';
    }
}
