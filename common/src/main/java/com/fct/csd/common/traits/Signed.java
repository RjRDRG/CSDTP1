package com.fct.csd.common.traits;

import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;

import java.io.Serializable;

import static com.fct.csd.common.util.Serialization.bytesToData;
import static com.fct.csd.common.util.Serialization.dataToBytes;

public class Signed<T extends Serializable> implements Serializable {

	byte[] data;
	byte[] signature;

	public Signed(T data, IDigestSuite suite) throws Exception {
		this.data = dataToBytes(data);
		this.signature = suite.digest(this.data);
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
}
