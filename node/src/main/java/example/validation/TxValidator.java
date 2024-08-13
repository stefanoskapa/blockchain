package example.validation;

import example.db.UTXO;
import org.example.common.model.Transaction;
import org.example.common.model.TransactionInput;
import org.example.common.util.ECC;
import org.example.common.util.Sha256;

import java.security.PublicKey;

public class TxValidator {


    /**
     * Validates a transaction
     * @param transaction
     * @return
     */
    public boolean validateTx(Transaction transaction) {
        System.out.println("Validating transaction " + transaction.getTransactionId());

        boolean sig = validateTxSignature(transaction);
        System.out.println("\tSignature: " + sig);

        System.out.println("\tInputs");

        for (var input: transaction.getInputs()) {
            System.out.println("\t\tInput " + input.getHash());
            System.out.println("\t\t\tHash: " + validateTxInputHash(input));
            System.out.println("\t\t\tSignature: " + validateTxInputSignature(input, transaction));
            System.out.println("\t\t\tIndex exists: " + (UTXO.getUTXOs(input.getPrevTransactionId()).size() > input.getOutputIndex()));
        }

        System.out.println();

        return true;

    }

    /**
     * Checks if the transaction input has been hashed correctly
     */
    public boolean validateTxInputHash(TransactionInput input) {
        String hash = Sha256.hash(input.getPrevTransactionId() + input.getOutputIndex());
        return hash.equals(input.getHash());
    }

    /**
     * Checks if the transactionInput has been signed with the same
     * signature as the whole transaction
     * @param input a transactionInput
     * @param parent The parent-transaction
     * @return
     */
    public boolean validateTxInputSignature(TransactionInput input, Transaction parent) {
        PublicKey sender = parent.getSender();
        return ECC.verify(sender, input.getHash(), input.getSignature());
    }

    public boolean validateAllTxInputSignatures(Transaction transaction) {

        for (TransactionInput input : transaction.getInputs()) {
            if (!validateTxInputSignature(input, transaction)) {
                return false;
            }
        }

        return true;

    }


    public boolean validateTxSignature(Transaction transaction) {
        return ECC.verify(transaction.getSender(), transaction.getTransactionId(), transaction.getSignature());
    }


}
