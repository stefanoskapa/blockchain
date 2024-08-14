package example.validation;

import example.db.UtxoDB;
import org.example.common.model.Constants;
import org.example.common.model.Transaction;
import org.example.common.model.TransactionInput;
import org.example.common.model.TransactionOutput;
import org.example.common.util.ECC;
import org.example.common.util.Sha256;

import java.security.PublicKey;

public class TxValidator {

    private UtxoDB db;
    public TxValidator(UtxoDB db) {
        this.db = db;
    }




    public boolean validateTxHash(Transaction transaction) {
        String data = (transaction.getSender() == null ? "" : transaction.getSender()) + transaction.getReceiver().toString() + transaction.getAmount();
        return Sha256.hash(data).equals(transaction.getTransactionId());
    }


    public boolean validateAllTransactionInputOwner(Transaction transaction) {
        for (TransactionInput ti: transaction.getInputs()) {
            if (!db.txInputBelongsToWallet(ti,transaction.getSender())) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the sum of transactionOutputs is equal
       to the sum
     * of transactionInputs
     * @return
     */
    public boolean validateTxIOBalance(Transaction transaction) {
        if (transaction.getSender() == null && transaction.getAmount() == Constants.MINER_REWARD) {
            return true;
        }

        double inputSum = 0;
        for (TransactionInput input : transaction.getInputs()) {
            double amount = db.getTransactionInputAmount(input);
            if (amount == -1) {
                return false;
            }
            inputSum += amount;
        }

        double outputSum = 0;
        for (TransactionOutput output: transaction.getOutputs()) {
            double amount = output.getAmount();
            outputSum += amount;
        }

        return inputSum == outputSum;


    }


    public boolean validateTxSignature(Transaction transaction) {

        String data = Sha256.hash(transaction.getSender().toString() +
                transaction.getReceiver().toString() + transaction.getAmount());

        return ECC.verify(transaction.getSender(),
                data,
                transaction.getSignature());
    }

    /**
     * Checks whether the sum of transactionInputs is equal
     * or greater than the transaction amount
     */
    public boolean validateTxInputSumAmounts(Transaction transaction) {
        if (transaction.getSender() == null && transaction.getAmount() == Constants.MINER_REWARD) {
            return true;
        }
        double sum = 0;
        for (TransactionInput input : transaction.getInputs()) {
            double amount = db.getTransactionInputAmount(input);
            if (amount == -1) {
                return false;
            }
            sum += amount;
            if (sum >= transaction.getAmount()) {
                return true;
            }

        }

        return false;
    }


}
