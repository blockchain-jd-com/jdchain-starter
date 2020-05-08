package com.jd.blockchain;

import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.TransactionTemplate;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.jd.blockchain.contract.SDKDemo_Constant.GW_PASSWORD;

public class ResponseTest extends SDKTest {
    private String invalid_GW_PUB_KEY = "3snPdw7i7PnBmpLtTjUt7Lyuo3RpWZzxNtjDsx1rRNi9jc3892N2Uc";
    private String invalid_GW_PRIV_KEY = "177gjxbtF999qv654gxRHwqvbibbwtNb2LknLvmsRVTDKt4MJehZE3me63r9uxpANjMEHGC";
    PrivKey gwPrivkey0 = KeyGenUtils.decodePrivKey(invalid_GW_PRIV_KEY, "DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY");
    PubKey gwPubKey0 = KeyGenUtils.decodePubKey(invalid_GW_PUB_KEY);
    BlockchainKeypair invalidAdminKey = new BlockchainKeypair(gwPubKey0, gwPrivkey0);

    @Before
    public void setup(){
    }


    //1.使用无效的endpoint用户,执行一次注册数据账户并set值的任务;
    @Test
    public void test_invalid_endPoint(){
        this.insertData(null,invalidAdminKey);
    }

    /**
     * 2.use the multiThread to insert date，构建单个区块中包含有效用户签名+无效用户签名的记录场景;
     */
    @Test
    public void test_validPlusInvalidSign_multiThread(){
        //1. 提供指定线程数量的线程池；
        ExecutorService service = Executors.newFixedThreadPool(10);
        //2. 执行指定的线程的操作，有效用户签名+无效用户签名；
        try {
            for(int i=0;i<10;i++){
                service.submit(new DataAccountThreadCall21());//适用于Callable
                service.submit(new DataAccountThreadCall31());//适用于Callable
            }
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //3. 关闭连接池
        service.shutdown();
    }

    //3.使用相同的数据账户地址插入数据;
    @Test
    public void test_insertData_same_dataAccount(){
        BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();
        this.insertData(dataAccount,null);
        this.insertData(dataAccount,null);
    }

    //4.使用相同的数据账户地址插入数据;多线程执行效果；
    @Test
    public void test_insertData_same_dataAccount_multiThread(){
        //1. 提供指定线程数量的线程池；
        ExecutorService service = Executors.newFixedThreadPool(10);
        //2. 执行指定的线程的操作，需要提供实现Runnable接口或Callable接口实现类的对象
        for(int i=0;i<10;i++){
            service.submit(new DataAccountThreadCall41());//适用于Callable
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //3. 关闭连接池
        service.shutdown();
    }

    //5.使用相同的用户注册;
    @Test
    public void test_registerUser_reply(){
        BlockchainKeypair userKey = BlockchainKeyGenerator.getInstance().generate();
        this.registerUser(null,userKey);
        this.registerUser(null,userKey);
    }

    //6.使用相同的用户注册，多线程并发;
    @Test
    public void test_registerUser_reply_multiThread(){
        //1. 提供指定线程数量的线程池；
        ExecutorService service = Executors.newFixedThreadPool(10);
        //2. 执行指定的线程的操作;
        for(int i=0;i<10;i++){
            service.submit(new UserAccountThreadCall61());
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //3. 关闭连接池
        service.shutdown();
    }

    //7.相同的合约地址部署合约，单线程;
    @Test
    public void test_deployContract_reply(){
        BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();
        this.contractHandle(null,null,contractDeployKey.getIdentity(),true,true);
        this.contractHandle(null,null,contractDeployKey.getIdentity(),true,false);
    }

    //8.相同的合约地址部署合约，多线程;
    @Test
    public void test_deployContract_reply_multiThread(){
        //1. 提供指定线程数量的线程池；
        ExecutorService service = Executors.newFixedThreadPool(10);
        //2. 执行指定的线程的操作;
        for(int i=0;i<10;i++){
            service.submit(new ContractAccountThreadCall71());
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //3. 关闭连接池
        service.shutdown();
    }

    //9.相同的合约重复执行;合约定义和执行严格定义;
    @Test
    public void test_executeContract_reply(){
        BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();
        this.contractHandle(null,null,contractDeployKey.getIdentity(),true,true);
        this.contractHandle(null,null,contractDeployKey.getIdentity(),false,true);
    }

    //10.合约执行非合约定义mainClass对应的接口;
    @Test
    public void test_executeContract_differ(){
        BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();
        this.contractHandle("contract-JDChain-Contract-Error.jar",null,contractDeployKey.getIdentity(),true,true);
    }

    //11.相同的合约执行;合约执行非合约定义mainClass对应的接口;
    @Test
    public void test_executeContract_reply_differ(){
        BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();
        this.contractHandle(null,null,contractDeployKey.getIdentity(),true,true);
        this.contractHandle(null,null,contractDeployKey.getIdentity(),false,true);
    }

    //12.数据账户未注册，直接set;
    @Test
    public void insertDataByExistDataAccount() {
        if (!isTest) return;
        String dataAccount = "LdeNremWbMBmmn4hJkgYBqGqruMYE8iZqjeF5";
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);

        //add some data for retrieve;
        System.out.println("current dataAccount=" + dataAccount);
        txTemp.dataAccount(dataAccount).setText("cc-fin01-01",
                "{\"dest\":\"KA001\",\"id\":\"cc-fin01-01\",\"items\":\"FIN001|5000\",\"source\":\"FIN001\"}", -1);

        // TX 准备就绪
        commit(txTemp,adminKey);
    }

    //13.合约未发布，直接执行;
    @Test
    public void test_executeContract_noAddress(){
        BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();
        this.contractHandle(null,null,contractDeployKey.getIdentity(),false,true);
    }

    //14.数据账户set相同版本；
    @Test
    public void test_insertData_same_version(){
        BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();
        this.insertData(dataAccount,null);
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        txTemp.dataAccount(dataAccount.getAddress()).setText("key1","v1",-1);
        commit(txTemp);
    }
}

//===========inner Class============

// 2-1. 创建一个实现Callable的实现类
class DataAccountThreadCall21 extends SDKTest implements Callable {
    // 2. 实现call方法，将此线程需要执行的操作声明在call中
    @Override
    public Object call() {
        insertData();
        return null;
    }
}

// 3-1. 创建一个实现Callable的实现类，无效用户进行签名；
class DataAccountThreadCall31 extends ResponseTest implements Callable {
    // 2. 实现call方法，将此线程需要执行的操作声明在call中
    @Override
    public Object call() {
        insertData(null,invalidAdminKey);
        return null;
    }
}
// 4-1. 创建一个实现Callable的实现类，相同数据账户执行两次insert；
class DataAccountThreadCall41 extends ResponseTest implements Callable {
    // 2. 实现call方法；
    @Override
    public Object call() {
        BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();
        this.insertData(dataAccount,null);
        this.insertData(dataAccount,null);
        return null;
    }
}

// 6-1. 创建一个实现Callable的实现类，相同用户注册;
class UserAccountThreadCall61 extends ResponseTest implements Callable {
    // 2. 实现call方法；
    @Override
    public Object call() {
        BlockchainKeypair userKey = BlockchainKeyGenerator.getInstance().generate();
        this.registerUser(null,userKey);
        this.registerUser(null,userKey);
        return null;
    }
}

// 7-1. 创建一个实现Callable的实现类，相同相同的合约地址部署合约;
class ContractAccountThreadCall71 extends ResponseTest implements Callable {
    // 2. 实现call方法；
    @Override
    public Object call() {
        BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();
        this.contractHandle(null,null,contractDeployKey.getIdentity(),true,false);
        this.contractHandle(null,null,contractDeployKey.getIdentity(),true,false);
        return null;
    }
}