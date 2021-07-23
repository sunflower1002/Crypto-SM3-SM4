/*******************************************************************************
 * Copyright (c) 2016 Sebastian Stenzel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the accompanying LICENSE.txt.
 *
 * Contributors:
 *     Sebastian Stenzel - initial API and implementation
 *******************************************************************************/
package org.cryptomator.cryptolib.common;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;
import java.security.spec.AlgorithmParameterSpec;

public final class CipherSupplier {

	public static final CipherSupplier SM4_CTR = new CipherSupplier("SM4/CTR/NoPadding");
	public static final CipherSupplier SM4_GCM = new CipherSupplier("SM4/GCM/NoPadding");
	public static final CipherSupplier RFC3394_KEYWRAP = new CipherSupplier("SM4");

	private final String cipherAlgorithm;
	private final ThreadLocal<Cipher> threadLocal;
	static {
		if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
			Security.addProvider(new BouncyCastleProvider());
		}
	}
	public CipherSupplier(String cipherAlgorithm) {
		this.cipherAlgorithm = cipherAlgorithm;
		this.threadLocal = new Provider();
		this.threadLocal.get(); // eagerly initialize to provoke exceptions
	}

	private class Provider extends ThreadLocal<Cipher> {
		@Override
		protected Cipher initialValue() {
			try {
				return Cipher.getInstance(cipherAlgorithm);
			} catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
				throw new IllegalArgumentException("Invalid cipher algorithm or padding.", e);
			}
		}
	}

	public Cipher forEncryption(SecretKey key, AlgorithmParameterSpec params) {
		return forMode(Cipher.ENCRYPT_MODE, key, params);
	}

	public Cipher forDecryption(SecretKey key, AlgorithmParameterSpec params) {
		return forMode(Cipher.DECRYPT_MODE, key, params);
	}

	public Cipher forWrapping(SecretKey kek) {
		return forMode(Cipher.WRAP_MODE, kek, null);
	}

	public Cipher forUnwrapping(SecretKey kek) {
		return forMode(Cipher.UNWRAP_MODE, kek, null);
	}

	// visible for testing
	Cipher forMode(int ciphermode, SecretKey key, AlgorithmParameterSpec params) {
		final Cipher cipher = threadLocal.get();
		try {
			cipher.init(ciphermode, key, params);
			return cipher;
		} catch (InvalidKeyException e) {
			throw new IllegalArgumentException("Invalid key.", e);
		} catch (InvalidAlgorithmParameterException e) {
			throw new IllegalArgumentException("Algorithm parameter not appropriate for " + cipher.getAlgorithm() + ".", e);
		}
	}
}