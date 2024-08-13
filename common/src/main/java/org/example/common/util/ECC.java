package org.example.common.util;

import java.security.*;
import java.security.spec.ECGenParameterSpec;

public class ECC {

    public static byte[] sign(PrivateKey privateKey, String input) {
        Signature signature;
        byte[] output;

        try {
            signature = Signature.getInstance("ECDSA", "BC");
            signature.initSign(privateKey);
            signature.update(input.getBytes());
            output = signature.sign();
        } catch (Exception e) {
            throw new RuntimeException();
        }

        return output;
    }

    public static boolean verify(PublicKey publicKey, String data, byte[] signature){
        Signature ecdsaSignature;
        try {
            ecdsaSignature = Signature.getInstance("ECDSA", "BC");
            ecdsaSignature.initVerify(publicKey);
            ecdsaSignature.update(data.getBytes());
            return ecdsaSignature.verify(signature);
        } catch (Exception e) {
            throw new RuntimeException();
        }

    }

    public static KeyPair generateKeyPair() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("ECDSA", "BC");
            ECGenParameterSpec params = new ECGenParameterSpec("prime256v1");
            keyPairGenerator.initialize(params);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
