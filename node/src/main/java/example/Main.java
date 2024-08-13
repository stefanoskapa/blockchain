package example;

import example.db.UTXO;
import example.mine.Miner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.common.model.Block;
import org.example.common.model.Blockchain;
import org.example.common.model.Transaction;
import org.example.common.model.Wallet;
import org.example.common.util.ECC;
import org.example.common.util.Sha256;
import org.w3c.dom.ls.LSOutput;


import java.security.PublicKey;
import java.security.Security;

public class Main {
    public static void main(String[] args) {

        Security.addProvider(new BouncyCastleProvider());

        var chain = Blockchain.getInstance();
        Miner miner = new Miner(chain);
        var genesis = new Block(miner.getWallet());

        miner.mine(genesis);
        miner.merge(genesis);

        System.out.println("Miner's balance: " + UTXO.getBalance(miner.getWallet()));


        Wallet myWallet = new Wallet();

        PublicKey minerPub = miner.getWallet();




        byte[] signature = ECC.sign(miner.getPrivateKey(), Sha256.hash(miner.getWallet().toString() + myWallet.getPublicKey().toString() + 2));
        var tran = new Transaction(miner.getWallet(),myWallet.getPublicKey(),2,signature);
        miner.addTransaction(tran);
        Block block1 = miner.createBlock();

        miner.mine(block1);
        miner.merge(block1);
        System.out.println("Miner's balance: " + UTXO.getBalance(miner.getWallet()));

        System.out.println(Blockchain.getInstance().toString());
    }
}