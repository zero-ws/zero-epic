package io.horizon.atom.datamation;

import io.horizon.uca.log.Annal;
import io.horizon.util.HUt;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

class KDictData {
    private static final Annal LOGGER = Annal.get(KDictData.class);
    private final ConcurrentMap<String, JsonArray> dictData
        = new ConcurrentHashMap<>();

    /*
     * Default domain
     */
    KDictData() {
    }

    ConcurrentMap<String, JsonArray> data() {
        return this.dictData;
    }

    void data(final ConcurrentMap<String, JsonArray> dictData) {
        if (Objects.nonNull(dictData) && !dictData.isEmpty()) {
            this.dictData.clear();                          /* Clear Queue */
            this.dictData.putAll(dictData);
        } else {
            LOGGER.debug("DictFabric got empty dictData ( ConcurrentMap<String, JsonArray> ) !");
        }
    }

    JsonArray item(final String key) {
        return this.dictData.getOrDefault(key, new JsonArray());
    }

    boolean ready() {
        return !this.dictData.isEmpty();
    }

    /*
     * Api for update / add / get operation on `dictData`
     */
    void itemUpdate(final String dictName, final JsonObject input, final String keyField) {
        final JsonObject data = HUt.valueJObject(input);
        final JsonArray original = this.item(dictName);
        // Check not null
        if (Objects.nonNull(data.getValue(keyField))) {
            final JsonArray updated = HUt.elementSave(original, data, keyField);
            this.dictData.put(dictName, updated);
        }
    }

    void itemUpdate(final String dictName, final JsonArray input, final String keyField) {
        final JsonArray data = HUt.valueJArray(input);
        HUt.itJArray(data).filter(item -> Objects.nonNull(item.getValue(keyField)))
            .forEach(json -> this.itemUpdate(dictName, json, keyField));
    }

    boolean itemExist(final String dictName, final String value, final String keyField) {
        if (HUt.isNil(keyField, value)) {
            return false;
        } else {
            final JsonArray original = this.item(dictName);
            final long counter = HUt.itJArray(original)
                .map(each -> each.getValue(keyField))
                .filter(Objects::nonNull)
                .filter(value::equals).count();
            return 0 < counter;
        }
    }

    JsonObject itemFind(final String dictName, final String value, final String keyField) {
        final JsonArray dictData = this.item(dictName);
        // Find the `keyField` = value as condition
        return HUt.elementFind(dictData, keyField, value);
    }
}
