package com.jd.blockchain.contract;

import com.jd.blockchain.binaryproto.BinaryProtocol;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.transaction.GenericValueHolder;
import com.jd.blockchain.transaction.SignatureUtils;
import com.jd.blockchain.utils.Bytes;
import com.jd.chain.contract.TransferContract;

import static com.jd.blockchain.contract.SDKDemo_Constant.readChainCodes;
import static com.jd.blockchain.transaction.ContractReturnValue.decode;

public abstract class SDK_Base_Demo {
    protected boolean useCommitA=true;

    protected BlockchainKeypair adminKey;

    protected HashDigest ledgerHash;

    protected BlockchainService blockchainService;

    public SDK_Base_Demo() {
        init();
    }

    public void init() {
        // 生成连接网关的账号
        adminKey = SDKDemo_Constant.adminKey;

        // 连接网关
        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(SDKDemo_Constant.GW_IPADDR,
                SDKDemo_Constant.GW_PORT, false, adminKey);

        // 获取网关对应的Service处理类
        blockchainService = serviceFactory.getBlockchainService();

        HashDigest[] ledgerHashs = blockchainService.getLedgerHashs();
        // 获取当前账本Hash
        ledgerHash = ledgerHashs[0];
    }

    /**
     * 默认使用A方式commit;
     * @param txTpl
     * @return
     */
    public TransactionResponse commit(TransactionTemplate txTpl) {
        return this.commitA(txTpl,null);
    }

    public TransactionResponse commitA(TransactionTemplate txTpl) {
        return this.commitA(txTpl,null);
    }

    public TransactionResponse commitB(TransactionTemplate txTpl) {
        return this.commitB(txTpl,null);
    }

    public TransactionResponse commit(TransactionTemplate txTpl, boolean useCommitA){
        if(useCommitA){
            return commitA(txTpl);
        }else {
            return commitB(txTpl);
        }
    }

    /**
     * 默认使用A方式commit;
     * @param txTpl
     * @param signAdminKey
     * @return
     */
    public TransactionResponse commit(TransactionTemplate txTpl, BlockchainKeypair signAdminKey){
        return commitA(txTpl, signAdminKey);
    }

    public TransactionResponse commit(TransactionTemplate txTpl, BlockchainKeypair signAdminKey, boolean useCommitA){
        if(useCommitA){
            return commitA(txTpl, signAdminKey);
        }else {
            return commitB(txTpl,signAdminKey);
        }
    }

    /**
     * 采用A方式提交；
     * @param txTpl
     * @param signAdminKey
     * @return
     */
    public TransactionResponse commitA(TransactionTemplate txTpl, BlockchainKeypair signAdminKey) {
        PreparedTransaction ptx = txTpl.prepare();

        if(signAdminKey != null){
            System.out.println("signAdminKey's pubKey = "+signAdminKey.getIdentity().getPubKey());
            ptx.sign(signAdminKey);
        }else {
            System.out.println("adminKey's pubKey = "+adminKey.getIdentity().getPubKey());
            ptx.sign(adminKey);
        }
        TransactionResponse transactionResponse = ptx.commit();

        if (transactionResponse.isSuccess()) {
            System.out.println(String.format("height=%d, ###OK#, contentHash=%s, executionState=%s",
                    transactionResponse.getBlockHeight(),
                    transactionResponse.getContentHash(), transactionResponse.getExecutionState().toString()));
        } else {
            System.out.println(String.format("height=%d, ###exception#, contentHash=%s, executionState=%s",
                    transactionResponse.getBlockHeight(),
                    transactionResponse.getContentHash(), transactionResponse.getExecutionState().toString()));
        }
        return transactionResponse;
    }

    /**
     * 采用B方式提交（交易序列化）；
     * @param txTpl
     * @param signAdminKey
     * @return
     */
    public TransactionResponse commitB(TransactionTemplate txTpl, BlockchainKeypair signAdminKey) {
        PreparedTransaction ptx = txTpl.prepare();

        //new code;
        // 序列化交易内容；
        byte[] txContentBytes = BinaryProtocol.encode(ptx.getTransactionContent(), TransactionContent.class);

        // 反序列化交易内容；
        TransactionContent txContent = BinaryProtocol.decode(txContentBytes, TransactionContent.class);

        // 对交易内容签名；
        DigitalSignature signature1 = SignatureUtils.sign(txContent, adminKey);

        // 根据交易内容重新准备交易；
        PreparedTransaction decodedPrepTx = blockchainService.prepareTransaction(txContent);

        // 使用私钥进行签名，或附加签名；
        decodedPrepTx.addSignature(signature1);
        if(signAdminKey != null){
            System.out.println("signAdminKey's pubKey = "+signAdminKey.getIdentity().getPubKey());
            decodedPrepTx.sign(signAdminKey);
        }else {
            System.out.println("adminKey's pubKey = "+adminKey.getIdentity().getPubKey());
            decodedPrepTx.sign(adminKey);
        }

        // 提交交易；
        TransactionResponse transactionResponse = decodedPrepTx.commit();
        //====end====

        if (transactionResponse.isSuccess()) {
            System.out.println(String.format("height=%d, ###OK#, contentHash=%s, executionState=%s",
                    transactionResponse.getBlockHeight(),
                    transactionResponse.getContentHash(), transactionResponse.getExecutionState().toString()));

            // 操作结果对应于交易中的操作顺序；无返回结果的操作对应结果为 null;
            if(transactionResponse.getOperationResults()!=null && transactionResponse.getOperationResults().length>0){
                OperationResult opResult = transactionResponse.getOperationResults()[0];
                Class<?> dataClazz = String.class;//返回值的类型；
                Object value = BytesValueEncoding.decode(opResult.getResult(), dataClazz);
                System.out.println("return value="+value);
            }
        } else {
            System.out.println(String.format("height=%d, ###exception#, contentHash=%s, executionState=%s",
                    transactionResponse.getBlockHeight(),
                    transactionResponse.getContentHash(), transactionResponse.getExecutionState().toString()));
        }
        return transactionResponse;
    }

    /**
     * 生成一个区块链用户，并注册到区块链；
     */
    public BlockchainKeypair registerUser() {
        return this.registerUser(null,null);
    }

    public BlockchainKeypair registerUser(BlockchainKeypair signAdminKey, BlockchainKeypair userKeypair) {
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        if(userKeypair == null){
            userKeypair = BlockchainKeyGenerator.getInstance().generate();
        }
        System.out.println("user'address="+userKeypair.getAddress());
        txTemp.users().register(userKeypair.getIdentity());
        // TX 准备就绪；
        commit(txTemp,signAdminKey,useCommitA);
        return userKeypair;
    }

    /**
     * 生成一个区块链用户，并注册到区块链；
     */
    public BlockchainKeypair registerUserByNewSigner(BlockchainKeypair signer) {
        return this.registerUser(signer,null);
    }

    public BlockchainIdentity createDataAccount() {
        // 首先注册一个数据账户
        BlockchainKeypair newDataAccount = BlockchainKeyGenerator.getInstance().generate();

        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
        txTpl.dataAccounts().register(newDataAccount.getIdentity());
        commitA(txTpl);
        return newDataAccount.getIdentity();
    }

    public BlockchainIdentity contractHandle(String contractZipName, BlockchainKeypair signAdminKey, BlockchainIdentity contractIdentity,
                               boolean isDeploy, boolean isExecute) {
       return this.contractHandle(contractZipName,signAdminKey,contractIdentity,isDeploy,isExecute,
                null,null,null);
    }

    public String create1(Bytes contractAddress, String address, String account, String content) {
        System.out.println(String.format("params,String address=%s, String account=%s, String content=%s, Bytes contractAddress=%s",
                address,account,content,contractAddress.toBase58()));
        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
        // 使用合约创建
        TransferContract guanghu = txTpl.contract(contractAddress, TransferContract.class);
        GenericValueHolder<String> result = decode(guanghu.putval(address, account, content, System.currentTimeMillis()));
        commit(txTpl,useCommitA);
        return result.get();
    }

    public BlockchainIdentity contractHandle(String contractZipName, BlockchainKeypair signAdminKey,
                                            BlockchainIdentity contractDeployIdentity, boolean isDeploy,
                               boolean isExecute, BlockchainIdentity dataAccount, String key, String value) {
        if(contractZipName == null){
            contractZipName = "contract-JDChain-Contract.jar";
        }
        // 发布jar包
        // 定义交易模板
        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
        Bytes contractAddress = null;
        if(contractDeployIdentity != null){
            contractAddress = contractDeployIdentity.getAddress();
        }

        if(isDeploy){
            // 将jar包转换为二进制数据
            byte[] contractCode = readChainCodes(contractZipName);

            // 生成一个合约账号
            if(contractDeployIdentity == null){
                contractDeployIdentity = BlockchainKeyGenerator.getInstance().generate().getIdentity();
            }
            contractAddress = contractDeployIdentity.getAddress();
            System.out.println("contract's address=" + contractAddress);

            // 生成发布合约操作
            txTpl.contracts().deploy(contractDeployIdentity, contractCode);

            // 生成预发布交易；
            commit(txTpl,signAdminKey,useCommitA);
        }

        if(isExecute){
            // 注册一个数据账户
            if(dataAccount == null){
                dataAccount = createDataAccount();
                key = "jd_zhangsan";
                value = "{\"dest\":\"KA006\",\"id\":\"cc-fin08-01\",\"items\":\"FIN001|3030\",\"source\":\"FIN001\"}";
            }
            // 获取数据账户地址x
            String dataAddress = dataAccount.getAddress().toBase58();
            // 打印数据账户地址
            System.out.printf("DataAccountAddress = %s \r\n", dataAddress);
            System.out.println("return value = "+create1(contractAddress, dataAddress, key, value));
        }
        return contractDeployIdentity;
    }
}
