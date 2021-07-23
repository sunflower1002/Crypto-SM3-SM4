//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.cryptomator.siv.org.bouncycastle.crypto.params;

import org.cryptomator.siv.org.bouncycastle.crypto.CipherParameters;

public class ParametersWithIV implements CipherParameters {
    private byte[] iv;
    private CipherParameters parameters;

    public ParametersWithIV(CipherParameters var1, byte[] var2) {
        this(var1, var2, 0, var2.length);
    }

    public ParametersWithIV(CipherParameters var1, byte[] var2, int var3, int var4) {
        this.iv = new byte[var4];
        this.parameters = var1;
        System.arraycopy(var2, var3, this.iv, 0, var4);
    }

    public byte[] getIV() {
        return this.iv;
    }

    public CipherParameters getParameters() {
        return this.parameters;
    }
}
