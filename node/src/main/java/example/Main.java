package example;

import example.mine.Miner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.common.model.Blockchain;
import org.example.common.model.Transaction;
import org.example.common.model.Wallet;
import org.example.common.util.ECC;
import org.example.common.util.Sha256;


import java.security.Security;

public class Main {
    public static void main(String[] args) {

        Security.addProvider(new BouncyCastleProvider());

        Miner miner = new Miner(Blockchain.getInstance());
        miner.commit();

        //send 2 coins from miner to me
        Wallet myWallet = new Wallet();
        double amount = 1;
        byte[] signature = ECC.sign(miner.getPrivateKey(), Sha256.hash(miner.getWallet().toString() + myWallet.getPublicKey().toString() + amount));
        var tran = new Transaction(miner.getWallet(),myWallet.getPublicKey(),amount,signature);
        miner.addTransaction(tran);

        miner.commit();

        System.out.println(Blockchain.getInstance().toString());
    }
}