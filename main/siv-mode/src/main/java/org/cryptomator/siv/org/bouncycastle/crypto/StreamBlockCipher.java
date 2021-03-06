//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.cryptomator.siv.org.bouncycastle.crypto;

public abstract class StreamBlockCipher implements BlockCipher, StreamCipher {
    private final BlockCipher cipher;

    protected StreamBlockCipher(BlockCipher var1) {
        this.cipher = var1;
    }

    public BlockCipher getUnderlyingCipher() {
        return this.cipher;
    }

    public final byte returnByte(byte var1) {
        return this.calculateByte(var1);
    }

    public int processBytes(byte[] var1, int var2, int var3, byte[] var4, int var5) throws DataLengthException {
        if (var2 + var3 > var1.length) {
            throw new DataLengthException("input buffer too small");
        } else if (var5 + var3 > var4.length) {
            throw new OutputLengthException("output buffer too short");
        } else {
            int var6 = var2;
            int var7 = var2 + var3;

            for(int var8 = var5; var6 < var7; var4[var8++] = this.calculateByte(var1[var6++])) {
            }

            return var3;
        }
    }

    protected abstract byte calculateByte(byte var1);
}
