package com.jd.blockchain;

import com.alibaba.fastjson.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;

public class APITest {
    private String ipPort;
    private String ipPortPlusLedgerHash;
    private String ledgerHash;
    private String userAddress;
    private String dataAddress;
    private String contractAddress;
    private RestTemplate restTemplate;
    private String blockHash;
    private int blockHeight;
    private String contentHash;

    @Before
    public void setup(){
        ipPort = "http://localhost:11000/";
        ledgerHash = "j5jeQnyTopJWUP8pNs5vWUk9cFwfRYAzkjAB6DbRCcPdAn";
//        ipPort = "http://jdchain1-8081.jd.com:80/";
//        ledgerHash = "j5mfQ6A3NTXisVjsMD46uHrkj3puZJCTkgwjxNDVYV1UmA";

//        ipPort = "http://jdchain-cloud4-8080.jdfmgt.com/";
//        ledgerHash = "j5s3Xx2djijUi7NewerfPtRTta3EAa9ErNcBsHgzkDND7g";

        ipPortPlusLedgerHash = ipPort + "ledgers/"+ledgerHash;

        userAddress = "";
        dataAddress = "";
        contractAddress = "";
        restTemplate = new RestTemplate();
    }

    @Test
    public void testApi() {
        System.out.println("2.1 获取账本总数");
        postHandle(ipPort + "/ledgers/count");

        System.out.println("2.2 获取账本列表");
        postHandle(ipPort + "ledgers?fromIndex=0&count=-1");

        System.out.println("2.3 获取账本详细信息");
        postHandle(ipPort+"ledgers/"+ledgerHash);

        System.out.println("2.4 获取账本成员总数");
        postHandle(ipPort+"ledgers/"+ledgerHash+"/participants/count");

        System.out.println("2.5 获取账本成员列表");
        postHandle(ipPort+"ledgers/"+ledgerHash+"/participants?fromIndex=0&count=-1");

        System.out.println("3.1 获取最新区块");
        String result = postHandle(ipPortPlusLedgerHash+"/blocks/latest");
        blockHash = JSONObject.parseObject(result).getJSONObject("data").getJSONObject("hash").getString("value");
        blockHeight = JSONObject.parseObject(result).getJSONObject("data").getInteger("height");

        System.out.println("3.2 根据区块哈希获取区块详细信息");
        postHandle(ipPortPlusLedgerHash+"/blocks/hash/"+blockHash);

        System.out.println("3.3 根据区块高度获取区块详细信息");
        postHandle(ipPortPlusLedgerHash+"/blocks/height/"+blockHeight);

//        System.out.println("3.4 根据哈希查询区块总数");
        //3.5 根据哈希查询区块


        System.out.println("4.1 获取账本交易总数");
        postHandle(ipPortPlusLedgerHash+"/txs/count");

        System.out.println("4.2 根据区块高度查询区块内的交易数量");
        postHandle(ipPortPlusLedgerHash+"/blocks/height/"+blockHeight+"/txs/additional-count");

        System.out.println("4.3 根据区块哈希查询区块内的交易数量");
        postHandle(ipPortPlusLedgerHash+"/blocks/hash/"+blockHash+"/txs/additional-count");

        System.out.println("4.4 获取指定高度的区块交易列表");
        postHandle(ipPortPlusLedgerHash+"/blocks/height/"+blockHeight+"/txs?fromIndex=0&count=-1");

        System.out.println("4.5 获取指定哈希的区块的交易列表");
        postHandle(ipPortPlusLedgerHash+"/blocks/hash/"+blockHash+"/txs?fromIndex=0&count=-1");

        //获得最新区块中的一个contentHash；
        String latestTxs = postHandle(ipPortPlusLedgerHash+"/blocks/height/"+blockHeight+"/txs");
        contentHash = JSONObject.parseObject(latestTxs).getJSONArray("data").getJSONObject(0).getJSONObject("transactionContent").getJSONObject("hash").getString("value");


        System.out.println("4.6 获取交易详细信息");
        postHandle(ipPortPlusLedgerHash+"/txs/hash/"+contentHash);

        //4.7 根据哈希查询交易总数
        //4.8 根据哈希查询交易

        System.out.println("5.1 获取用户总数");
        postHandle(ipPortPlusLedgerHash+"/users/count");

        System.out.println("5.2 获取用户列表");
        result = postHandle(ipPortPlusLedgerHash+"/users?fromIndex=0&count=-1");
        userAddress = JSONObject.parseObject(result).getJSONArray("data").getJSONObject(0).getJSONObject("address").getString("value");

        System.out.println("5.3 获取用户详细信息");
        postHandle(ipPortPlusLedgerHash+"/users/address/"+userAddress);

        //5.4 用户查询数量
        //5.5 用户查询

        System.out.println("6.1 获取账户列表");
        result = postHandle(ipPortPlusLedgerHash+"/accounts?fromIndex=0&count=-1");
        dataAddress = JSONObject.parseObject(result).getJSONArray("data").getJSONObject(0).getJSONObject("address").getString("value");

        System.out.println("6.2 获取账户详细信息");
        postHandle(ipPortPlusLedgerHash+"/accounts/address/"+dataAddress);

        System.out.println("6.3 获取账户总数");
        postHandle(ipPortPlusLedgerHash+"/accounts/count/");

        //6.4 查询数据账户匹配的数量
        //6.5 查询数据账户

        System.out.println("6.6 获取某数据账户KV总数");
        postHandle(ipPortPlusLedgerHash+"/accounts/address/"+dataAddress+"/entries/count");

        System.out.println("6.7 获取某数据账户KV详情");
        postHandle(ipPortPlusLedgerHash+"/accounts/address/"+dataAddress+"/entries?fromIndex=0&count=-1");

//        System.out.println("6.8 查询某数据账户键数量");
        //6.9 查询某数据账户键

        //7.1 搜索区块链

        System.out.println("8.1 获取合约列表");
        result = postHandle(ipPortPlusLedgerHash+"/contracts?fromIndex=0&count=-1");
        contractAddress = JSONObject.parseObject(result).getJSONArray("data").getJSONObject(0).getJSONObject("address").getString("value");

        System.out.println("8.2 获取合约详细信息");
        postHandle(ipPortPlusLedgerHash+"/contracts/address/"+contractAddress);

        System.out.println("8.3 获取合约总数");
        postHandle(ipPortPlusLedgerHash+"/contracts/count");

        //8.4 查询指定合约数量
        //8.5 合约查询

    }

    private String postHandle(String url){
        String result = restTemplate.getForObject(url,String.class);
        Assert.assertTrue(JSONObject.parseObject(result).getBoolean("success"));
        System.out.println(result);
        return result;
    }
}
