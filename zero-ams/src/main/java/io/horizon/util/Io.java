package io.horizon.util;

import io.horizon.eon.VString;
import io.horizon.exception.internal.EmptyIoException;
import io.horizon.exception.internal.JsonFormatException;
import io.horizon.fn.HFn;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Properties;

/**
 * The library for IO resource reading.
 */
final class Io {

    /**
     * 「DEAD-LOCK」LoggerFactory.getLogger
     * Do not use `Annal` logger because of deadlock.
     */
    private static final LogUtil LOG = LogUtil.from(Io.class);

    private Io() {
    }

    static JsonArray ioJArray(final InputStream in) {
        final JsonArray content;
        try {
            content = new JsonArray(ioString(in, null));
        } catch (final Throwable ex) {
            throw new JsonFormatException(Io.class, "Stream/JArray");
        }
        return content;
    }

    static JsonArray ioJArray(final URL url) {
        if (Objects.isNull(url)) {
            return new JsonArray();
        }
        try (final InputStream in = url.openStream()) {
            return ioJArray(in);
        } catch (final Throwable ex) {
            throw new EmptyIoException(Io.class, "URL/JArray: " + url.getPath());
        }
    }

    static JsonArray ioJArray(final String filename) {
        final JsonArray content;
        try {
            content = new JsonArray(ioString(filename, null));
        } catch (final Throwable ex) {
            throw new JsonFormatException(Io.class, filename);
        }
        return content;
    }

    static JsonObject ioJObject(final String filename) {
        final JsonObject content;
        try {
            content = new JsonObject(ioString(filename, null));
        } catch (final Throwable ex) {
            throw new JsonFormatException(Io.class, filename);
        }
        return content;
    }

    static JsonObject ioJObject(final InputStream in) {
        final JsonObject content;
        try {
            content = new JsonObject(ioString(in, null));
        } catch (final Throwable ex) {
            throw new JsonFormatException(Io.class, "Stream/JObject");
        }
        return content;
    }

    static JsonObject ioJObject(final URL url) {
        if (Objects.isNull(url)) {
            return new JsonObject();
        }
        try (final InputStream in = url.openStream()) {
            return ioJObject(in);
        } catch (final Throwable ex) {
            throw new EmptyIoException(Io.class, "URL/JObject: " + url.getPath());
        }
    }


    static String ioString(final URL url, final String joined) {
        if (Objects.isNull(url)) {
            return VString.EMPTY;
        }
        try (final InputStream in = url.openStream()) {
            return ioString(in, joined);
        } catch (final Throwable ex) {
            throw new EmptyIoException(Io.class, "URL/String: " + url.getPath());
        }
    }

    static String ioString(final InputStream in, final String joined) {
        final StringBuilder buffer = new StringBuilder();
        return HFn.failOr(() -> {
            try (final BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
                // Character stream
                String line;
                while (null != (line = reader.readLine())) {
                    buffer.append(line);
                    if (!TIs.isNil(joined)) {
                        buffer.append(joined);
                    }
                }
                return buffer.toString();
            }
        }, in);
    }

    static String ioString(final String filename, final String joined) {
        return HFn.failOr(() -> ioString(IoStream.read(filename), joined), filename);
    }

    /**
     * Read to property object
     *
     * @param filename input filename
     *
     * @return Properties that will be returned
     */
    static Properties ioProp(final String filename) {
        return HFn.failOr(() -> {
            try (final InputStream in = IoStream.read(filename)) {
                final Properties prop = new Properties();
                prop.load(in);
                return prop;
            }
        }, filename);
    }

    static URL ioURL(final String filename) {
        return HFn.failOr(() -> {
            final URL url = Thread.currentThread().getContextClassLoader()
                .getResource(filename);
            return HFn.runOr(null == url, null,
                () -> Io.class.getResource(filename),
                () -> url);
        }, filename);
    }


    static File ioFile(final String filename) {
        return HFn.failOr(() -> {
            final File file = new File(filename);
            return HFn.runOr(file.exists(), null,
                () -> file,
                () -> {
                    final URL url = ioURL(filename);
                    if (null == url) {
                        throw new EmptyIoException(Io.class, filename);
                    }
                    return new File(url.getFile());
                });
        }, filename);
    }

    static boolean isExist(final String filename) {
        try {
            final File file = new File(filename);
            if (file.exists()) {
                return true;
            }
            final URL url = ioURL(filename);
            return Objects.nonNull(url);
        } catch (final Throwable ex) {
            // Fix: java.lang.NullPointerException
            // File does not exist
            return false;
        }
    }

    /**
     * Read to Path
     *
     * @param filename input filename
     *
     * @return file content that converted to String
     */
    static String ioPath(final String filename) {
        return HFn.failOr(() -> {
            final File file = ioFile(filename);
            return HFn.failOr(() -> {
                LOG.io(INFO.Io.INF_PATH, file.getAbsolutePath());
                return file.getAbsolutePath();
            }, file);
        }, filename);
    }
}
