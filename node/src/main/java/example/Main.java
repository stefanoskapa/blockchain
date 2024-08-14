package example;

import example.db.UtxoDB;
import example.mine.Miner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.common.model.Block;
import org.example.common.model.Blockchain;
import org.example.common.model.Transaction;
import org.example.common.model.Wallet;
import org.example.common.util.ECC;
import org.example.common.util.Sha256;


import java.security.PublicKey;
import java.security.Security;

public class Main {
    public static void main(String[] args) {

        Security.addProvider(new BouncyCastleProvider());

        Miner miner = new Miner(Blockchain.getInstance());


        var block0 = miner.createBlock();
        miner.mine(block0);
        miner.merge(block0);


        //send 2 coins from miner to me
        Wallet myWallet = new Wallet();
        double amount = 1;
        byte[] signature = ECC.sign(miner.getPrivateKey(), Sha256.hash(miner.getWallet().toString() + myWallet.getPublicKey().toString() + amount));
        var tran = new Transaction(miner.getWallet(),myWallet.getPublicKey(),amount,signature);
        miner.addTransaction(tran);

        var block1 = miner.createBlock();
        miner.mine(block1);
        miner.merge(block1);

        System.out.println(Blockchain.getInstance().toString());
    }
}