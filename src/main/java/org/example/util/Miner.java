package org.example.util;

import org.example.model.Block;
import org.example.model.Blockchain;
import org.example.model.Config;

public class Miner {


    public static void mine(Block block, Blockchain blockchain) {
        System.out.println("Mining...");
        while(!isGoldenHash(block.getHash())) {
            block.incrementNonce();
            block.generateHash();
        }

        System.out.println(block + " has been mined!");

        blockchain.add(block);
    }

    private static boolean isGoldenHash(String hash) {
        return hash.startsWith("0".repeat(Config.DIFFICULTY));
    }
}
