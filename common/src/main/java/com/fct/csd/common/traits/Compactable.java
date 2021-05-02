package com.fct.csd.common.traits;

import java.io.Serializable;
import java.util.Base64;

import org.apache.commons.lang3.SerializationUtils;

public interface Compactable extends Serializable {
	
	default byte[] compact() {
		return SerializationUtils.serialize(this);
	}
	
	static <T extends Serializable> T decompact(byte[] bytes) {
		return SerializationUtils.deserialize(bytes);
	}
	
	default String stringify() {
		return Base64.getEncoder().encodeToString(this.compact());
	}
	
	static <T extends Serializable> T unstringify(String string) {
		return decompact(Base64.getDecoder().decode(string));
 	}
}
