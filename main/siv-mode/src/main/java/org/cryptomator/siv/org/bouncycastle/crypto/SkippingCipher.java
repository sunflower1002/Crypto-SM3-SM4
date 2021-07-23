//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.cryptomator.siv.org.bouncycastle.crypto;

public interface SkippingCipher {
    long skip(long var1);

    long seekTo(long var1);

    long getPosition();
}
