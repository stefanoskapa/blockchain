package org.example.common.util;



import org.example.common.model.Transaction;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MerkleTree {



    public static String getMerkleRoot(List<Transaction> transactions) {
        List<String> transactionHashes = transactions.stream().map(Transaction::getTransactionId).collect(Collectors.toList());
        return construct(transactionHashes).get(0);
    }


    private static List<String> construct(List<String> transactions) {

        if (transactions.size() == 1)
            return transactions;

        List<String> updatedList = new ArrayList<>();

        for (int i = 0; i < transactions.size() - 1; i += 2)
            updatedList.add(mergeHash(transactions.get(i), transactions.get(i + 1)));

        if (transactions.size() % 2 == 1)
            updatedList.add(mergeHash(transactions.get(transactions.size() - 1), transactions.get(transactions.size() - 1)));

        return construct(updatedList);
    }

    private static String mergeHash(String hash1, String hash2) {
        String mergedHash = hash1 + hash2;
        return Sha256.hash(mergedHash);
    }
}
