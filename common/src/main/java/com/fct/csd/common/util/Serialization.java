package com.fct.csd.common.util;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

import org.apache.commons.lang3.SerializationUtils;

public class Serialization {

	public static <T extends Serializable> byte[] dataToBytes(T data) {
		return SerializationUtils.serialize(data);
	}

	public static <T extends Serializable> T bytesToData(byte[] bytes) {
		return SerializationUtils.deserialize(bytes);
	}

	public static byte[] stringToBytes(String string) {
		return Base64.getDecoder().decode(string);
	}

	public static String bytesToString(byte[] data) {
		return Base64.getEncoder().encodeToString(data);
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
