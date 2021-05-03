package com.fct.csd.common.traits;

import com.fct.csd.common.cryptography.suites.digest.IDigestSuite;

import java.io.Serializable;

import static com.fct.csd.common.util.Serialization.dataToBytes;

public class Signed<T extends Serializable> implements Serializable {
	private static final long serialVersionUID = -7492394231279641146L;

	T data;
	byte[] signature;

	public Signed(T data, IDigestSuite suite) throws Exception {
		this.data = data;
		this.signature = suite.digest(dataToBytes(data));
	}

	public boolean verify(IDigestSuite suite) throws Exception {
		return suite.verify(dataToBytes(data), signature);
	}

	Signed() {
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
}
