package com.jd.blockchain.contract;

import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainKeypair;
import org.apache.commons.io.FileUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.InputStream;

public class SDKDemo_Constant {

    //localhost
    public static  String GW_IPADDR = "jdchain-cloud7-8080.jdfmgt.com";
    public static  int GW_PORT = 80;
    public static String GW_PUB_KEY[] = {"3snPdw7i7Pd3CdgAyFyNKaAEYGkBpEtMKPvpCCJWgX9dDx6KcEDeKj",
            "3snPdw7i7PajLB35tEau1kmixc6ZrjLXgxwKbkv5bHhP7nT5dhD9eX"};
    public static String GW_PRIV_KEY[] = {"177gjyRVfUgdN5UiXA2YDu53cMwxiXkFbLdhCQCe7wyt59SBxcVKyg9qP3CDRyoTFgYLP7y",
            "177gju9p5zrNdHJVEQnEEKF4ZjDDYmAXyfG84V5RPGVc5xFfmtwnHA7j51nyNLUFffzz5UT"};
    public static String GW_PASSWORD = "AXhhKihAa2LaRwY5mftnngSPKDF4N9JignnQ4skynY8y";
    public static String GW_PASSWORD_PEER1 = "DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY";

    //another server;
//    public static String GW_IPADDR = "jdchain-cloud0-8080.jdfmgt.com";
//    public static int GW_PORT = 80;
//    public static String GW_PUB_KEY[] = {"3snPdw7i7Pb2WSq3kRDrhwNCeaUeZKop96i4D2wp9XyHRnCA3BNNzt",
//            "3snPdw7i7Pg2qC7GbaN2Ly85BpZsRbncZcJtpeDRrr7aeKNkbMnSxM"};
//    public static String GW_PRIV_KEY[] = {"177gjzgZD2uZi2DFWJVzUTLYfoUxLU1qbktj1ugkzWLbPNrRx395hmmAYDLBJyxZWGQ7Tbv",
//            "177gjz2q2LkJiUj52hz1NkQSSEoSZwNZB5brLQRFMBTSYkA19QQhwsgj2VExmAbeNSEgZj9"};
//    public static String GW_PASSWORD = "DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY";
//    public static String GW_PASSWORD_PEER1 = "8EjkXVSTxMFjCvNNsTo8RBMDEVQmk7gYkW4SCDuvdsBG";

//    public static String GW_IPADDR = "jdchain2-18081.jd.com";
//    public static int GW_PORT = 80;
//    public static String GW_PUB_KEY[] = {"3snPdw7i7PXVXYjsBDQAjyExMjVLEVNYViK8fkTfexjqbxqsWgZVGX",
//        "3snPdw7i7Pf8eJ1uycdAM6spw7XjbST7m39MZbD9qdL4QEzoBAwLKh"};
//    public static String GW_PRIV_KEY[] = {"177gjufK1ZNFncmgdCwGYs6cnyeu8HoG6wsc2XKesGEGxfTrPYqYWi2GfRE55SAxvtY4KbJ",
//            "177gjyFk3VDzfExR1a5NxHvSGqS9FJ2aNAaqMgNyTUrRguMA9jN6Bp3vasUB7wXr1cqcDdj"};
//    public static String GW_PASSWORD = "8EjkXVSTxMFjCvNNsTo8RBMDEVQmk7gYkW4SCDuvdsBG";
//    public static String GW_PASSWORD_PEER1 = "8EjkXVSTxMFjCvNNsTo8RBMDEVQmk7gYkW4SCDuvdsBG";

    public static PrivKey gwPrivkey0 = KeyGenUtils.decodePrivKey(GW_PRIV_KEY[0], GW_PASSWORD);
    public static PubKey gwPubKey0 = KeyGenUtils.decodePubKey(GW_PUB_KEY[0]);
    public static BlockchainKeypair adminKey = new BlockchainKeypair(gwPubKey0, gwPrivkey0);


    public static PrivKey peer1Privkey0 = KeyGenUtils.decodePrivKey(GW_PRIV_KEY[1], GW_PASSWORD_PEER1);
    public static PubKey peer1PubKey0 = KeyGenUtils.decodePubKey(GW_PUB_KEY[1]);
    public static BlockchainKeypair peer1Key = new BlockchainKeypair(peer1PubKey0, peer1Privkey0);


    public static final byte[] readChainCodes(String contractZip) {
        // 构建合约的字节数组;
        try {
            ClassPathResource contractPath = new ClassPathResource(contractZip);
//            File contractFile = new File(contractPath.getURI());

            InputStream in = contractPath.getInputStream();
            // 将文件写入至config目录下
            File directory = new File(".");
            String configPath = directory.getAbsolutePath() + File.separator + "contract.jar";
            File targetFile = new File(configPath);
            // 先将原来文件删除再Copy
            if (targetFile.exists()) {
                FileUtils.forceDelete(targetFile);
            }
            FileUtils.copyInputStreamToFile(in, targetFile);
//            return FileUtils.readFileToByteArray(contractFile);
            return FileUtils.readFileToByteArray(targetFile);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
