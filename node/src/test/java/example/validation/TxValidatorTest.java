package example.validation;

import example.db.UtxoDB;
import example.mine.Miner;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.common.model.*;
import org.example.common.util.ECC;
import org.example.common.util.Sha256;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.security.Security;

import static org.junit.jupiter.api.Assertions.*;

class TxValidatorTest {

    private UtxoDB db;
    private TxValidator txValidator;

    @BeforeEach
    public void initCase() {
        db = new UtxoDB();
        txValidator = new TxValidator(db);
    }
    @BeforeAll
    public static void init() {
        Security.addProvider(new BouncyCastleProvider());
    }


    @Test
    public void validateTxInputSumAmounts_oneUtxoOneTransaction_moreThanEnough() {
        Wallet senderWallet = new Wallet();
        Wallet receiverWallet = new Wallet();

        TransactionOutput output1 = new TransactionOutput(senderWallet.getPublicKey(),10);
        db.storeUTXO(Sha256.hash("transaction1"), output1);

        // sign txInput with sender public key
        var txInput1 = new TransactionInput(Sha256.hash("transaction1"), 0);

        Transaction transaction = new Transaction(senderWallet.getPublicKey(), receiverWallet.getPublicKey(), 4,"doesn't matter here".getBytes());
        transaction.getInputs().add(txInput1);

        assertTrue(txValidator.validateTxInputSumAmounts(transaction));
    }

    @Test
    public void validateTxInputSumAmounts_oneUtxoOneTransaction_notEnough() {
        Wallet senderWallet = new Wallet();
        Wallet receiverWallet = new Wallet();

        TransactionOutput output1 = new TransactionOutput(senderWallet.getPublicKey(),3);
        db.storeUTXO(Sha256.hash("transaction1"), output1);

        // sign txInput with sender public key
        var txInput1 = new TransactionInput(Sha256.hash("transaction1"), 0);

        Transaction transaction = new Transaction(senderWallet.getPublicKey(), receiverWallet.getPublicKey(), 4,"doesn't matter here".getBytes());
        transaction.getInputs().add(txInput1);

        assertFalse(txValidator.validateTxInputSumAmounts(transaction));
    }

    @Test
    public void validateTxInputSumAmounts_oneUtxoOneTransaction_exactAmount() {
        Wallet senderWallet = new Wallet();
        Wallet receiverWallet = new Wallet();

        TransactionOutput output1 = new TransactionOutput(senderWallet.getPublicKey(),4);
        db.storeUTXO(Sha256.hash("transaction1"), output1);

        // sign txInput with sender public key
        var txInput1 = new TransactionInput(Sha256.hash("transaction1"), 0);

        Transaction transaction = new Transaction(senderWallet.getPublicKey(), receiverWallet.getPublicKey(), 4,"doesn't matter here".getBytes());
        transaction.getInputs().add(txInput1);

        assertTrue(txValidator.validateTxInputSumAmounts(transaction));
    }

    @Test
    public void validateTxInputSumAmounts_manyUtxoOneTransaction_exactAmount() {
        Wallet senderWallet = new Wallet();
        Wallet receiverWallet = new Wallet();

        TransactionOutput output1 = new TransactionOutput(senderWallet.getPublicKey(),1);
        db.storeUTXO(Sha256.hash("transaction1"), output1);
        TransactionOutput output2 = new TransactionOutput(senderWallet.getPublicKey(),1);
        db.storeUTXO(Sha256.hash("transaction1"), output2);
        TransactionOutput output3 = new TransactionOutput(senderWallet.getPublicKey(),1);
        db.storeUTXO(Sha256.hash("transaction1"), output3);
        TransactionOutput output4 = new TransactionOutput(senderWallet.getPublicKey(),1);
        db.storeUTXO(Sha256.hash("transaction1"), output4);


        // sign txInput with sender public key
        var txInput1 = new TransactionInput(Sha256.hash("transaction1"), 0);
        var txInput2 = new TransactionInput(Sha256.hash("transaction1"), 1);
        var txInput3 = new TransactionInput(Sha256.hash("transaction1"), 2);
        var txInput4 = new TransactionInput(Sha256.hash("transaction1"), 3);

        Transaction transaction = new Transaction(senderWallet.getPublicKey(), receiverWallet.getPublicKey(), 4,"doesn't matter here".getBytes());
        transaction.getInputs().add(txInput1);
        transaction.getInputs().add(txInput2);
        transaction.getInputs().add(txInput3);
        transaction.getInputs().add(txInput4);

        assertTrue(txValidator.validateTxInputSumAmounts(transaction));
    }

    @Test
    public void validateTxInputSumAmounts_manyUtxoOneTransaction_less() {
        Wallet senderWallet = new Wallet();
        Wallet receiverWallet = new Wallet();

        TransactionOutput output1 = new TransactionOutput(senderWallet.getPublicKey(),1);
        db.storeUTXO(Sha256.hash("transaction1"), output1);
        TransactionOutput output2 = new TransactionOutput(senderWallet.getPublicKey(),1);
        db.storeUTXO(Sha256.hash("transaction1"), output2);
        TransactionOutput output3 = new TransactionOutput(senderWallet.getPublicKey(),1);
        db.storeUTXO(Sha256.hash("transaction1"), output3);
        TransactionOutput output4 = new TransactionOutput(senderWallet.getPublicKey(),0.5);
        db.storeUTXO(Sha256.hash("transaction1"), output4);


        // sign txInput with sender public key
        var txInput1 = new TransactionInput(Sha256.hash("transaction1"), 0);
        var txInput2 = new TransactionInput(Sha256.hash("transaction1"), 1);
        var txInput3 = new TransactionInput(Sha256.hash("transaction1"), 2);
        var txInput4 = new TransactionInput(Sha256.hash("transaction1"), 3);

        Transaction transaction = new Transaction(senderWallet.getPublicKey(), receiverWallet.getPublicKey(), 4,"doesn't matter here".getBytes());
        transaction.getInputs().add(txInput1);
        transaction.getInputs().add(txInput2);
        transaction.getInputs().add(txInput3);
        transaction.getInputs().add(txInput4);

        assertFalse(txValidator.validateTxInputSumAmounts(transaction));
    }

    @Test
    public void validateTxInputSumAmounts_manyUtxoOneTransaction_more() {
        Wallet senderWallet = new Wallet();
        Wallet receiverWallet = new Wallet();

        TransactionOutput output1 = new TransactionOutput(senderWallet.getPublicKey(),1);
        db.storeUTXO(Sha256.hash("transaction1"), output1);
        TransactionOutput output2 = new TransactionOutput(senderWallet.getPublicKey(),1);
        db.storeUTXO(Sha256.hash("transaction1"), output2);
        TransactionOutput output3 = new TransactionOutput(senderWallet.getPublicKey(),1);
        db.storeUTXO(Sha256.hash("transaction1"), output3);
        TransactionOutput output4 = new TransactionOutput(senderWallet.getPublicKey(),1.5);
        db.storeUTXO(Sha256.hash("transaction1"), output4);


        // sign txInput with sender public key
        var txInput1 = new TransactionInput(Sha256.hash("transaction1"), 0);
        var txInput2 = new TransactionInput(Sha256.hash("transaction1"), 1);
        var txInput3 = new TransactionInput(Sha256.hash("transaction1"), 2);
        var txInput4 = new TransactionInput(Sha256.hash("transaction1"), 3);

        Transaction transaction = new Transaction(senderWallet.getPublicKey(), receiverWallet.getPublicKey(), 4,"doesn't matter here".getBytes());
        transaction.getInputs().add(txInput1);
        transaction.getInputs().add(txInput2);
        transaction.getInputs().add(txInput3);
        transaction.getInputs().add(txInput4);

        assertTrue(txValidator.validateTxInputSumAmounts(transaction));
    }

    @Test
    public void validateTxInputSumAmounts_oneUtxoPerTransaction_exact() {
        Wallet senderWallet = new Wallet();
        Wallet receiverWallet = new Wallet();

        TransactionOutput output1 = new TransactionOutput(senderWallet.getPublicKey(),2);
        db.storeUTXO(Sha256.hash("transaction1"), output1);
        TransactionOutput output2 = new TransactionOutput(senderWallet.getPublicKey(),2);
        db.storeUTXO(Sha256.hash("transaction2"), output2);



        // sign txInput with sender public key
        var txInput1 = new TransactionInput(Sha256.hash("transaction1"), 0);
        var txInput2 = new TransactionInput(Sha256.hash("transaction2"), 0);


        Transaction transaction = new Transaction(senderWallet.getPublicKey(), receiverWallet.getPublicKey(), 4,"doesn't matter here".getBytes());
        transaction.getInputs().add(txInput1);
        transaction.getInputs().add(txInput2);


        assertTrue(txValidator.validateTxInputSumAmounts(transaction));
    }

    @Test
    public void validateTxInputSumAmounts_oneUtxoPerTransaction_less() {
        Wallet senderWallet = new Wallet();
        Wallet receiverWallet = new Wallet();

        TransactionOutput output1 = new TransactionOutput(senderWallet.getPublicKey(),0.9);
        db.storeUTXO(Sha256.hash("transaction1"), output1);
        TransactionOutput output2 = new TransactionOutput(senderWallet.getPublicKey(),2);
        db.storeUTXO(Sha256.hash("transaction2"), output2);



        // sign txInput with sender public key
        var txInput1 = new TransactionInput(Sha256.hash("transaction1"), 0);
        var txInput2 = new TransactionInput(Sha256.hash("transaction2"), 0);


        Transaction transaction = new Transaction(senderWallet.getPublicKey(), receiverWallet.getPublicKey(), 4,"doesn't matter here".getBytes());
        transaction.getInputs().add(txInput1);
        transaction.getInputs().add(txInput2);

        assertFalse(txValidator.validateTxInputSumAmounts(transaction));
    }

    @Test
    public void validateTxInputSumAmounts_oneUtxoPerTransaction_more() {
        Wallet senderWallet = new Wallet();
        Wallet receiverWallet = new Wallet();

        TransactionOutput output1 = new TransactionOutput(senderWallet.getPublicKey(),5);
        db.storeUTXO(Sha256.hash("transaction1"), output1);
        TransactionOutput output2 = new TransactionOutput(senderWallet.getPublicKey(),2);
        db.storeUTXO(Sha256.hash("transaction2"), output2);



        // sign txInput with sender public key
        var txInput1 = new TransactionInput(Sha256.hash("transaction1"), 0);
        var txInput2 = new TransactionInput(Sha256.hash("transaction2"), 0);


        Transaction transaction = new Transaction(senderWallet.getPublicKey(), receiverWallet.getPublicKey(), 4,"doesn't matter here".getBytes());
        transaction.getInputs().add(txInput1);
        transaction.getInputs().add(txInput2);


        assertTrue(txValidator.validateTxInputSumAmounts(transaction));
    }


}