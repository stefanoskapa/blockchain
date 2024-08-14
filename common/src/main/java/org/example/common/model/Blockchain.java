package org.example.common.model;

import java.util.*;

public class Blockchain {

    private static Blockchain instance;

    private final LinkedList<Block> blockList;


    private Blockchain() {
        blockList = new LinkedList<>();
    }

    public static Blockchain getInstance() {
        if (instance == null)
            instance = new Blockchain();
        return instance;
    }

    public void add(Block block) {
        blockList.add(block);
    }

    public int size() {
        return blockList.size();
    }

    public Block getTip() {
        return blockList.getLast();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("--- BLOCKCHAIN START ---\n");
        for (int i = 0; i < blockList.size(); i++) {
            sb.append(i).append(" : ");
            sb.append(blockList.get(i).toString()).append("\n");
        }
        sb.append("--- BLOCKCHAIN END ---\n");
        return sb.toString();
    }
}
