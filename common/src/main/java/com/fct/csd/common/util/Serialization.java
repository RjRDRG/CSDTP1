package com.fct.csd.common.util;

import java.io.IOException;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.apache.commons.lang3.SerializationUtils;
import java.util.Base64;

public class Serialization {

	public static <T extends Serializable> byte[] dataToBytes(T data) {
		return SerializationUtils.serialize(data);
	}

	public static <T extends Serializable> T bytesToData(byte[] bytes) {
		return SerializationUtils.deserialize(bytes);
	}

	public static byte[] stringToBytes(String string) {
		return Base64.getUrlDecoder().decode(string);
	}

	public static String bytesToString(byte[] data) {
		return Base64.getUrlEncoder().encodeToString(data);
	}

	public static long bytesToInt(byte[] bytes) {
		return ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getLong();
	}

	public static byte[] intToBytes(long value) {
		byte[] bytes = new byte[Integer.BYTES];
		ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).putLong(value);
		return bytes;
	}

	public final static ObjectMapper jsonMapper = createObjectMapper();

	private static ObjectMapper createObjectMapper() {
		ObjectMapper om = new ObjectMapper();
		om.registerModule(new JavaTimeModule());
		SimpleModule module = new SimpleModule();
		module.addSerializer(byte[].class, new ByteSerializer());
		module.addDeserializer(byte[].class, new ByteDeserializer());
		om.registerModule(module);
		return om;
	}

	public static <T extends Serializable> String dataToJson(T data) throws JsonProcessingException {
		return jsonMapper.writeValueAsString(data);
	}

	public static <T extends Serializable> T jsonToData(byte[] bytes, Class<T> type) throws IOException {
		return jsonMapper.readValue(bytes, type);
	}
}
