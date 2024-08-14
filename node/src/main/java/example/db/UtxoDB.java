package example.db;




import org.example.common.model.TransactionInput;
import org.example.common.model.TransactionOutput;

import java.security.PublicKey;
import java.util.*;

/*
    In-memory database for storing the UTXO set
 */
public class UtxoDB {

    private final Map<String, List<TransactionOutput>> map = new HashMap<>();

    public boolean txInputBelongsToWallet(TransactionInput txInput, PublicKey publicKey) {
        try {
            TransactionOutput to = map.get(txInput.getPrevTransactionId()).get(txInput.getOutputIndex());
            return to.getReceiver().equals(publicKey);
        } catch (Exception e) {
            return false;
        }
    }

    public List<TransactionInput> getTxInputsForWalletForAmount(PublicKey publicKey, double amount) {
        double sum = 0;

        List<TransactionInput> inputs = new ArrayList<>();
        for (var transactionId : map.keySet()) {
            for (var utxo : map.get(transactionId)) {
                if (utxo.getReceiver().equals(publicKey)) {
                    sum += utxo.getAmount();
                    TransactionInput ti = new TransactionInput(transactionId,map.get(transactionId).indexOf(utxo));
                    inputs.add(ti);
                }
                if (sum >= amount) {
                    return inputs;
                }
            }
        }
        return null;
    }
    public void storeUTXO(String transactionID, TransactionOutput utxo) {
        if (map.containsKey(transactionID)) {
            map.get(transactionID).add(utxo);
        } else {
            List<TransactionOutput> list = new ArrayList<>();
            list.add(utxo);
            map.put(transactionID, list);
        }
    }

    public double getTransactionInputAmount(TransactionInput transactionInput) {
        List<TransactionOutput> utxoList = map.get(transactionInput.getPrevTransactionId());
        int index = transactionInput.getOutputIndex();
        if (utxoList == null || index >= utxoList.size()) {
            return -1;
        }
        TransactionOutput utxo = utxoList.get(transactionInput.getOutputIndex());
        return utxo.getAmount();
    }

    public List<TransactionOutput> getUTXOs(String transactionID) {
        return map.get(transactionID);
    }

    public void removeUTXO(String transactionID, int outputIndex) {

        if (!map.containsKey(transactionID)) {
            throw new IllegalStateException("Transaction not found in UTXO database");
        }

        List<TransactionOutput> list = map.get(transactionID);
        if (list == null || list.size() <= outputIndex) {
            throw new IllegalStateException("UTXO not found in UTXO database");
        }
        list.remove(outputIndex);
    }

    public double getBalance(PublicKey wallet) {
        double balance = 0;

        for (String s : map.keySet()) {
            for (var to : map.get(s)) {
                if (to.getReceiver().equals(wallet)) {
                    balance += to.getAmount();
                }
            }
        }
        return balance;
    }

}
