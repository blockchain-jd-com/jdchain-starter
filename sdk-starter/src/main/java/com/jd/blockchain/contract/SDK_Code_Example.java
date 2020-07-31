package com.jd.blockchain.contract;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.crypto.KeyGenUtils;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
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
import com.jd.blockchain.ledger.Event;
import com.jd.blockchain.ledger.LedgerBlock;
import com.jd.blockchain.ledger.LedgerInitOperation;
import com.jd.blockchain.ledger.LedgerInitSetting;
import com.jd.blockchain.ledger.LedgerTransaction;
import com.jd.blockchain.ledger.Operation;
import com.jd.blockchain.ledger.OperationResult;
import com.jd.blockchain.ledger.ParticipantNode;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.SystemEvent;
import com.jd.blockchain.ledger.Transaction;
import com.jd.blockchain.ledger.TransactionContent;
import com.jd.blockchain.ledger.TransactionResponse;
import com.jd.blockchain.ledger.TransactionTemplate;
import com.jd.blockchain.ledger.TypedKVEntry;
import com.jd.blockchain.ledger.UserRegisterOperation;
import com.jd.blockchain.sdk.BlockchainService;
import com.jd.blockchain.sdk.EventContext;
import com.jd.blockchain.sdk.EventListenerHandle;
import com.jd.blockchain.sdk.SystemEventListener;
import com.jd.blockchain.sdk.SystemEventPoint;
import com.jd.blockchain.sdk.UserEventListener;
import com.jd.blockchain.sdk.UserEventPoint;
import com.jd.blockchain.sdk.client.GatewayServiceFactory;
import com.jd.blockchain.sdk.converters.ClientResolveUtil;
import com.jd.blockchain.transaction.ContractReturnValue;
import com.jd.blockchain.transaction.GenericValueHolder;
import com.jd.blockchain.utils.io.BytesUtils;
import com.jd.blockchain.utils.io.FileUtils;
import com.jd.chain.contract.TransferContract;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.springframework.util.Base64Utils;

import java.io.IOException;

/**
 * @author zhaogw
 * @date 2020/7/31 10:45
 */
public class SDK_Code_Example {
    GatewayServiceFactory serviceFactory;
    HashDigest ledgerHash;
    BlockchainKeypair adminKey;
    BlockchainIdentity dataAccountIdentity;
    BlockchainIdentity contractIdentity;
    BlockchainIdentity eventIdentity;

    public static void main(String[] args) {
        SDK_Code_Example sdk_code_example = new SDK_Code_Example();
        sdk_code_example.connect();
        sdk_code_example.registerUser();
        sdk_code_example.registerDataAccount();
        sdk_code_example.writeDataAccount();
        sdk_code_example.queryTransaction();
        try {
            sdk_code_example.deployContract();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sdk_code_example.executeContract();

        //event
        sdk_code_example.registerEvent();
        sdk_code_example.deployEvent();
        sdk_code_example.listenSystemEvent();
//        sdk_code_example.listenUserEvent();
        try {
            Thread.sleep(10000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void connect(){
        //构建联网用户,持有如下公钥的用户事先需要在链上注册，测试可直接复用peer0持有的公私钥数据;
        String GW_PUB_KEY = "3snPdw7i7PjVKiTH2VnXZu5H8QmNaSXpnk4ei533jFpuifyjS5zzH9";
        String GW_PRIV_KEY = "177gjzHTznYdPgWqZrH43W3yp37onm74wYXT4v9FukpCHBrhRysBBZh7Pzdo5AMRyQGJD7x";
        String GW_PASSWORD = "DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY";
        PrivKey gwPrivkey0 = KeyGenUtils.decodePrivKey(GW_PRIV_KEY, GW_PASSWORD);
        PubKey gwPubKey0 = KeyGenUtils.decodePubKey(GW_PUB_KEY);
//        BlockchainKeypair adminKey = new BlockchainKeypair(gwPubKey0, gwPrivkey0);
        adminKey = new BlockchainKeypair(gwPubKey0, gwPrivkey0);

        //创建服务代理
        final String GATEWAY_IP = "127.0.0.1";
        final int GATEWAY_PORT = 11000;

        final boolean SECURE = false;
//        GatewayServiceFactory serviceFactory = GatewayServiceFactory.connect(GATEWAY_IP, GATEWAY_PORT, SECURE, CLIENT_CERT);
        serviceFactory = GatewayServiceFactory.connect(GATEWAY_IP, GATEWAY_PORT, SECURE, adminKey);
        // 创建服务代理；
        BlockchainService service = serviceFactory.getBlockchainService();
        HashDigest[] ledgerHashs = service.getLedgerHashs();
        // 获取当前账本Hash
//        HashDigest ledgerHash = ledgerHashs[0];
        ledgerHash = ledgerHashs[0];
    }

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
        prepTx.sign(adminKey);
        // 提交交易；
        prepTx.commit();
    }

    //注册数据账户;
    public void registerDataAccount(){
// 创建服务代理；
        BlockchainService service = serviceFactory.getBlockchainService();
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = service.newTransaction(ledgerHash);
        BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();
        txTemp.dataAccounts().register(dataAccount.getIdentity());
        dataAccountIdentity = dataAccount.getIdentity();

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();
        // 使用私钥进行签名；
        prepTx.sign(adminKey);

        // 提交交易；
        prepTx.commit();
    }

    //写入数据账户;
    public void writeDataAccount(){
// 创建服务代理；
        BlockchainService service = serviceFactory.getBlockchainService();

        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = service.newTransaction(ledgerHash);

        // --------------------------------------
        // 将商品信息写入到指定的账户中；
        // 对象将被序列化为 JSON 形式存储，并基于 JSON 结构建立查询索引；
//        String commodityDataAccount = "LdeNk55z6N7zDqNerKLBWC19wTXHwC568kTWZ";
        String commodityDataAccount = dataAccountIdentity.getAddress().toBase58();
        txTemp.dataAccount(commodityDataAccount).setText("ASSET_CODE", "value1", -1);
        txTemp.dataAccount(commodityDataAccount).setInt64("x001",10000L,-1);
        txTemp.dataAccount(commodityDataAccount).setInt64("x002",100L,-1);

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();

        String txHash = Base64Utils.encodeToUrlSafeString(prepTx.getHash().toBytes());
        // 使用私钥进行签名；
        prepTx.sign(adminKey);

        // 提交交易；
        prepTx.commit();
    }

    //查询
    public void queryTransaction(){
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
//        String commerceAccount = "LdeNk55z6N7zDqNerKLBWC19wTXHwC568kTWZ";
        String commerceAccount = dataAccountIdentity.getAddress().toBase58();
        String[] objKeys = new String[] { "x001", "x002" };
        TypedKVEntry[] kvData = service.getDataEntries(ledgerHash, commerceAccount, objKeys);

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

    public void deployContract() throws IOException {
        // 创建服务代理；
        BlockchainService service = serviceFactory.getBlockchainService();

        // 在本地定义TX模板
        TransactionTemplate txTemp = service.newTransaction(ledgerHash);

        // 合约内容读取
        ClassPathResource contractPath = new ClassPathResource("contract-compile-1.3.0.RELEASE.car");
        byte[] contractBytes = FileUtils.readBytes(contractPath.getFile());
        // 生成用户
        BlockchainKeypair contractKeyPair = BlockchainKeyGenerator.getInstance().generate();

        // 发布合约
        txTemp.contracts().deploy(contractKeyPair.getIdentity(), contractBytes);
        contractIdentity = contractKeyPair.getIdentity();

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();

        // 使用私钥进行签名；
        prepTx.sign(adminKey);

        // 提交交易；
        TransactionResponse transactionResponse = prepTx.commit();

        Assert.isTrue(transactionResponse.isSuccess(),"返回不成功");

        // 打印合约地址
        System.out.println(contractKeyPair.getIdentity().getAddress().toBase58());
    }

    //执行合约;
    public void executeContract(){
        // 创建服务代理；
        BlockchainService service = serviceFactory.getBlockchainService();

        // 在本地定义TX模板
        TransactionTemplate txTemp = service.newTransaction(ledgerHash);

        // 合约地址
//        String contractAddress = "LdeNgrmS9FySDxTYc8Ruuyp36MzsHxLA6Pjxh";
        String contractAddress = contractIdentity.getAddress().toBase58();

        // 使用接口方式调用合约
        TransferContract transferContract = txTemp.contract(contractAddress, TransferContract.class);

        // 使用decode方式调用合约内部方法（create方法）
        // 返回GenericValueHolder可通过get方法获取结果，但get方法需要在commit调用后执行
        String address = dataAccountIdentity.getAddress().toBase58();
        String account = "accountId";
        long money = 100000000L;
        GenericValueHolder<String> result = ContractReturnValue.decode(transferContract.create(address, account, money));

        PreparedTransaction ptx = txTemp.prepare();

        ptx.sign(adminKey);

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

    //注册事件;
    public void registerEvent(){
        BlockchainService service = serviceFactory.getBlockchainService();
        TransactionTemplate txTemp = service.newTransaction(ledgerHash);

        BlockchainKeypair eventAccount = BlockchainKeyGenerator.getInstance().generate();
        txTemp.eventAccounts().register(eventAccount.getIdentity());
        eventIdentity = eventAccount.getIdentity();

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();
        // 使用私钥进行签名；
        prepTx.sign(adminKey);

        // 提交交易；
        prepTx.commit();
    }

    //发布事件;
    public void deployEvent(){
        BlockchainService service = serviceFactory.getBlockchainService();
        TransactionTemplate txTemp = service.newTransaction(ledgerHash);

        // 发布事件到指定的账户中；
//        String eventAccount = "GGhhreGeasdfasfUUfehf9932lkae99ds66jf==";
        String eventAccount = eventIdentity.getAddress().toBase58();
        txTemp.eventAccount(eventAccount).publish("event_name", "string", -1)
                .publish("event_name", 2, 0)
                .publish("event_name", "bytes".getBytes(), 1);

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();

        // 使用私钥进行签名；
        prepTx.sign(adminKey);

        // 提交交易；
        prepTx.commit();
    }

    //事件监听-系统事件
    public  void listenSystemEvent(){
        BlockchainService service = serviceFactory.getBlockchainService();
        EventListenerHandle<SystemEventPoint> handler = service.monitorSystemEvent(ledgerHash,
                SystemEvent.NEW_BLOCK_CREATED, 0, new SystemEventListener<SystemEventPoint>() {
            @Override
            public void onEvents(Event[] eventMessages, EventContext<SystemEventPoint> eventContext) {
                for (Event eventMessage : eventMessages) {
                    BytesValue content = eventMessage.getContent();
                    // content中存放的是当前链上最新高度
                    System.out.println(BytesUtils.toLong(content.getBytes().toBytes()));
                }

                // 关闭监听的两种方式：1
//                eventContext.getHandle().cancel();
            }
        });

        // 关闭监听的两种方式：2
//        handler.cancel();
    }

    //事件监听-用户自定义事件;
    public void listenUserEvent(){
        BlockchainService service = serviceFactory.getBlockchainService();
//        String eventAccount = "";
        String eventAccount = eventIdentity.getAddress().toBase58();
        String eventName = "event_name";
        EventListenerHandle<UserEventPoint> handler = service.monitorUserEvent(ledgerHash, eventAccount,
                eventName, 0, new UserEventListener<UserEventPoint>() {
            @Override
            public void onEvent(Event eventMessage, EventContext<UserEventPoint> eventContext) {
                BytesValue content = eventMessage.getContent();
                switch (content.getType()) {
                    case TEXT:
                    case XML:
                    case JSON:
                        System.out.println(content.getBytes().toUTF8String());
                        break;
                    case INT64:
                    case TIMESTAMP:
                        System.out.println(BytesUtils.toLong(content.getBytes().toBytes()));
                        break;
                    default:
                        break;
                }

                // 关闭监听的两种方式：1
//                eventContext.getHandle().cancel();
            }
        });

        // 关闭监听的两种方式：2
//        handler.cancel();
    }
}
