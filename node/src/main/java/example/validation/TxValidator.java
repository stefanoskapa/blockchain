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
    /**
     * Validates a transaction as a whole.
     *
     * 1. Check if the txInputs all belong to the sender
     * 2. Check if the txInputs add up to an equal or greater Transaction amount
     * 3. Check if the sum of txOutputs is equal to the sum of txInputs
     * 4. Check if the sum of txOutputs is equal or greater than the transaction amount (redundant)
     * 7. Check if transaction has been signed by the sender
     * 8. Check if transaction has been hashed correctly
     *
     * @param transaction The transaction to validate
     * @return True if transaction is valid, otherwise false
     */
    public boolean validateTx(Transaction transaction) {
        System.out.println("Validating transaction " + transaction.getTransactionId());

        boolean doesSenderOwnInputs = validateAllTransactionInputOwner(transaction);
        System.out.println("\tInput ownership: " + (doesSenderOwnInputs ? "Valid" : "Invalid"));
        if (!doesSenderOwnInputs) {
            return false;
        }

        if (!(transaction.getSender() == null && transaction.getAmount() == Constants.MINER_REWARD)) {

            boolean isAmountEnough = validateTxInputSumAmounts(transaction);
            System.out.println("\tInput sufficiency: " + (isAmountEnough ? "Valid" : "Invalid"));
            if (!isAmountEnough) {
                transaction.getInputs().forEach(System.out::println);
                transaction.getOutputs().forEach(System.out::println);
                return false;
            }

            boolean ioBalance = validateTxIOBalance(transaction);
            System.out.println("\ttxInput-txOutput balance: " + (ioBalance ? "Valid" : "invalid"));
            if (!ioBalance) {
                return false;
            }
        }

        boolean isValidHash = validateTxHash(transaction);
        System.out.println("\tTransaction Hash: " + (isValidHash ? "Valid" : "Invalid"));
        if (!isValidHash) {
            return false;
        }

        if (!(transaction.getSender() == null && transaction.getAmount() == Constants.MINER_REWARD)) {
            boolean sig = validateTxSignature(transaction);
            System.out.println("\tTransaction Signature: " + (sig ? "Valid" : "Invalid"));
            if (!sig) {
                return false;
            }

        }

        return true;

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
