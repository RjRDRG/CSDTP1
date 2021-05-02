package com.fct.csd.common.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class ByteLab {

	public static byte[] concat(byte[] first, byte[] second) {
		if(first == null) return second;
		if(second == null) return first;
		
		byte[] result = new byte[first.length + second.length];
		System.arraycopy(first, 0, result, 0, first.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	
	public static long bytesToInt(byte[] bytes) {
	     return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
	}
	
	public static byte[] intToBytes(long value) {
	    byte[] bytes = new byte[Integer.BYTES];
	    ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putLong(value);
	    return bytes;
	}
}
