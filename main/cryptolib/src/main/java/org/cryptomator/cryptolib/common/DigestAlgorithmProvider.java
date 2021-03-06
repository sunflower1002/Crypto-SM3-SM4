package org.cryptomator.cryptolib.common;

import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AlgorithmProvider;

abstract class DigestAlgorithmProvider
        extends AlgorithmProvider
{
    protected void addHMACAlgorithm(
            ConfigurableProvider provider,
            String algorithm,
            String algorithmClassName,
            String keyGeneratorClassName)
    {
        String mainName = "HMAC" + algorithm;

        provider.addAlgorithm("mac." + mainName, algorithmClassName);
        provider.addAlgorithm("Alg.Alias.mac.HMAC-" + algorithm, mainName);
        provider.addAlgorithm("Alg.Alias.mac.HMAC/" + algorithm, mainName);
        provider.addAlgorithm("keyGenerator." + mainName, keyGeneratorClassName);
        provider.addAlgorithm("Alg.Alias.keyGenerator.HMAC-" + algorithm, mainName);
        provider.addAlgorithm("Alg.Alias.keyGenerator.HMAC/" + algorithm, mainName);
    }

    protected void addHMACAlias(
            ConfigurableProvider provider,
            String algorithm,
            ASN1ObjectIdentifier oid)
    {
        String mainName = "HMAC" + algorithm;

        provider.addAlgorithm("Alg.Alias.Mac." + oid, mainName);
        provider.addAlgorithm("Alg.Alias.KeyGenerator." + oid, mainName);
    }
}
