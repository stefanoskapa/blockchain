package example.mine;

import example.db.UtxoDB;
import example.validation.TxValidator;
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

    public Block createBlock() {
        if (blockchain.size() == 0) {
            memPool = new ArrayList<>();
            return new Block(getWallet());
        }
        Block result = new Block(blockchain.getTip().getHash(), memPool, minerWallet.getPublic());

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
        System.out.println("Block mined: " + block);
    }

    public void addReward(Block block) {
        Transaction reward = new Transaction(null, minerWallet.getPublic(), Constants.MINER_REWARD, null);
        var transactionOutput = new TransactionOutput(minerWallet.getPublic(), Constants.MINER_REWARD);
        reward.getOutputs().add(transactionOutput);
        block.getTransactions().add(reward);
    }

    public String generateHash(Block block) {
        String hash = "";
        while (!hash.startsWith(HASH_PREFIX)) {
            String input = block.getPrevHash() +
                    block.getTimestamp() +
                    block.getNonce() +
                    block.getMerkleRoot();
            hash = Sha256.hash(input);
            block.incrementNonce();
        }
        return hash;
    }

    private boolean isBlock0(Block block) {

        return block.getPrevHash().equals(Constants.GENESIS_PREV_HASH) &&
                block.getTransactions().size() == 1 &&
                block.getTransactions().get(0).getAmount() == Constants.MINER_REWARD;
    }



    public boolean merge(Block block) {

        if (isBlock0(block)) {
            var tra = block.getTransactions().get(0);
            db.storeUTXO(tra.getTransactionId(),tra.getOutputs().get(0));
            blockchain.add(block);
            System.out.println("Genesis block created!");
        } else {
            System.out.println("[ Validating... ]");
            // validate all transactions
            for (var transaction : block.getTransactions()) {
                if (!txValidator.validateTx(transaction)) {
                    System.out.println("Invalid transaction: " + transaction.getTransactionId());
                    System.out.println("Merge failed!");
                    return false;
                }
            }
            // consume txInputs and store txOutputs
            for (var transaction : block.getTransactions()) {
                // consume
                for (var input: transaction.getInputs()) {
                    db.removeUTXO(input.getPrevTransactionId(),input.getOutputIndex());
                }
                // store
                for (var utxo: transaction.getOutputs()) {
                    db.storeUTXO(transaction.getTransactionId(), utxo);
                }
            }
            blockchain.add(block);
        }

        System.out.println("Block " + block.getHash() + " merged!");

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
