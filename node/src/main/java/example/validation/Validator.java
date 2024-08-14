package example.validation;

import example.db.UtxoDB;
import org.example.common.model.Block;
import org.example.common.model.Constants;
import org.example.common.model.Transaction;

import java.util.function.BooleanSupplier;
import java.util.function.Predicate;

public class Validator {
    private TxValidator txValidator;
    private BlockValidator blockValidator;

    private Block block;
    public Validator(Block block, UtxoDB db) {
        txValidator = new TxValidator(db);
        blockValidator = new BlockValidator(block);
        this.block = block;
    }


    public boolean validate() {


        System.out.println("\t- Validating Block Transactions");

        for (var transaction : block.getTransactions()) {

            if (!validateTx(transaction))
                return false;
        }

        System.out.println("\t- Validating Block");
        if (!validateBlock())
            return false;

        return true;
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
        System.out.println("\t\tValidating transaction " + transaction.getTransactionId());
        boolean isReward =transaction.getSender() == null && transaction.getAmount() == Constants.MINER_REWARD;

        if (!valHelper("Input ownership", 3, () -> txValidator.validateAllTransactionInputOwner(transaction)))
            return false;

        if (!isReward) {
            if (!valHelper("Input sufficiency", 3, () -> txValidator.validateTxInputSumAmounts(transaction)))
                return false;

            if (!valHelper("Input-Output balance", 3, () -> txValidator.validateTxIOBalance(transaction)))
                return false;
        }

        if (!valHelper("Transaction Hash", 3, () -> txValidator.validateTxHash(transaction)))
            return false;

        if (!isReward) {
            if (!valHelper("Transaction Signature", 3, () -> txValidator.validateTxSignature(transaction)))
                return false;
        }

        return true;

    }

    public boolean validateBlock() {
        if (!valHelper("Merkle Root", 2, () -> blockValidator.validateMerkleRoot()))
            return false;

        if (!valHelper("Block Hash", 2, () -> blockValidator.validateHash()))
            return false;

        if (!valHelper("Prev Hash", 2, () -> blockValidator.validatePrevHash()))
            return false;

        if (!valHelper("Reward", 2, () -> blockValidator.validateReward()))
            return false;
        return true;
    }

    private boolean valHelper(String subject, int indentation, BooleanSupplier supplier) {

        System.out.print("\t".repeat(indentation) + subject + ": ");
        boolean retValue = supplier.getAsBoolean();
        System.out.println(retValue ? "Valid" : "Invalid");
        return retValue;
    }
}
