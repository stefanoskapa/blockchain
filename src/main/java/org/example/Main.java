package org.example;

import org.example.model.Block;
import org.example.model.Blockchain;
import org.example.model.Config;
import org.example.util.Miner;

public class Main {
    public static void main(String[] args) {
        var blockchain = new Blockchain();

        var block0 = new Block(0, Config.GENESIS_PREV_HASH, "");
        Miner.mine(block0, blockchain);

        var block1 = new Block(1,block0.getHash(), "foo bar");
        Miner.mine(block1, blockchain);

        System.out.println(blockchain);

    }
}