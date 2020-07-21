package com.jd.blockchain;

import com.jd.blockchain.contract.SDK_Base_Demo;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.ledger.*;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhaogw
 * date 2019/9/18 16:23
 */
public class PermissionTest extends SDK_Base_Demo {
    Logger logger = LoggerFactory.getLogger(PermissionTest.class);
    BlockchainKeypair newUser;
    /**
     * 新增加一个角色
     */
    @Test
    public void executeRoleConfig_sign() {
        // 定义交易模板
        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
        txTpl.security().roles().configure("SIGN1")
                .enable(LedgerPermission.APPROVE_TX, LedgerPermission.CONSENSUS_TX)
                .disable(TransactionPermission.CONTRACT_OPERATION);
        TransactionResponse txResp = commit(txTpl);
        System.out.println(txResp.isSuccess());
    }

    /**
     * 新增加角色
     */
    @Test
    public void executeRoleConfig_moreRoles() {
        // 定义交易模板
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        // 定义角色权限；
        txTemp.security().roles().configure("MANAGER0")
                .enable(LedgerPermission.CONFIGURE_ROLES)
                .enable(TransactionPermission.DIRECT_OPERATION);
        txTemp.security().roles().configure("MANAGER1")
                .enable(LedgerPermission.REGISTER_USER)
                .enable(TransactionPermission.DIRECT_OPERATION);
        txTemp.security().roles().configure("MANAGER2")
                .enable(LedgerPermission.REGISTER_DATA_ACCOUNT)
                .enable(TransactionPermission.DIRECT_OPERATION);
        txTemp.security().roles().configure("MANAGER3")
                .enable(LedgerPermission.REGISTER_DATA_ACCOUNT)
                .enable(LedgerPermission.WRITE_DATA_ACCOUNT)
                .enable(TransactionPermission.DIRECT_OPERATION);
        txTemp.security().roles().configure("MANAGER4")
                .enable(LedgerPermission.CONFIGURE_ROLES)
                .enable(LedgerPermission.REGISTER_CONTRACT)
                .enable(LedgerPermission.REGISTER_EVENT_ACCOUNT)
                .enable(TransactionPermission.DIRECT_OPERATION);

        TransactionResponse txResp = commit(txTemp);
        System.out.println(txResp.isSuccess());
    }

    /**
     * 针对系统中已经注册的用户赋予角色;
     */
    @Test
    public void addPermission4ExistUser() {
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
//        BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();
        /**
         * 使用已知的用户构建一个keypair;
         * pubKey=3snPdw7i7PjXU3qkPdRNRch974TDGbqim2Dm1GbJDuUYqfjyYUEfSU
         * privkey=177gjyztVu92xSMda4FkhHfS6CvisvJ4nC9mSVscVsvAWN649Epy6yZ1PYYTZ4vaG1ByWZA
         * pass=abc
         */

        PrivKey privKey = KeyGenUtils.decodePrivKey("177gjyztVu92xSMda4FkhHfS6CvisvJ4nC9mSVscVsvAWN649Epy6yZ1PYYTZ4vaG1ByWZA",
                "DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY");
        PubKey pubKey = KeyGenUtils.decodePubKey("3snPdw7i7PjXU3qkPdRNRch974TDGbqim2Dm1GbJDuUYqfjyYUEfSU");
        newUser = new BlockchainKeypair(pubKey, privKey);
        System.out.println("user'id="+newUser.getAddress());
        System.out.println("pubKey="+newUser.getPubKey().toBase58());
        System.out.println("privKey="+newUser.getPrivKey().toBase58());

        txTemp.users().register(newUser.getIdentity());

        txTemp.security().authorziations().forUser(newUser.getIdentity()).
                setPolicy(RolesPolicy.UNION)
                .authorize("MANAGER0")
                .authorize("MANAGER1")
                .authorize("MANAGER3")
                .authorize("MANAGER4")
                .unauthorize("MANAGER3")
                .unauthorize("MANAGER4");
        commitA(txTemp,adminKey);
    }

    @Test
    public void testAll(){
        executeRoleConfig_moreRoles();
        addPermission4ExistUser();
    }

    @Test
    public void testAuthorization(){
        PrivilegeSet privilegeSet = blockchainService.getRolePrivileges(ledgerHash,"MANAGER1");
        logger.info(privilegeSet.toString());
        PrivKey privKey = KeyGenUtils.decodePrivKey("177gjyztVu92xSMda4FkhHfS6CvisvJ4nC9mSVscVsvAWN649Epy6yZ1PYYTZ4vaG1ByWZA",
                "DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY");
        PubKey pubKey = KeyGenUtils.decodePubKey("3snPdw7i7PjXU3qkPdRNRch974TDGbqim2Dm1GbJDuUYqfjyYUEfSU");
        newUser = new BlockchainKeypair(pubKey, privKey);
        UserPrivilegeSet userPrivilegeSet = blockchainService.getUserPrivileges(ledgerHash, newUser.getAddress().toBase58());
        logger.info(userPrivilegeSet.toString());
    }
}
