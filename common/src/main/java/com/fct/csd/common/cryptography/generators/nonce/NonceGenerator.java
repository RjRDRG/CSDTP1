package com.fct.csd.common.cryptography.generators.nonce;

public class NonceGenerator {

	public NonceGenerator() {
	}

	public Counter generateCounter() {
		return new Counter();
	}
}
