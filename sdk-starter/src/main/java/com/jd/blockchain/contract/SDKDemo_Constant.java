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
    public static  String GW_IPADDR = "localhost";
    public static  int GW_PORT = 11000;
    public static String GW_PUB_KEY = "3snPdw7i7PjVKiTH2VnXZu5H8QmNaSXpnk4ei533jFpuifyjS5zzH9";
    public static String GW_PRIV_KEY = "177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x";
    public static String GW_PASSWORD = "DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY";

    //another server;
//    public static String GW_IPADDR = "jdchain-cloud4-8080.jdfmgt.com";
//    public static int GW_PORT = 80;
//    public static String GW_PUB_KEY = "3snPdw7i7Po4VYVWquEwKMTzMqycJ6DbGwFbmJkB3Vgx5UBoHLAFXz";
//    public static String GW_PRIV_KEY = "177gjuFSYF1CM4rZ5Mp2AXdKNyg47V5JiWiJPGNwEeZ1wLMHpEjgEyvn5cMGnEvqMSc8gdg";
//    public static String GW_PASSWORD = "8EjkXVSTxMFjCvNNsTo8RBMDEVQmk7gYkW4SCDuvdsBG";

//    public static String GW_IPADDR = "jdchain2-18081.jd.com";
//    public static int GW_PORT = 80;
//    public static String GW_PUB_KEY = "3snPdw7i7PXVXYjsBDQAjyExMjVLEVNYViK8fkTfexjqbxqsWgZVGX";
//    public static String GW_PRIV_KEY = "177gjufK1ZNFncmgdCwGYs6cnyeu8HoG6wsc2XKesGEGxfTrPYqYWi2GfRE55SAxvtY4KbJ";
//    public static String GW_PASSWORD = "8EjkXVSTxMFjCvNNsTo8RBMDEVQmk7gYkW4SCDuvdsBG";

    public static PrivKey gwPrivkey0 = KeyGenUtils.decodePrivKey(GW_PRIV_KEY, GW_PASSWORD);
    public static PubKey gwPubKey0 = KeyGenUtils.decodePubKey(GW_PUB_KEY);
    public static BlockchainKeypair adminKey = new BlockchainKeypair(gwPubKey0, gwPrivkey0);

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
