package com.fct.csd.common.cryptography.generators.nonce;

import com.fct.csd.common.traits.Compactable;

public interface INonce extends Compactable {
	INonce increment();
	boolean prior(INonce other);
	String toString();
	String type();
}
