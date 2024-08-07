package org.example.model;

import java.util.LinkedList;

public class Blockchain {

    private final LinkedList<Block> blockchain;

    public Blockchain() {
        blockchain = new LinkedList<>();
    }

    public void add(Block block) {
        if (!blockchain.isEmpty() && !isValid(block)) {
            throw new IllegalArgumentException("Invalid Block");
        }

        blockchain.add(block);
    }

    private boolean isValid(Block block) {
        return  block.getPrevHash().equals(blockchain.getLast().getHash()) &&
                block.getHash().startsWith("0".repeat(Config.DIFFICULTY));
    }


    public int size() {
        return blockchain.size();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Block b : blockchain) {
            sb.append(b.toString());
        }
        return sb.toString();
    }
}
