package org.example.common.model;


import org.example.common.util.Sha256;

/**
 * The transactionID and the index of the UTXO that is
 * being consumed.
 *
 * In order to consume a UTXO we must make sure it
 * belongs to the spender's wallet. So we need
 * the transactionInput to be signed by the
 * Transaction sender

 */
public class TransactionInput {


    private String hash;
    private final String prevTransactionId;

    private final int outputIndex;

    private byte[] signature;

    public TransactionInput(String transactionOutputId, int outputIndex) {
        this.prevTransactionId = transactionOutputId;
        this.outputIndex = outputIndex;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getOutputIndex() {
        return outputIndex;
    }

    public byte[] getSignature() {
        return signature;
    }

    public String getPrevTransactionId() {
        return prevTransactionId;
    }
}
