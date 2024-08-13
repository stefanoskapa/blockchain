package org.example.common.model;

import org.example.common.util.ECC;
import org.example.common.util.Sha256;

import java.io.Serializable;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;


public class Transaction implements Serializable {

    private final String transactionId;
    private final PublicKey sender;
    private final PublicKey receiver;
    private final double amount;
    private final byte[] signature;

    private List<TransactionInput> inputs = new ArrayList<>();
    private List<TransactionOutput> outputs = new ArrayList<>();

    public Transaction(PublicKey sender, PublicKey receiver, double amount, byte[] signature) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.signature = signature;
        this.transactionId = Sha256.doubleHash(sender == null ? "" : sender.toString() + receiver.toString() + amount);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PublicKey getSender() {
        return sender;
    }

    public PublicKey getReceiver() {
        return receiver;
    }

    public double getAmount() {
        return amount;
    }

    public byte[] getSignature() {
        return signature;
    }


    public List<TransactionInput> getInputs() {
        return inputs;
    }

    public void addInput(TransactionInput input) {
        this.inputs.add(input);
    }

    public List<TransactionOutput> getOutputs() {
        return outputs;
    }

    public void addOutput(TransactionOutput output) {
        this.outputs.add(output);
    }
}
