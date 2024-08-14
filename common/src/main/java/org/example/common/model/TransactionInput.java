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


    private final String prevTransactionId;

    private final int outputIndex;


    public TransactionInput(String transactionId, int outputIndex) {
        this.prevTransactionId = transactionId;
        this.outputIndex = outputIndex;
    }




    public int getOutputIndex() {
        return outputIndex;
    }



    public String getPrevTransactionId() {
        return prevTransactionId;
    }

    @Override
    public String toString() {
        return "TransactionInput{" +
                "prevTransactionId='" + prevTransactionId + '\'' +
                ", outputIndex=" + outputIndex +
                '}';
    }
}
