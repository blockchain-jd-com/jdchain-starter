package com.jd.blockchain;

import com.jd.blockchain.contract.SDKDemo_Constant;
import com.jd.blockchain.contract.SDK_Base_Demo;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import org.junit.Before;
import org.junit.Test;

/**
 * @author zhaogw
 * date 2019/12/9 14:21
 */
public class SDKDemo_RegisterUser extends SDK_Base_Demo {
    //because it need to connect the web, so make the switch;
    private boolean isTest = true;
    private String strDataAccount;
    private BlockchainKeypair existUser;

    @Before
    public void setup(){
        existUser = BlockchainKeyGenerator.getInstance().generate();
//        existUser = getExistUser();
    }

    private BlockchainKeypair getExistUser(){
//        通过bin/keygen.sh可获得如下公私钥，格式：sh keygen.sh –n xxx
        String pubKeyStr = "3snPdw7i7PXwePqNj5f6ZbYBGXSHbH4XJefqf735WtcDQvkDdwPSGU";
        String privKeyStr = "177gjvtAB5pQ23Kpw1icRcYa8vRg5aQiWfJzwj5AbD4hJbMnN8eLtcvkaf5w2T4poU5imR9";
        String passwd = "DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY";
        // 生成连接网关的账号
        PrivKey privKey = KeyGenUtils.decodePrivKey(privKeyStr, passwd);
        PubKey pubKey = KeyGenUtils.decodePubKey(pubKeyStr);
        return new BlockchainKeypair(pubKey, privKey);
    }

    private void registerRole(String roleName){
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);

        // 定义角色权限；
        txTemp.security().roles().configure(roleName).enable(LedgerPermission.APPROVE_TX)
                .enable(LedgerPermission.REGISTER_USER).enable( LedgerPermission.REGISTER_DATA_ACCOUNT)
                .enable(LedgerPermission.WRITE_DATA_ACCOUNT)
                .enable(TransactionPermission.DIRECT_OPERATION);

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();
        prepTx.sign(adminKey);

        // 提交交易；
        prepTx.commit();
    }

    /**
     * rigister the exist user to ledger;
     */
    private void registerExistUser(String roleName){
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        System.out.println("user'id="+existUser.getAddress());

        txTemp.users().register(existUser.getIdentity());
//        txTemp.security().authorziations().forUser(newUser.getIdentity()).authorize("ROLE-ADD-DATA").setPolicy(RolesPolicy.INTERSECT);
        txTemp.security().authorziations().forUser(existUser.getIdentity()).unauthorize("DEFAULT").authorize(roleName);

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();
        prepTx.sign(adminKey);

        // 提交交易；
        prepTx.commit();
        System.out.println("registerExistUser() done.");
    }

    private void writeDataAcount() {
        boolean SECURE = false;
        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(SDKDemo_Constant.GW_IPADDR, SDKDemo_Constant.GW_PORT, SECURE,
                existUser);
        BlockchainService service2 = serviceFactory.getBlockchainService();
        HashDigest[] ledgerHashs = service2.getLedgerHashs();

        TransactionTemplate txTemp = service2.newTransaction(ledgerHashs[0]);
        BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();
        txTemp.dataAccounts().register(dataAccount.getIdentity());
        txTemp.dataAccount(dataAccount.getAddress()).setText("key2", "value2", -1);

        // TX 准备就绪
        PreparedTransaction prepTx = txTemp.prepare();
        prepTx.sign(existUser);

        // 提交交易；
        TransactionResponse transactionResponse = prepTx.commit();
        if(transactionResponse.isSuccess()){
            System.out.println("writeDataAcount() done.");
        }else {
            System.out.println(transactionResponse.getExecutionState());
        }
    }

    @Test
    public void checkPermission(){
        String roleName = "ROLE-ADD-DATA-1";
        registerRole(roleName);
        registerExistUser(roleName);
        writeDataAcount();
    }
}
