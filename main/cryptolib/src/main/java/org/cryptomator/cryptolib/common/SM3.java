package org.cryptomator.cryptolib.common;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;

public class SM3 {
    private SM3() {}
    /**
     * SM3 HashMac
     */
    public static class Mappings extends DigestAlgorithmProvider {
        private static final String PREFIX = SM3.class.getName();
        public Mappings() {}
        @Override
        public void configure(ConfigurableProvider provider) {
            addHMACAlgorithm(provider, "SM3", PREFIX + "$HashMac", PREFIX + "$KeyGenerator");
        }
    }

}
