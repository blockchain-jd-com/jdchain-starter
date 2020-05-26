package com.jd.blockchain;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.BlockchainIdentity;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.BytesValue;
import com.jd.blockchain.ledger.BytesValueEncoding;
import com.jd.blockchain.ledger.ContractCodeDeployOperation;
import com.jd.blockchain.ledger.ContractEventSendOperation;
import com.jd.blockchain.ledger.DataAccountKVSetOperation;
import com.jd.blockchain.ledger.DataAccountRegisterOperation;
import com.jd.blockchain.ledger.DataType;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.OperationResult;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.Transaction;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.ledger.TypedKVEntry;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.sdk.converters.ClientResolveUtil;
import com.jd.blockchain.transaction.ContractReturnValue;
import com.jd.blockchain.transaction.GenericValueHolder;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.chain.contract.TransferContract;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Base64Utils;

import java.io.File;

import static org.junit.Assert.assertTrue;

public class CodeDemo {
    BlockchainKeypair CLIENT_CERT;
    GatewayServiceFactory serviceFactory;
    BlockchainService service;
    HashDigest ledgerHash;

    @Before
    public void init(){
        //创建服务代理
        CLIENT_CERT = BlockchainKeyGenerator.getInstance().generate();
        final String GATEWAY_IP = "127.0.0.1";
        final int GATEWAY_PORT = 80;
        final boolean SECURE = false;
        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IP, GATEWAY_PORT, SECURE,
                CLIENT_CERT);
        // 创建服务代理；
        service = serviceFactory.getBlockchainService();
        HashDigest[] ledgerHashs = service.getLedgerHashs();
        // 获取当前账本Hash
        ledgerHash = ledgerHashs[0];
    }

    @Test
    public void registerUser(){
// 创建服务代理；
        BlockchainService service = serviceFactory.getBlockchainService();
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = service.newTransaction(ledgerHash);
        BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();

        txTemp.users().register(user.getIdentity());

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();
        // 使用私钥进行签名；
        prepTx.sign(CLIENT_CERT);
        // 提交交易；
        prepTx.commit();
    }

    @Test
    public void RegisterData(){
        // 创建服务代理；
        BlockchainService service = serviceFactory.getBlockchainService();
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = service.newTransaction(ledgerHash);
        BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();

        txTemp.dataAccounts().register(dataAccount.getIdentity());

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();
        // 使用私钥进行签名；
        prepTx.sign(CLIENT_CERT);

        // 提交交易；
        prepTx.commit();
    }

    @Test
    public void setData(){
// 创建服务代理；
        BlockchainService service = serviceFactory.getBlockchainService();

        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = service.newTransaction(ledgerHash);

        // --------------------------------------
        // 将商品信息写入到指定的账户中；
        // 对象将被序列化为 JSON 形式存储，并基于 JSON 结构建立查询索引；
        String commodityDataAccount = "GGhhreGeasdfasfUUfehf9932lkae99ds66jf==";
        txTemp.dataAccount(commodityDataAccount).setText("ASSET_CODE", "value1", -1);

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();

        String txHash = Base64Utils.encodeToUrlSafeString(prepTx.getHash().toBytes());
        // 使用私钥进行签名；
        prepTx.sign(CLIENT_CERT);

        // 提交交易；
        prepTx.commit();
    }

    @Test
    public void queryData(){
        // 创建服务代理；
        BlockchainService service = serviceFactory.getBlockchainService();

        // 查询区块信息；
        // 区块高度；
        long ledgerNumber = service.getLedger(ledgerHash).getLatestBlockHeight();
        // 最新区块；
        LedgerBlock latestBlock = service.getBlock(ledgerHash, ledgerNumber);
        // 区块中的交易的数量；
        long txCount = service.getTransactionCount(ledgerHash, latestBlock.getHash());
        // 获取交易列表；
        LedgerTransaction[] txList = service.getTransactions(ledgerHash, ledgerNumber, 0, 100);
        // 遍历交易列表
        for (LedgerTransaction ledgerTransaction : txList) {
            TransactionContent txContent = ledgerTransaction.getTransactionContent();
            Operation[] operations = txContent.getOperations();
            if (operations != null && operations.length > 0) {
                for (Operation operation : operations) {
                    operation = ClientResolveUtil.read(operation);
                    // 操作类型：数据账户注册操作
                    if (operation instanceof DataAccountRegisterOperation) {
                        DataAccountRegisterOperation daro = (DataAccountRegisterOperation) operation;
                        BlockchainIdentity blockchainIdentity = daro.getAccountID();
                    }
                    // 操作类型：用户注册操作
                    else if (operation instanceof UserRegisterOperation) {
                        UserRegisterOperation uro = (UserRegisterOperation) operation;
                        BlockchainIdentity blockchainIdentity = uro.getUserID();
                    }
                    // 操作类型：账本注册操作
                    else if (operation instanceof LedgerInitOperation) {

                        LedgerInitOperation ledgerInitOperation = (LedgerInitOperation)operation;
                        LedgerInitSetting ledgerInitSetting = ledgerInitOperation.getInitSetting();

                        ParticipantNode[] participantNodes = ledgerInitSetting.getConsensusParticipants();
                    }
                    // 操作类型：合约发布操作
                    else if (operation instanceof ContractCodeDeployOperation) {
                        ContractCodeDeployOperation ccdo = (ContractCodeDeployOperation) operation;
                        BlockchainIdentity blockchainIdentity = ccdo.getContractID();
                    }
                    // 操作类型：合约执行操作
                    else if (operation instanceof ContractEventSendOperation) {
                        ContractEventSendOperation ceso = (ContractEventSendOperation) operation;
                    }
                    // 操作类型：KV存储操作
                    else if (operation instanceof DataAccountKVSetOperation) {
                        DataAccountKVSetOperation.KVWriteEntry[] kvWriteEntries =
                                ((DataAccountKVSetOperation) operation).getWriteSet();
                        if (kvWriteEntries != null && kvWriteEntries.length > 0) {
                            for (DataAccountKVSetOperation.KVWriteEntry kvWriteEntry : kvWriteEntries) {
                                BytesValue bytesValue = kvWriteEntry.getValue();
                                DataType dataType = bytesValue.getType();
                                Object showVal = ClientResolveUtil.readValueByBytesValue(bytesValue);
                                System.out.println("writeSet.key=" + kvWriteEntry.getKey());
                                System.out.println("writeSet.value=" + showVal);
                                System.out.println("writeSet.type=" + dataType);
                                System.out.println("writeSet.version=" + kvWriteEntry.getExpectedVersion());
                            }
                        }
                    }
                }
            }
        }

        // 根据交易的 hash 获得交易；注：客户端生成 PrepareTransaction 时得到交易hash；
        HashDigest txHash = txList[0].getTransactionContent().getHash();
        Transaction tx = service.getTransactionByContentHash(ledgerHash, txHash);
        // 获取数据；
        String commerceAccount = "GGhhreGeasdfasfUUfehf9932lkae99ds66jf==";
        String[] objKeys = new String[] { "x001", "x002" };
        TypedKVEntry[] kvData = service.getDataEntries(ledgerHash, commerceAccount, objKeys);

        long payloadVersion = kvData[0].getVersion();

        // 获取数据账户下所有的KV列表
        TypedKVEntry[] kvDatas = service.getDataEntries(ledgerHash, commerceAccount, 0, 100);
        if (kvData != null && kvData.length > 0) {
            for (TypedKVEntry kvDatum : kvDatas) {
                System.out.println("kvData.key=" + kvDatum.getKey());
                System.out.println("kvData.version=" + kvDatum.getVersion());
                System.out.println("kvData.type=" + kvDatum.getType());
                System.out.println("kvData.value=" + kvDatum.getValue());
            }
        }
    }

    @Test
    public void deployContract(){
        // 创建服务代理；
        BlockchainService service = serviceFactory.getBlockchainService();

        // 在本地定义TX模板
        TransactionTemplate txTemp = service.newTransaction(ledgerHash);

        // 合约内容读取
        byte[] contractBytes = FileUtils.readBytes(new File("CONTRACT_FILE"));

        // 生成用户
        BlockchainKeypair contractKeyPair = BlockchainKeyGenerator.getInstance().generate();

        // 发布合约
        txTemp.contracts().deploy(contractKeyPair.getIdentity(), contractBytes);

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();

        // 使用私钥进行签名；
        prepTx.sign(CLIENT_CERT);

        // 提交交易；
        TransactionResponse transactionResponse = prepTx.commit();

        assertTrue(transactionResponse.isSuccess());

        // 打印合约地址
        System.out.println(contractKeyPair.getIdentity().getAddress().toBase58());
    }

    @Test
    public void execContract(){
        // 创建服务代理；
        BlockchainService service = serviceFactory.getBlockchainService();

        // 在本地定义TX模板
        TransactionTemplate txTemp = service.newTransaction(ledgerHash);

        // 合约地址
        String contractAddress = "";

        // 使用接口方式调用合约
        TransferContract transferContract = txTemp.contract(contractAddress, TransferContract.class);

        // 使用decode方式调用合约内部方法（create方法）
        // 返回GenericValueHolder可通过get方法获取结果，但get方法需要在commit调用后执行
        String address = "address";
        String account = "fill account";
        long money = 100000000L;
        GenericValueHolder<String> result = ContractReturnValue.decode(transferContract.create(address, account, money));

        PreparedTransaction ptx = txTemp.prepare();

        ptx.sign(CLIENT_CERT);

        TransactionResponse transactionResponse = ptx.commit();

        String cotractExecResult = result.get();

        // TransactionResponse也提供了可供查询结果的接口
        OperationResult[] operationResults = transactionResponse.getOperationResults();

        // 通过OperationResult获取结果
        for (int i = 0; i < operationResults.length; i++) {
            OperationResult opResult = operationResults[i];
            System.out.printf("Operation[%s].result = %s \r\n",
                    opResult.getIndex(), BytesValueEncoding.decode(opResult.getResult()));
        }
    }
}
