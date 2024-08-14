package example.mine;

import example.db.UtxoDB;
import example.validation.TxValidator;
import example.validation.Validator;
import org.example.common.model.*;
import org.example.common.util.ECC;
import org.example.common.util.Sha256;

import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class Miner {

    private static final String HASH_PREFIX = "0".repeat(Constants.DIFFICULTY);

    private final KeyPair minerWallet = ECC.generateKeyPair();

    private final Blockchain blockchain;
    private final UtxoDB db;

    private final TxValidator txValidator;

    private List<Transaction> memPool = new ArrayList<>();

    public Miner(Blockchain blockchain) {
        this.blockchain = blockchain;
        this.db = new UtxoDB();
        this.txValidator = new TxValidator(db);
    }

    public double getWalletBalance(PublicKey publicKey) {
        return db.getBalance(publicKey);
    }

    public PublicKey getWallet() {
        return minerWallet.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return minerWallet.getPrivate();
    }

    /**
     * Executes all phases (creating, mining, validating, merging)
     */
    public void commit() {
        var block = createBlock();
        mine(block);
        validate(block);
        merge(block);
    }
    public Block createBlock() {
        System.out.println("\n[ Creating... ]");
        if (blockchain.size() == 0) {
            memPool = new ArrayList<>();
            return new Block(getWallet());
        }
        Block result = new Block(blockchain.getTipHash(), memPool, minerWallet.getPublic());

        for (Transaction t : result.getTransactions()) {
            generateInputsAndOutputs(t);
        }
        return result;
    }

    public void addTransaction(Transaction transaction) {
        memPool.add(transaction);
    }

    public void mine(Block block) {
        System.out.println("[ Mining... ]");
        block.setHash(generateHash(block));
        System.out.println("\tBlock mined: " + block.getHash());
    }


    public String generateHash(Block block) {
        String hash = "";
        while (!hash.startsWith(HASH_PREFIX)) {
            block.incrementNonce();
            String input = block.getPrevHash() +
                    block.getTimestamp() +
                    block.getNonce() +
                    block.getMerkleRoot();
            hash = Sha256.hash(input);

        }
        return hash;
    }


    public boolean validate(Block block) {
        System.out.println("[ Validating... ]");
        Validator validator = new Validator(block, db);
        if (!validator.validate()) {
            System.out.println("\tInvalid Block");
            return false;
        }
        System.out.println("\tBlock is valid");
        return true;
    }
    public boolean merge(Block block) {

        System.out.println("[ Merging... ]");
            // consume txInputs and store txOutputs
            for (var transaction : block.getTransactions()) {
                // consume
                for (var input : transaction.getInputs()) {
                    db.removeUTXO(input.getPrevTransactionId(), input.getOutputIndex());
                }
                // store
                for (var utxo : transaction.getOutputs()) {
                    db.storeUTXO(transaction.getTransactionId(), utxo);
                }
            }
            blockchain.add(block);

        System.out.println("\tBlock " + block.getHash() + " merged!");

        return true;
    }

    private void generateInputsAndOutputs(Transaction transaction) {
        List<TransactionInput> necessaryInputs = db.getTxInputsForWalletForAmount(transaction.getSender(), transaction.getAmount());
        if (transaction.getSender() == null || necessaryInputs == null) {
            return;
        }


        transaction.getInputs().addAll(necessaryInputs);
        double sum = 0;
        for (var input : necessaryInputs) {
            sum += db.getTransactionInputAmount(input);
        }

        double change = sum - transaction.getAmount();

        var toReceiver = new TransactionOutput(transaction.getReceiver(), transaction.getAmount());
        var toSender = new TransactionOutput(transaction.getSender(), change);

        transaction.getOutputs().add(toReceiver);
        transaction.getOutputs().add(toSender);
    }


}
