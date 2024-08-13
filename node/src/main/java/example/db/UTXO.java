package example.db;




import org.example.common.model.TransactionOutput;

import java.security.PublicKey;
import java.util.*;

/*
    In-memory database for storing the UTXO set
 */
public class UTXO {

    private static final Map<String, List<TransactionOutput>> map = new HashMap<>();

    public static void storeUTXO(String transactionID,TransactionOutput utxo) {
        if (map.containsKey(transactionID)) {
            map.get(transactionID).add(utxo);
        } else {
            List<TransactionOutput> list = new ArrayList<>();
            list.add(utxo);
            map.put(transactionID, list);
        }
    }

    public static List<TransactionOutput> getUTXOs(String transactionID) {
        return map.get(transactionID);
    }

    public static void removeUTXO(String transactionID, TransactionOutput utxo) {

        if (!map.containsKey(transactionID)) {
            throw new IllegalStateException("Transaction not found in UTXO database");
        }

        List<TransactionOutput> set = map.get(transactionID);
        if (!set.contains(utxo)) {
            throw new IllegalStateException("UTXO not found in UTXO database");
        }
        set.remove(utxo);
    }

    public static double getBalance(PublicKey wallet) {
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
