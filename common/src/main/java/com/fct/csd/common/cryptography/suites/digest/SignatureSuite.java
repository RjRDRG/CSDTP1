package com.fct.csd.common.cryptography.suites.digest;

import com.fct.csd.common.cryptography.config.ISuiteConfiguration;
import com.fct.csd.common.cryptography.config.ISuiteSpecification;
import com.fct.csd.common.cryptography.generators.key.AsymmetricKeyPairGenerator;
import com.fct.csd.common.cryptography.key.EncodedPublicKey;
import com.fct.csd.common.cryptography.key.KeyInfo;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.HashSet;
import java.util.Set;

public class SignatureSuite implements IDigestSuite {
	public enum Mode {
		Digest, Verify, Both
	}

	private Signature suite;
	private PublicKey publicKey;
	private PrivateKey privateKey;

	public SignatureSuite(ISuiteConfiguration config, Mode mode) throws Exception {
		String alg = config.getString("alg");
		String provider = config.getString("provider");
		if(provider != null)
			this.suite = Signature.getInstance(alg, provider);
		else
			this.suite = Signature.getInstance(alg);
		
		String keyAlias = config.getString("keyAlias");
		switch (mode) {
			case Digest:
				publicKey = null;
				privateKey = config.getPrivateKey(keyAlias);
				break;
			case Verify:
				publicKey = config.getCertificate(keyAlias).getPublicKey();
				privateKey = null;
				break;
			case Both:
				publicKey = config.getCertificate(keyAlias).getPublicKey();
				privateKey = config.getPrivateKey(keyAlias);
		}
	}
	
	public SignatureSuite(ISuiteSpecification spec) throws Exception {
		String alg = spec.getString("alg");
		String provider = spec.getString("provider");
		if(provider != null)
			this.suite = Signature.getInstance(alg, provider);
		else
			this.suite = Signature.getInstance(alg);
		
		AsymmetricKeyPairGenerator keyGen = new AsymmetricKeyPairGenerator(spec.getSubSpec("keyGenerator"));
		KeyPair keyPair = keyGen.generateKeyPair();
		
		privateKey = keyPair.getPrivate();
		publicKey = keyPair.getPublic();
	}
	
	public SignatureSuite(ISuiteSpecification spec, PublicKey pubKey) throws Exception {
		String alg = spec.getString("alg");
		String provider = spec.getString("provider");
		if(provider != null)
			this.suite = Signature.getInstance(alg, provider);
		else
			this.suite = Signature.getInstance(alg);
		
		publicKey = pubKey;
		privateKey = null;
	}

	@Override
	public byte[] digest(byte[] plainText) throws Exception {
		suite.initSign(privateKey);
		suite.update(plainText);
		return suite.sign();
	}

	@Override
	public boolean verify(byte[] data, byte[] signature) throws Exception {
		suite.initVerify(publicKey);
		suite.update(data);
		return suite.verify(signature);
	}
	
	public EncodedPublicKey getPublicKey() {
		return new EncodedPublicKey(publicKey);
	}

	public static Set<KeyInfo> requiredKeys(ISuiteSpecification spec, Mode mode) throws Exception {
		String alg = spec.getString("alg").split("/")[0];
		String keyAlias = spec.getString("keyAlias");
		Set<KeyInfo> keys = new HashSet<>(2);
		switch (mode) {
			case Digest:
				keys.add(new KeyInfo(keyAlias, alg, null, KeyInfo.Type.Private));
				break;
			case Verify:
				keys.add(new KeyInfo(keyAlias, alg, null, KeyInfo.Type.Public));
				break;
			case Both:
				keys.add(new KeyInfo(keyAlias, alg, null, KeyInfo.Type.Private));
				keys.add(new KeyInfo(keyAlias, alg, null, KeyInfo.Type.Public));
				break;
		}
		return keys;
	}


}
