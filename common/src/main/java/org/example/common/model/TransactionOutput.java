package org.example.common.model;

import org.example.common.util.Sha256;

import java.security.PublicKey;

/**
 * The receiving wallet and the amount
 */
public class TransactionOutput {



    private PublicKey receiver;
    private double amount;



    public TransactionOutput(PublicKey receiver, double amount) {
        this.receiver = receiver;
        this.amount = amount;

    }


    public PublicKey getReceiver() {
        return receiver;
    }

    public void setReceiver(PublicKey receiver) {
        this.receiver = receiver;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "TransactionOutput{" +
                "receiver=" + receiver +
                ", amount=" + amount +
                '}';
    }
}
