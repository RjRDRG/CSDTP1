package com.fct.csd.common.traits;

import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;

import java.io.Serializable;

import static com.fct.csd.common.util.Serialization.*;

public class Signed<T extends Serializable> implements Serializable {

	byte[] data;
	byte[] signature;

	public Signed(T data, IDigestSuite suite) throws Exception {
		this.data = dataToBytes(data);
		this.signature = suite.digest(this.data);
	}

	public Signed(byte[] data, byte[] signature) {
		this.data = data;
		this.signature = signature;
	}

	public boolean verify(IDigestSuite suite) throws Exception {
		return suite.verify(data, signature);
	}

	public T extractData() {
		return bytesToData(data);
	}

	public Signed() {
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
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
		T value = bytesToData(this.data);
		return "Signed{" +
				"data=" + value.toString() +
				", signature=" + bytesToString(signature) +
				'}';
	}
}
