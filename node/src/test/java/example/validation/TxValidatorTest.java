package example.validation;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.example.common.model.Transaction;
import org.example.common.model.TransactionInput;
import org.example.common.model.Wallet;
import org.example.common.util.ECC;
import org.example.common.util.Sha256;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.security.Security;

import static org.junit.jupiter.api.Assertions.*;

class TxValidatorTest {

    TxValidator txValidator = new TxValidator();

    @BeforeAll
    public static void init() {
        Security.addProvider(new BouncyCastleProvider());
    }


    @Test
    public void validateTXInputHash_correctHash() {
        var txInput = new TransactionInput(Sha256.hash("test"), 4);
        txInput.setHash(Sha256.hash(txInput.getPrevTransactionId() + txInput.getOutputIndex()));
        assertTrue(txValidator.validateTxInputHash(txInput));
    }

    @Test
    public void validateTXInputHash_wrongHash() {
        var txInput = new TransactionInput(Sha256.hash("test"), 4);
        txInput.setHash(Sha256.hash("arbitrary data"));
        assertFalse(txValidator.validateTxInputHash(txInput));
    }

    @Test
    public void validateTxInputSignature_matchingSignatures() {
        Wallet senderWallet = new Wallet();
        Wallet receiverWallet = new Wallet();

        // sign txInput with sender public key
        var txInput = new TransactionInput(Sha256.hash("test"), 4);
        txInput.setHash(Sha256.hash(txInput.getPrevTransactionId() + txInput.getOutputIndex()));
        byte[] signature = ECC.sign(senderWallet.getPrivateKey(), txInput.getHash());
        txInput.setSignature(signature);

        Transaction transaction = new Transaction(senderWallet.getPublicKey(), receiverWallet.getPublicKey(), 4,"doesn't matter here".getBytes());
        transaction.getInputs().add(txInput);

        assertTrue(txValidator.validateTxInputSignature(txInput, transaction));
    }

    @Test
    public void validateTxInputSignature_differentSignatures() {
        Wallet villainWallet = new Wallet();
        Wallet senderWallet = new Wallet();
        Wallet receiverWallet = new Wallet();

        // sign txInput with sender public key
        var txInput = new TransactionInput(Sha256.hash("test"), 4);
        txInput.setHash(Sha256.hash(txInput.getPrevTransactionId() + txInput.getOutputIndex()));
        byte[] signature = ECC.sign(villainWallet.getPrivateKey(), txInput.getHash());
        txInput.setSignature(signature);

        Transaction transaction = new Transaction(senderWallet.getPublicKey(), receiverWallet.getPublicKey(), 4,"doesn't matter here".getBytes());
        transaction.getInputs().add(txInput);

        assertFalse(txValidator.validateTxInputSignature(txInput, transaction));
    }

}