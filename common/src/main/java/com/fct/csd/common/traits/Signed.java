package com.fct.csd.common.traits;

import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

import static com.fct.csd.common.util.Serialization.*;

public class Signed<T extends Serializable> implements Serializable {

	T data;
	byte[] signature;

	public Signed(T data, IDigestSuite suite) throws Exception {
		this.data = data;
		this.signature = suite.digest(dataToJson(data).getBytes(StandardCharsets.UTF_8));
	}

	public Signed(T data, byte[] signature) {
		this.data = data;
		this.signature = signature;
	}

	public boolean verify(IDigestSuite suite) throws Exception {
		return suite.verify(dataToJson(data).getBytes(StandardCharsets.UTF_8), signature);
	}

	public Signed() {
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public byte[] getSignature() {
		return signature;
	}

	public void setSignature(byte[] signature) {
		this.signature = signature;
	}

	@Override
	public String toString() {
		return "Signed{" +
				"data=" + data.toString() +
				", signature=" + bytesToString(signature) +
				'}';
	}
}
