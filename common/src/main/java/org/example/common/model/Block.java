package org.example.common.model;

import org.example.common.util.MerkleTree;
import org.example.common.util.Sha256;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block {
    private int nonce;
    private final long timestamp;
    private String hash;
    private final String prevHash;
    private final String merkleRoot;
    private final List<Transaction> transactions;

    public Block(String prevHash, List<Transaction> transactions, PublicKey miner) {
        this.prevHash = prevHash;
        this.transactions = transactions;
        addReward(miner);
        this.timestamp = new Date().getTime();
        this.merkleRoot = MerkleTree.getMerkleRoot(transactions);
    }

    /**
     * For block0 only
     * @param
     */
    public Block(PublicKey miner) {
        this.prevHash = Constants.GENESIS_PREV_HASH;
        this.transactions = new ArrayList<>();
        addReward(miner);
        this.timestamp = new Date().getTime();
        this.merkleRoot = MerkleTree.getMerkleRoot(transactions);
    }

    public void addReward(PublicKey miner) {
        Transaction reward = new Transaction(null, miner, Constants.MINER_REWARD, null);
        var transactionOutput = new TransactionOutput(miner, Constants.MINER_REWARD);
        reward.getOutputs().add(transactionOutput);
        transactions.add(reward);
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getNonce() {
        return nonce;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getMerkleRoot() {
        return merkleRoot;
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

    public List<Transaction> getTransactions() {
        return transactions;
    }

    @Override
    public String toString() {
        return "Block{" +
                "hash='" + hash + '\'' +
                ", nonce=" + nonce +
                ", timestamp=" + timestamp +

                ", prevHash='" + prevHash + '\'' +
                ", merkleRoot='" + merkleRoot + '\'' +
                '}';
    }
}
