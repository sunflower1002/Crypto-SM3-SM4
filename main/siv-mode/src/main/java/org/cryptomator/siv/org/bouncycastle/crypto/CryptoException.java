//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.cryptomator.siv.org.bouncycastle.crypto;

public class CryptoException extends Exception {
    private Throwable cause;

    public CryptoException() {
    }

    public CryptoException(String var1) {
        super(var1);
    }

    public CryptoException(String var1, Throwable var2) {
        super(var1);
        this.cause = var2;
    }

    public Throwable getCause() {
        return this.cause;
    }
}
