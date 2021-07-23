/*******************************************************************************
 * Copyright (c) 2016 Sebastian Stenzel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the accompanying LICENSE.txt.
 *
 * Contributors:
 *     Sebastian Stenzel - initial API and implementation
 *******************************************************************************/
package org.cryptomator.cryptolib.v2;

import org.cryptomator.cryptolib.api.AuthenticationFailedException;
import org.cryptomator.cryptolib.api.FileHeader;
import org.cryptomator.cryptolib.api.FileHeaderCryptor;
import org.cryptomator.cryptolib.common.CipherSupplier;

import javax.crypto.*;
import javax.crypto.spec.GCMParameterSpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;

import static org.cryptomator.cryptolib.v2.Constants.GCM_TAG_SIZE;

class FileHeaderCryptorImpl implements FileHeaderCryptor {

	private final SecretKey headerKey;
	private final SecureRandom random;

	FileHeaderCryptorImpl(SecretKey headerKey, SecureRandom random) {
		this.headerKey = headerKey;
		this.random = random;
	}

	@Override
	public FileHeader create() {
		byte[] nonce = new byte[FileHeaderImpl.NONCE_LEN];
		random.nextBytes(nonce);
		byte[] contentKey = new byte[FileHeaderImpl.Payload.CONTENT_KEY_LEN];
		random.nextBytes(contentKey);
		return new FileHeaderImpl(nonce, contentKey);
	}

	@Override
	public int headerSize() {
		return FileHeaderImpl.SIZE;
	}

	@Override
	public ByteBuffer encryptHeader(FileHeader header) {
		FileHeaderImpl headerImpl = FileHeaderImpl.cast(header);
		ByteBuffer payloadCleartextBuf = ByteBuffer.allocate(FileHeaderImpl.Payload.SIZE);
		payloadCleartextBuf.putLong(-1l);
		payloadCleartextBuf.put(headerImpl.getPayload().getContentKeyBytes());
		payloadCleartextBuf.flip();
		try {
			ByteBuffer result = ByteBuffer.allocate(FileHeaderImpl.SIZE);
			result.put(headerImpl.getNonce());

			// encrypt payload:
			Cipher cipher = CipherSupplier.SM4_GCM.forEncryption(headerKey, new GCMParameterSpec(GCM_TAG_SIZE * Byte.SIZE, headerImpl.getNonce()));
			int encrypted = cipher.doFinal(payloadCleartextBuf, result);
			assert encrypted == FileHeaderImpl.PAYLOAD_LEN + FileHeaderImpl.TAG_LEN;

			result.flip();
			return result;
		} catch (ShortBufferException e) {
			throw new IllegalStateException("Result buffer too small for encrypted header payload.", e);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new IllegalStateException("Unexpected exception during GCM encryption.", e);
		} finally {
			Arrays.fill(payloadCleartextBuf.array(), (byte) 0x00);
		}
	}

	@Override
	public FileHeader decryptHeader(ByteBuffer ciphertextHeaderBuf) throws AuthenticationFailedException {
		if (ciphertextHeaderBuf.remaining() < FileHeaderImpl.SIZE) {
			throw new IllegalArgumentException("Malformed ciphertext header");
		}
		ByteBuffer buf = ciphertextHeaderBuf.asReadOnlyBuffer();
		byte[] nonce = new byte[FileHeaderImpl.NONCE_LEN];
		buf.position(FileHeaderImpl.NONCE_POS);
		buf.get(nonce);
		byte[] ciphertextAndTag = new byte[FileHeaderImpl.PAYLOAD_LEN + FileHeaderImpl.TAG_LEN];
		buf.position(FileHeaderImpl.PAYLOAD_POS);
		buf.get(ciphertextAndTag);

		ByteBuffer payloadCleartextBuf = ByteBuffer.allocate(FileHeaderImpl.Payload.SIZE);
		try {
			// decrypt payload:
			Cipher cipher = CipherSupplier.SM4_GCM.forDecryption(headerKey, new GCMParameterSpec(GCM_TAG_SIZE * Byte.SIZE, nonce));
			int decrypted = cipher.doFinal(ByteBuffer.wrap(ciphertextAndTag), payloadCleartextBuf);
			assert decrypted == FileHeaderImpl.Payload.SIZE;

			payloadCleartextBuf.position(FileHeaderImpl.Payload.FILESIZE_POS);
			long fileSize = payloadCleartextBuf.getLong();
			payloadCleartextBuf.position(FileHeaderImpl.Payload.CONTENT_KEY_POS);
			byte[] contentKey = new byte[FileHeaderImpl.Payload.CONTENT_KEY_LEN];
			payloadCleartextBuf.get(contentKey);

			final FileHeaderImpl header = new FileHeaderImpl(nonce, contentKey);
			header.setFilesize(fileSize);
			return header;
		} catch (AEADBadTagException e) {
			throw new AuthenticationFailedException("Header tag mismatch.", e);
		} catch (ShortBufferException e) {
			throw new IllegalStateException("Result buffer too small for decrypted header payload.", e);
		} catch (IllegalBlockSizeException | BadPaddingException e) {
			throw new IllegalStateException("Unexpected exception during GCM decryption.", e);
		} finally {
			Arrays.fill(payloadCleartextBuf.array(), (byte) 0x00);
		}
	}

}
