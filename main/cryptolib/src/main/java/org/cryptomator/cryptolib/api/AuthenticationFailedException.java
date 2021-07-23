/*******************************************************************************
 * Copyright (c) 2016 Sebastian Stenzel and others.
 * This file is licensed under the terms of the MIT license.
 * See the LICENSE.txt file for more info.
 *
 * Contributors:
 *     Sebastian Stenzel - initial API and implementation
 *******************************************************************************/
package org.cryptomator.cryptolib.api;

public class AuthenticationFailedException extends CryptoException {

	public AuthenticationFailedException(String message) {
		super(message);
	}

	public AuthenticationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

}
