package com.fct.csd.common.traits;

import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

import static com.fct.csd.common.util.Serialization.*;

public class Signed<T extends Serializable> implements Serializable {

	T data;
	byte[] signature;

	public Signed(T data, IDigestSuite suite) throws Exception {
		this.data = data;
		this.signature = suite.digest(dataToBytes(data));
	}

	public Signed(T data, byte[] signature) {
		this.data = data;
		this.signature = signature;
	}

	public boolean verify(IDigestSuite suite) throws Exception {
		return suite.verify(dataToBytes(data), signature);
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
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Signed<?> signed = (Signed<?>) o;
		return data.equals(signed.data) && Arrays.equals(signature, signed.signature);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(data);
		result = 31 * result + Arrays.hashCode(signature);
		return result;
	}

	@Override
	public String toString() {
		return "Signed{" +
				"data=" + data +
				", signature=" + bytesToString(signature) +
				'}';
	}
}
