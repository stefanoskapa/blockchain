package org.example.model;

import org.example.util.Sha512;

import java.util.Date;

public class Block {
    private final int id;
    private int nonce;
    private final long timestamp;
    private String hash;
    private final String prevHash;
    private final String transaction;

    public Block(int id, String prevHash, String transaction) {
        this.id = id;
        this.prevHash = prevHash;
        this.transaction = transaction;
        this.timestamp = new Date().getTime();
        generateHash();
    }

    public void generateHash() {
        String input = id +
                prevHash +
                timestamp +
                nonce +
                transaction;
        this.hash =  Sha512.hash(input);
    }

    public void incrementNonce() {
        nonce++;
    }

    public String getHash() {
        return hash;
    }


    public String getPrevHash() {
        return prevHash;
    }


    @Override
    public String toString() {
        return "Block{" +
                "id=" + id +
                ", hash='" + hash + '\'' +
                ", prevHash='" + prevHash + '\'' +
                ", transaction='" + transaction + '\'' +
                '}';
    }
}
