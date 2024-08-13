package example.mine;

import example.db.UTXO;
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

    private Blockchain blockchain;

    private List<Transaction> memPool = new ArrayList<>();

    public Miner(Blockchain blockchain) {
        this.blockchain = blockchain;
    }

    public PublicKey getWallet() {
        return minerWallet.getPublic();
    }

    public PrivateKey getPrivateKey() {
        return minerWallet.getPrivate();
    }

    public Block createBlock() {
        if (blockchain.size() == 0) {
            throw new IllegalStateException("Blockchain is empty");
        }
        Block result = new Block(blockchain.getTip().getHash(), memPool, minerWallet.getPublic());


        return result;
    }

    public void addTransaction(Transaction transaction) {
        memPool.add(transaction);
    }
    public void mine(Block block) {
        System.out.println("[1] Validating transactions... ");

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

//        System.out.println(block.getPrevHash().equals(Constants.GENESIS_PREV_HASH));
//        System.out.println(block.getTransactions().size() == 1);
//        System.out.println(block.getTransactions().size());
//        System.out.println(block.getTransactions().get(0).getAmount() == Constants.MINER_REWARD);
        return block.getPrevHash().equals(Constants.GENESIS_PREV_HASH) &&
                block.getTransactions().size() == 1 &&
                block.getTransactions().get(0).getAmount() == Constants.MINER_REWARD;
    }

    public void merge(Block block) {
        if (isBlock0(block)) {
            var tra = block.getTransactions().get(0);
            UTXO.storeUTXO(tra.getTransactionId(),tra.getOutputs().get(0));
            blockchain.add(block);
            System.out.println("Genesis block created!");
        } else {
            for (var transaction : block.getTransactions()) {
                for (var utxo: transaction.getOutputs()) {
                    UTXO.storeUTXO(transaction.getTransactionId(), utxo);
                }
            }
            blockchain.add(block);
        }
        memPool = new ArrayList<>();
    }






}
