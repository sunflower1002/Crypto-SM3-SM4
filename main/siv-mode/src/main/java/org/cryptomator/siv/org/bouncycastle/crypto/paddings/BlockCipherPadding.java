//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.cryptomator.siv.org.bouncycastle.crypto.paddings;

import java.security.SecureRandom;
import org.cryptomator.siv.org.bouncycastle.crypto.InvalidCipherTextException;

public interface BlockCipherPadding {
    void init(SecureRandom var1) throws IllegalArgumentException;

    String getPaddingName();

    int addPadding(byte[] var1, int var2);

    int padCount(byte[] var1) throws InvalidCipherTextException;
}
