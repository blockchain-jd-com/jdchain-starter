package com.jd.blockchain;

import com.jd.blockchain.contract.SDK_Base_Demo;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.transaction.GenericValueHolder;
import com.jd.blockchain.utils.Bytes;
import contract.service.QANVUVVEIBR2Contract;
import org.junit.Before;
import org.junit.Test;

import static com.jd.blockchain.contract.SDKDemo_Constant.readChainCodes;
import static com.jd.blockchain.transaction.ContractReturnValue.decode;

/**
 * @author zhaogw
 * date 2020/3/19 16:15
 */
public class SDKTest4Jr extends SDK_Base_Demo {
    //because it need to connect the web, so make the switch;
    public boolean isTest = true;
    private String strDataAccount;
    private BlockchainKeypair existUser;

    @Before
    public void setup() {
        useCommitA = false;
        existUser = BlockchainKeyGenerator.getInstance().generate();
    }


    @Test
    public void executeContractJr() {
        this.contractHandle1("contract-JDChain-Contract-long.jar",null,null,true,true);
    }

    public void contractHandle1(String contractZipName, BlockchainKeypair signAdminKey, BlockchainKeypair contractDeployKey,
                               boolean isDeploy, boolean isExecute) {
        if(contractZipName == null){
            contractZipName = "contract-JDChain-Contract.jar";
        }
        // 发布jar包
        // 定义交易模板
        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
        Bytes contractAddress = null;
        if(isDeploy){
            // 将jar包转换为二进制数据
            byte[] contractCode = readChainCodes(contractZipName);

            // 生成一个合约账号
            if(contractDeployKey == null){
                contractDeployKey = BlockchainKeyGenerator.getInstance().generate();
            }
            contractAddress = contractDeployKey.getAddress();
            System.out.println("contract's address=" + contractAddress);

            // 生成发布合约操作
            txTpl.contracts().deploy(contractDeployKey.getIdentity(), contractCode);

            // 生成预发布交易；
            commit(txTpl,signAdminKey, false);
        }

        if(isExecute){
            // 注册一个数据账户
            BlockchainIdentity dataAccount = createDataAccount();
            // 获取数据账户地址x
            String dataAddress = dataAccount.getAddress().toBase58();
            // 打印数据账户地址
            System.out.printf("DataAccountAddress = %s \r\n", dataAddress);

            // 创建两个账号：
            String account0 = "jd_zhangsan";
            String content = "{\"dest\":\"KA006\",\"id\":\"cc-fin08-01\",\"items\":\"FIN001|3030\",\"source\":\"FIN001\"}";

            System.out.println("old method, return value = "+create1(contractAddress, dataAddress, account0, content));
        }
    }

    public String create1(Bytes contractAddress, String address, String account, String content) {
        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
        // 使用合约创建
        QANVUVVEIBR2Contract guanghu = txTpl.contract(contractAddress, QANVUVVEIBR2Contract.class);
        GenericValueHolder<String> result = decode(guanghu.writeQANVUVVEIBR2(address,"{\"userName\":\"vitty\",\"age\":10}"));
        commit(txTpl,useCommitA);
        return result.get();
    }
}
