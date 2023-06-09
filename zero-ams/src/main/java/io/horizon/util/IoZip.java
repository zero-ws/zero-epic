package io.horizon.util;

import io.horizon.annotations.ChatGPT;
import io.horizon.eon.VValue;
import io.horizon.eon.em.typed.CompressLevel;
import io.horizon.fn.HFn;
import io.vertx.core.buffer.Buffer;

import java.io.*;
import java.util.Set;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

class IoZip {

    static Buffer ioZip(final Set<String> fileSet) {
        // Create Tpl zip file path
        return HFn.failOr(() -> {
            final ByteArrayOutputStream fos = new ByteArrayOutputStream();
            try (final ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fos))) {
                final byte[] buffers = new byte[VValue.DFT.SIZE_BYTE_ARRAY];
                fileSet.forEach(filename -> ioZip(zos, buffers, filename));
            }
            return Buffer.buffer(fos.toByteArray());
        });
    }

    @ChatGPT
    private static void ioZip(final ZipOutputStream zos, final byte[] buffers, final String filename) {
        HFn.jvmAt(() -> {
            final File file = new File(filename);
            final ZipEntry zipEntry = new ZipEntry(file.getName());
            zos.putNextEntry(zipEntry);
            try (final FileInputStream fis = new FileInputStream(file);
                 final BufferedInputStream bis = new BufferedInputStream(fis, VValue.DFT.SIZE_BYTE_ARRAY)) {
                int read;
                while ((read = bis.read(buffers)) != -1) {
                    zos.write(buffers, 0, read);
                }
            }
        });
    }

    static String ioCompress(final String file) {
        final byte[] bytes = IoStream.readBytes(file);
        final byte[] compressed = decompress(bytes);
        return new String(compressed, VValue.DFT.CHARSET);
    }

    static byte[] compress(final byte[] data, final CompressLevel level) {
        return HFn.failOr(() -> {
            final Deflater deflater = new Deflater();
            deflater.setLevel(level.getLevel());
            deflater.setInput(data);

            try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(data.length)) {
                deflater.finish();
                final byte[] buffer = new byte[VValue.DFT.SIZE_BYTE_ARRAY];
                while (!deflater.finished()) {
                    final int count = deflater.deflate(buffer);
                    outputStream.write(buffer, 0, count);
                }
                return outputStream.toByteArray();
            }
        });
    }

    private static byte[] decompress(final byte[] data) {
        return HFn.failOr(() -> {
            final Inflater inflater = new Inflater();
            inflater.setInput(data);

            try (final ByteArrayOutputStream outputStream = new ByteArrayOutputStream(
                data.length)) {
                final byte[] buffer = new byte[VValue.DFT.SIZE_BYTE_ARRAY];
                while (!inflater.finished()) {
                    final int count = inflater.inflate(buffer);
                    outputStream.write(buffer, 0, count);
                }
                return outputStream.toByteArray();
            }
        });
    }
}
