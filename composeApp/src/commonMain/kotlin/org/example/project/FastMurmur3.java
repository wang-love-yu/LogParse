package org.example.project;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * @author morning(李照发)
 * @date 2022-09-19 09:25
 */
public class FastMurmur3 {

    private static final int C1 = 0xcc9e2d51;
    private static final int C2 = 0x1b873593;
    private static final int DEFAULT_SEED = 0;

    public static int hash32(byte[] b, int seed) {
        final int len = b.length;

        int h1 = seed, i = 0, newLen = len & 0xfffffffc;
        while (i < newLen) {
            int k1 = (b[i++] & 0xff) | ((b[i++] & 0xff) << 8) | ((b[i++] & 0xff) << 16) | (b[i++] << 24);
            k1 *= C1;
            k1 = ((k1 << 15) | (k1 >>> 17)) * C2;

            h1 ^= k1;
            h1 = ((h1 << 13) | (h1 >>> 19)) * 5 + 0xe6546b64;
        }

        if (i != len) {
            int k1 = 0, j = 0;
            do {
                k1 ^= (b[i++] & 0xff) << j;
                j += 8;
            } while (i != len);

            k1 *= C1;
            k1 = ((k1 << 15) | (k1 >>> 17)) * C2;
            h1 ^= k1;
        }

        h1 ^= len;

        h1 ^= h1 >>> 16;
        h1 *= 0x85ebca6b;
        h1 ^= h1 >>> 13;
        h1 *= 0xc2b2ae35;
        h1 ^= h1 >>> 16;

        return h1;
    }

    public static int hash32(byte[] bytes) {
        return hash32(bytes, DEFAULT_SEED);
    }

    public static int hash32(String str) {
        return hash32(str.getBytes());
    }

    public static int hash32(String str, int seed) {
        return hash32(str.getBytes(), seed);
    }

/*    public static byte[] gzip(byte[] val) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(val.length);
        GZIPOutputStream gos = null;
        try {
            gos = new GZIPOutputStream(bos);
            gos.write(val, 0, val.length);
            gos.finish();
            gos.flush();
            bos.flush();
            val = bos.toByteArray();
        } finally {
            if (gos != null)
                gos.close();
            if (bos != null)
                bos.close();
        }
        return val;
    }*/
    public static byte[] compress(final byte[] input) throws IOException
    {
        try (ByteArrayOutputStream bout = new ByteArrayOutputStream();
             GZIPOutputStream gzipper = new GZIPOutputStream(bout))
        {
            gzipper.write(input, 0, input.length);
            gzipper.close();

            return bout.toByteArray();
        }
    }

    public static byte[] decompress(final byte[] input) throws IOException
    {
        try (ByteArrayInputStream bin = new ByteArrayInputStream(input);
             GZIPInputStream gzipper = new GZIPInputStream(bin))
        {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream out = new ByteArrayOutputStream();

            int len;
            while ((len = gzipper.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
            return out.toByteArray();
        }
    }
}