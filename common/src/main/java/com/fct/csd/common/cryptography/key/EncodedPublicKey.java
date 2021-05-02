package com.fct.csd.common.cryptography.key;

import com.fct.csd.common.traits.Compactable;

import java.security.Key;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Objects;

public class EncodedPublicKey implements Compactable {
	private static final long serialVersionUID = -1440213254532977043L;
	
	byte[] enconded;
	String alg;

	public EncodedPublicKey(PublicKey key) {
		this.alg = key.getAlgorithm();
		this.enconded = key.getEncoded();
	}
	
	public PublicKey toPublicKey() throws Exception {
		return KeyFactory.getInstance(alg).generatePublic(new X509EncodedKeySpec(enconded));
	}
	
	EncodedPublicKey() {
	}

	public String getAlg() {
		return alg;
	}

	public void setAlg(String alg) {
		this.alg = alg;
	}
	
	public byte[] getEnconded() {
		return enconded;
	}

	public void setEnconded(byte[] enconded) {
		this.enconded = enconded;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		EncodedPublicKey that = (EncodedPublicKey) o;
		return Arrays.equals(enconded, that.enconded) && alg.equals(that.alg);
	}

	@Override
	public int hashCode() {
		int result = Objects.hash(alg);
		result = 31 * result + Arrays.hashCode(enconded);
		return result;
	}

	@Override
	public String toString() {
		return "EncodedPublicKey{" +
				"enconded=" + Arrays.toString(enconded) +
				", alg='" + alg + '\'' +
				'}';
	}
}
