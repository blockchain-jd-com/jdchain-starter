package com.jd.blockchain;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;

public class TestJdChain {


    public static void main(String[] argas){
        testOne();
    }

    public static void testOne() {
        // 私钥
        String PRIV_KEY = "177gjxvoT9qsJncbnwkhwwJXLahnYmwFYBSeR26tBhoJGhmRj6jtMEVUZF92Djr86nakwqM";
        // 私钥密码
        String PRIV_KEY_PASSWORD = "DAc48C8t1V1UJk6ZvNQYXzEPbHgG3H8eTzGBNtZZMNPC";
        // 公钥
        PrivKey privkey0 = KeyGenUtils.decodePrivKey(PRIV_KEY, PRIV_KEY_PASSWORD);
        PubKey pub_key= KeyGenUtils.decodePubKey("3snPdw7i7Pij7VXCkq7soCgAHSx5zS8S8vNdiV8DEkJ76eWKNd3sdd");
        //创建服务代理
        BlockchainKeypair CLIENT_CERT = new BlockchainKeypair(pub_key, privkey0);
        String GATEWAY_IP = "jdchain2-18081.jd.com";
        int GATEWAY_PORT = 80;
        boolean SECURE = false;
        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IP, GATEWAY_PORT, SECURE, CLIENT_CERT);
        // 创建服务代理
        BlockchainService service = serviceFactory.getBlockchainService();

        HashDigest[] ledgerHashs = service.getLedgerHashs();
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = service.newTransaction(ledgerHashs[0]);

        txTemp.dataAccounts().register(CLIENT_CERT.getIdentity());
        txTemp.dataAccount(CLIENT_CERT.getAddress()).setText("key1","value1",-1);
        //add some data for retrieve;
        System.out.println("current dataAccount="+CLIENT_CERT.getAddress());
        txTemp.dataAccount(CLIENT_CERT.getAddress()).setText("cc-fin01-01","{\"dest\":\"KA001\",\"id\":\"cc-fin01-01\",\"items\":\"FIN001|5000\",\"source\":\"FIN001\"}",-1);
        txTemp.dataAccount(CLIENT_CERT.getAddress()).setText("cc-fin02-01","{\"dest\":\"KA001\",\"id\":\"cc-fin02-01\",\"items\":\"FIN002|2000\",\"source\":\"FIN002\"}",-1);

        // TX 准备就绪
        PreparedTransaction prepTx = txTemp.prepare();
        prepTx.sign(CLIENT_CERT);

        // 提交交易；
        TransactionResponse transactionResponse = prepTx.commit();
        System.out.println("result is:"+transactionResponse.getExecutionState());
        long height = service.getLedger(ledgerHashs[0]).getLatestBlockHeight();
        System.out.println(height);

    }
}
