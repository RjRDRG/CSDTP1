package com.fct.csd.common.traits;

import java.io.Serializable;

import org.apache.commons.lang3.SerializationUtils;

public interface Compactable extends Serializable {
	
	default byte[] compact() {
		return SerializationUtils.serialize(this);
	}
	
	static <T> T decompact(byte[] bytes) {
		return SerializationUtils.deserialize(bytes);
	}
}
