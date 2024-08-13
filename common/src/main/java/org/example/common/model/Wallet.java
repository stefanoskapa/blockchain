package org.example.common.model;

import org.example.common.util.ECC;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Wallet {

    // used for signature
    private PrivateKey privateKey;
    // used for verification
    private PublicKey publicKey;

    //address RIPMD public key (160 bits)

    public Wallet() {
        KeyPair keyPair = ECC.generateKeyPair();
        this.privateKey = keyPair.getPrivate();
        this.publicKey = keyPair.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
    }

    public PublicKey getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(PublicKey publicKey) {
        this.publicKey = publicKey;
    }
}
