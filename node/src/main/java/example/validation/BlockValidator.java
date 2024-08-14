package example.validation;

import org.example.common.model.Block;
import org.example.common.model.Blockchain;
import org.example.common.model.Constants;
import org.example.common.model.Transaction;
import org.example.common.util.MerkleTree;
import org.example.common.util.Sha256;

public class BlockValidator {

    private final Block block;

    public BlockValidator(Block block) {
        this.block = block;
    }

//    public boolean validate() {
//        System.out.println("Validating block " + block);
//
//        boolean mr = validateMerkleRoot();
//        System.out.println("\tMerkle root: " + (mr ? "Valid" : "invalid"));
//        if (!mr) {
//            return false;
//        }
//
//
//        return validateMerkleRoot() && validateHash();
//    }
    public boolean validateMerkleRoot() {
        String merkleRoot = MerkleTree.getMerkleRoot(block.getTransactions());
        return merkleRoot.equals(block.getMerkleRoot());
    }

    public boolean validateHash() {
        return block.getHash().equals(generateHash());
    }
    private String generateHash() {
            String input = block.getPrevHash() +
                    block.getTimestamp() +
                    block.getNonce() +
                    block.getMerkleRoot();
            return  Sha256.hash(input);
    }

    public boolean validatePrevHash() {

        return block.getPrevHash().equals(Blockchain.getInstance().getTipHash());
    }

    public boolean validateReward() {

        int count = 0;

        for (Transaction tx: block.getTransactions()) {
            if (tx.getSender() == null) {
                count++;
            }
        }

        return count == 1;
    }
}
