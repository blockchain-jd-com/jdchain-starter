package com.jd.blockchain.util;

import com.jd.blockchain.SDKTest;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.DigitalSignature;
import com.jd.blockchain.ledger.Transaction;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.ledger.TypedKVEntry;
import org.junit.Test;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PerformanceTest extends SDKTest {

    @Test
    public void insertDataMore() {
        for (int i=1;i<=256;i=i*2){
            String fileName= "1-"+i+"k";
            byte[] arr = new byte[i*1024];
            new Random().nextBytes(arr);
            long startTime = System.currentTimeMillis();
            this.insertData("key1",arr);
            System.out.println("base58,"+fileName+",spend time="+(System.currentTimeMillis()-startTime));
            startTime = System.currentTimeMillis();
//            Base58Utils.encode(arr);
            System.out.println("Base58Utils,"+fileName+",spend time="+(System.currentTimeMillis()-startTime));
        }
    }

    @Test
    public void insertBigData() {
        int i = 1000;//unit:K;max=4095;
        String fileName= "1-"+i+"k";
        byte[] arr = new byte[i*1024];
        new Random().nextBytes(arr);
        long startTime = System.currentTimeMillis();
        HashDigest contentHash=this.insertData("key1",arr);
        System.out.println("base58,"+fileName+",spend time="+(System.currentTimeMillis()-startTime));
        startTime = System.currentTimeMillis();
        System.out.println("Base58Utils,"+fileName+",spend time="+(System.currentTimeMillis()-startTime));
        System.out.println("contentHash="+contentHash);
        System.out.println("getTransactionByContentHash pre...");
        Transaction tx = blockchainService.getTransactionByContentHash(ledgerHash,contentHash);
        System.out.println("getTransactionByContentHash after...");
        DigitalSignature[] signatures = tx.getEndpointSignatures();
        for (DigitalSignature signature : signatures) {
            System.out.println("signer's pubKey="+signature.getPubKey().toBase58());
        }
        System.out.println("transaction.blockHeight=" + tx.getBlockHeight());
        System.out.println("transaction.executionState=" + tx.getExecutionState());
    }

    private HashDigest insertData(String key, byte[] bytes){
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        //采用KeyGenerator来生成BlockchainKeypair;
        BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();
        txTemp.dataAccounts().register(dataAccount.getIdentity());

        System.out.println("current dataAccount=" + dataAccount.getAddress());
        txTemp.dataAccount(dataAccount.getAddress()).setBytes(key, bytes, -1);

        // TX 准备就绪
        TransactionResponse transactionResponse = commit(txTemp,null,useCommitA);

        //get the version
        TypedKVEntry[] kvData = blockchainService.getDataEntries(ledgerHash,
                dataAccount.getAddress().toBase58(), key);
        System.out.println(String.format("key1 info:key=%s,value=%s,version=%d",
                kvData[0].getKey(),kvData[0].getValue().toString(),kvData[0].getVersion()));
        System.out.println("######getData start...");
        getData(dataAccount.getAddress().toBase58());
        System.out.println("contentHash="+transactionResponse.getContentHash().toBase58());
        return transactionResponse.getContentHash();
    }

    /**
     * use the multiThread to insert date;
     */
    @Test
    public void pressureTest(){
        //1. 提供指定线程数量的线程池；
        ExecutorService service = Executors.newFixedThreadPool(100);
        //2. 执行指定的线程的操作，需要提供实现Runnable接口或Callable接口实现类的对象
        for(int i=0;i<100;i++){
            service.submit(new DataAccountThreadCall());//适用于Callable
        }
        try {
            Thread.sleep(1000*60*30);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //3. 关闭连接池
        service.shutdown();
    }
}

// 1. 创建一个实现Callable的实现类
class DataAccountThreadCall extends SDKTest implements Callable {
    // 2. 实现call方法，将此线程需要执行的操作声明在call中
    @Override
    public Object call() throws InterruptedException {
        while (true){
            insertData();
            Thread.sleep(100);
        }
//        for(int i=0;i<100;i++){
//            insertData();
//            Thread.sleep(100);
//        }
//        return null;
    }
}
