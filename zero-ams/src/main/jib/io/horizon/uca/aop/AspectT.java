package io.horizon.uca.aop;

import io.horizon.eon.VName;
import io.horizon.eon.em.typed.ChangeFlag;
import io.horizon.fn.HFn;
import io.horizon.util.HUt;
import io.vertx.core.Future;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author lang : 2023-05-27
 */
class AspectT {

    private final AspectRobin config;

    AspectT(final AspectRobin config) {
        this.config = config;
    }

    <T> Function<T, Future<T>> beforeFn(
        final Supplier<T> supplierDefault,
        final ChangeFlag... type
    ) {
        return input -> {
            if (Objects.isNull(input)) {
                // Empty Input Captured
                return Future.succeededFuture(supplierDefault.get());
            }
            // Before
            return HFn.passion(input,
                this.plugin(() -> this.config.beforeQueue(input), Before::types, type));
        };
    }

    <T> Function<T, Future<T>> afterFn(
        final Supplier<T> supplierDefault,
        final ChangeFlag... type) {
        return input -> {
            if (Objects.isNull(input)) {
                // Empty Input Captured
                return Future.succeededFuture(supplierDefault.get());
            }
            // After
            return HFn.passion(input,
                    this.plugin(() -> this.config.afterQueue(input), After::types, type))
                // Job
                .compose(processed -> HFn.parallel(processed,
                    this.plugin(() -> this.config.afterJob(input), After::types, type)));
        };
    }

    <T> Function<T, Future<T>> aopFn(
        final Supplier<T> supplierDefault,
        final Function<T, Future<T>> runner,
        final ChangeFlag... type) {
        return input -> {
            if (Objects.isNull(input)) {
                // Empty Input Captured
                return Future.succeededFuture(supplierDefault.get());
            }
            // Before
            return Future.succeededFuture(input)
                // Run Before
                .compose(processed -> HFn.passion(processed,
                    this.plugin(() -> this.config.beforeQueue(input), Before::types, type)))
                // Runner
                .compose(inputT -> runner.apply(inputT)
                    .compose(processed -> AspectData.build(inputT, processed))
                )
                // After
                .compose(processed -> HFn.passion(processed,
                    this.plugin(() -> this.config.afterQueue(input), After::types, type)))
                // Job
                .compose(processed -> HFn.parallel(processed,
                    this.plugin(() -> this.config.afterJob(input), After::types, type)));
        };
    }

    @SuppressWarnings("all")
    private <P, T> Function<T, Future<T>> buildFn(final P plugin, final JsonObject config) {
        return input -> {
            if (plugin instanceof Before) {
                /*
                 * Run Before 前置模式下不需要做参数编译
                 * 此处的 input 就直接是输入参数了，做强制转换即可
                 */
                final Before before = (Before) plugin;
                if (input instanceof JsonObject) {
                    final JsonObject inputJ = (JsonObject) input;
                    return before.beforeAsync(inputJ, config)
                        .compose(json -> Future.succeededFuture((T) json));
                } else {
                    final JsonArray inputA = (JsonArray) input;
                    return before.beforeAsync(inputA, config)
                        .compose(array -> Future.succeededFuture((T) array));
                }
            } else {
                /*
                 * Run After 后置模式下需要对参数做编译
                 * 此处的 input 中会包含 __input 请求参数，所以此处需要构造新的
                 * 参数来针对后置插件进行操作，防止数据丢失的情况发生
                 */
                final After after = (After) plugin;
                if (input instanceof JsonObject) {
                    final JsonObject inputJ = (JsonObject) input;
                    return after.afterAsync(this.buildParameters(inputJ), config)
                        .compose(json -> Future.succeededFuture((T) json));
                } else {
                    final JsonArray inputA = (JsonArray) input;
                    return after.afterAsync(this.buildParameters(inputA), config)
                        .compose(array -> Future.succeededFuture((T) array));
                }
            }
        };
    }

    private <P, T> List<Function<T, Future<T>>> plugin(final Supplier<List<Class<?>>> fnClass,
                                                       final Function<P, Set<ChangeFlag>> fnTypes, final ChangeFlag... type) {
        final List<Class<?>> pluginCls = fnClass.get();
        final List<Function<T, Future<T>>> executor = new ArrayList<>();
        pluginCls.stream().map(plugin -> {
            final P beforeFn = HUt.instance(plugin);
            final Set<ChangeFlag> supported = fnTypes.apply(beforeFn);
            // Expected:  supported
            // Limitation: type
            if (Arrays.stream(type).anyMatch(supported::contains)) {
                final JsonObject config = this.config.config(plugin);
                return this.<P, T>buildFn(beforeFn, config);
            } else {
                return null;
            }
        }).filter(Objects::nonNull).forEach(executor::add);
        return executor;
    }


    private JsonObject buildParameters(final JsonObject input) {
        final JsonObject inputJ = HUt.valueJObject(input, true);
        final JsonObject requestJ = HUt.valueJObject(input, VName.__.INPUT, true);
        return inputJ.mergeIn(requestJ, true);
    }

    private JsonArray buildParameters(final JsonArray input) {
        final JsonArray paramA = new JsonArray();
        HUt.itJArray(input).forEach(inputJ -> paramA.add(this.buildParameters(inputJ)));
        return paramA;
    }
}
