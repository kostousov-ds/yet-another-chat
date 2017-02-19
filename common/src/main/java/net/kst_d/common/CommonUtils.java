package net.kst_d.common;

import java.nio.ByteBuffer;

public class CommonUtils {
    private CommonUtils() {
    }

    protected static final char[] HEX_ARRAY = "0123456789abcdef".toCharArray();

    public static byte[] longToBytes(long x) {
	ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	buffer.putLong(x);
	return buffer.array();
    }

    public static long bytesToLong(byte[] bytes) {
	ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
	buffer.put(bytes);
	buffer.flip();//need flip
	return buffer.getLong();
    }

    private static String hex(byte b) {
	int v = b & 0xFF;
	return new String(new char[] {HEX_ARRAY[v >>> 4], HEX_ARRAY[v & 0x0F]});
    }

    public static String hex(byte[] bytes) {
	if (bytes == null) {
	    return null;
	}
	return hex(bytes, 0, bytes.length);
    }

    public static String hex(byte[] bytes, int from, int to) {
	if (from < 0) {
	    throw new ArrayIndexOutOfBoundsException(from);
	}
	if (to > bytes.length) {
	    throw new ArrayIndexOutOfBoundsException(to);
	}

	if (from == to) {
	    return "";
	}
	int j = 0;
	int len = to - from;
	char[] chars = new char[len * 2];
	for (int i = from; i < to; i++) {
	    int v = bytes[i] & 0xff;
	    chars[j * 2] = HEX_ARRAY[v >>> 4];
	    chars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
	    j++;
	}
	return new String(chars);
    }


}
