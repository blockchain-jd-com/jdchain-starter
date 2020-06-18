package com.jd.blockchain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jd.blockchain.contract.SDK_Base_Demo;
import com.jd.blockchain.crypto.*;
import com.jd.blockchain.ledger.*;
import com.jd.blockchain.sdk.converters.ClientResolveUtil;
import com.jd.blockchain.transaction.GenericValueHolder;
import com.jd.blockchain.utils.Bytes;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.security.ShaUtils;
import com.jd.chain.contract.TransferContract;
import org.bouncycastle.util.encoders.Hex;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.jd.blockchain.contract.SDKDemo_Constant.readChainCodes;
import static com.jd.blockchain.transaction.ContractReturnValue.decode;

/**
 * @author zhaogw
 * date 2019/8/8 10:43
 */
public class SDKTest extends SDK_Base_Demo {
    //because it need to connect the web, so make the switch;
    public boolean isTest = true;
    private String strDataAccount;
    private BlockchainKeypair existUser;

    @Before
    public void setup() {
        existUser = BlockchainKeyGenerator.getInstance().generate();
    }

    @Test
    public void checkXml_existDataAcount() {
        if (!isTest) return;
        insertData();
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);

        //add some data for retrieve;
        System.out.println("current dataAccount=" + this.strDataAccount);
        txTemp.dataAccount(this.strDataAccount).setText("textKey", "{\"dest\":\"KA001\",\"id\":\"cc-fin01-01\",\"items\":\"FIN001|5000\",\"source\":\"FIN001\"}", -1);
        txTemp.dataAccount(this.strDataAccount).setXML("xmlKey", "<person>\n" +
                "    <age value=\"too young\" />\n" +
                "    <experience value=\"too simple\" />\n" +
                "    <result value=\"sometimes naive\" />\n" +
                "</person>", -1);

        // TX 准备就绪
        commitA(txTemp,adminKey);

        getData(strDataAccount);
    }

    /**
     * 根据已有的数据账户地址，添加数据;
     */
    @Test
    public void insertDataByExistDataAccount() {
        if (!isTest) return;
//        this.strDataAccount = "LdeNremWbMBmmn4hJkgYBqGqruMYE8iZqjeF5";
        this.strDataAccount  = this.createDataAccount().getAddress().toBase58();
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);

        //add some data for retrieve;
        System.out.println("current dataAccount=" + this.strDataAccount);
        txTemp.dataAccount(this.strDataAccount).setText("cc-fin01-01",
                "{\"dest\":\"KA001\",\"id\":\"cc-fin01-01\",\"items\":\"FIN001|5000\",\"source\":\"FIN001\"}", -1);
        txTemp.dataAccount(this.strDataAccount).setText("cc-fin02-01",
                "{\"dest\":\"KA001\",\"id\":\"cc-fin02-01\",\"items\":\"FIN002|2000\",\"source\":\"FIN002\"}", -1);
        txTemp.dataAccount(this.strDataAccount).setText("cc-fin03-01",
                "{\"dest\":\"KA001\",\"id\":\"cc-fin03-01\",\"items\":\"FIN001|5000\",\"source\":\"FIN003\"}", -1);
        txTemp.dataAccount(this.strDataAccount).setText("cc-fin04-01",
                "{\"dest\":\"KA002\",\"id\":\"cc-fin04-01\",\"items\":\"FIN003|3000\",\"source\":\"FIN002\"}", -1);
        txTemp.dataAccount(this.strDataAccount).setText("cc-fin05-01",
                "{\"dest\":\"KA003\",\"id\":\"cc-fin05-01\",\"items\":\"FIN001|5000\",\"source\":\"FIN001\"}", -1);
        txTemp.dataAccount(this.strDataAccount).setText("cc-fin06-01",
                "{\"dest\":\"KA004\",\"id\":\"cc-fin06-01\",\"items\":\"FIN002|2020\",\"source\":\"FIN001\"}", -1);
        txTemp.dataAccount(this.strDataAccount).setText("cc-fin07-01",
                "{\"dest\":\"KA005\",\"id\":\"cc-fin07-01\",\"items\":\"FIN001|5010\",\"source\":\"FIN001\"}", -1);
        txTemp.dataAccount(this.strDataAccount).setText("cc-fin08-01",
                "{\"dest\":\"KA006\",\"id\":\"cc-fin08-01\",\"items\":\"FIN001|3030\",\"source\":\"FIN001\"}", -1);

        // TX 准备就绪
        commitA(txTemp,adminKey);

        getData(strDataAccount);
    }

    public void getData(String commerceAccount) {
        // 查询区块信息；
        // 区块高度；
        long ledgerNumber = blockchainService.getLedger(ledgerHash).getLatestBlockHeight();
        // 最新区块；
        LedgerBlock latestBlock = blockchainService.getBlock(ledgerHash, ledgerNumber);
        // 区块中的交易的数量；
        long txCount = blockchainService.getTransactionCount(ledgerHash, latestBlock.getHash());
        // 获取交易列表；
        LedgerTransaction[] txList = blockchainService.getTransactions(ledgerHash, ledgerNumber, 0, 100);
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

                        LedgerInitOperation ledgerInitOperation = (LedgerInitOperation) operation;
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
                        BytesValueList bytesValueList = ceso.getArgs();
                        BytesValue[] bytesValueArr = bytesValueList.getValues();
                        for(BytesValue bytesValue1 : bytesValueArr){
                            if(bytesValue1 instanceof TypedValue){
                                TypedValue typedValue = (TypedValue) bytesValue1;
                                if("JSON".equals(typedValue.getType().toString())){
                                    JSONArray jsonArray = JSON.parseObject(typedValue.stringValue()).getJSONArray("values");
                                    for(int i=0 ; i< jsonArray.size(); i++ ){
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                                        String realValBase58 = jsonObject.getJSONObject("bytes").getString("value");
                                        String typeStr = jsonObject.getString("type");
                                        DataType dataType = DataType.valueOf(typeStr);
                                        BytesValue bytesValue = TypedValue.fromType(dataType, Base58Utils.decode(realValBase58));
                                        try {
                                            if("INT64".equals(typeStr)){
                                                long time = bytes2Long(Base58Utils.decode(realValBase58));
                                                System.out.println("Long = "+time);
                                            }else {
                                                System.out.println("params,key="+typeStr+",value="+ new String(Base58Utils.decode(realValBase58),"utf-8"));
                                            }
                                        } catch (UnsupportedEncodingException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
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

        //根据交易的 hash 获得交易；注：客户端生成 PrepareTransaction 时得到交易hash；
        HashDigest txHash = txList[0].getTransactionContent().getHash();
//		Transaction tx = blockchainService.getTransactionByContentHash(ledgerHash, txHash);
//		String[] objKeys = new String[] { "x001", "x002" };
//		KVDataEntry[] kvData = blockchainService.getDataEntries(ledgerHash, commerceAccount, objKeys);

        // 获取数据账户下所有的KV列表
        if(commerceAccount == null){
            return;
        }
        TypedKVEntry[] kvData = blockchainService.getDataEntries(ledgerHash, commerceAccount, 0, 100);
        if (kvData != null && kvData.length > 0) {
            for (TypedKVEntry kvDatum : kvData) {
                System.out.println("kvData.key=" + kvDatum.getKey());
                System.out.println("kvData.version=" + kvDatum.getVersion());
                System.out.println("kvData.type=" + kvDatum.getType());
                System.out.println("kvData.value=" + kvDatum.getValue());
            }
        }
    }

    public static long bytes2Long(byte[] byteNum) {
        long num = 0;
        for (int ix = 0; ix < 8; ++ix) {
            num <<= 8;
            num |= (byteNum[ix] & 0xff);
        }
        return num;
    }

    @Test
    public void executeContractOK() {
//        BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();
        this.contractHandle(null,null,null,true,true);
    }

    //contract bifurcation
    @Test
    public void executeContractBifByHalf() {
        this.executeContractBif("half");
    }

    /**
     *  test  more bifurcation, then insertData;
     *  purpose: Is the system still robust after much rollback ?
     */

    @Test
    public void executeContractBifByHalf_more() {
        for(int i=0;i<100;i++){
            this.executeContractBif("half");
        }
        this.insertData();
    }

    /**
     * bifurcation, 3:1;
     */
    @Test
    public void executeContractBifBy31() {
        this.executeContractBif("most");
    }

    private void executeContractBif(String isHalf) {
        // 发布jar包
        // 定义交易模板
        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

        // 将jar包转换为二进制数据
        byte[] contractCode = readChainCodes("contract-JDChain-Contract.jar");

        // 生成一个合约账号
        BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();
        System.out.println("contract's address=" + contractDeployKey.getAddress());

        // 生成发布合约操作
        txTpl.contracts().deploy(contractDeployKey.getIdentity(), contractCode);

        // 生成预发布交易；
        PreparedTransaction ptx = txTpl.prepare();

        // 对交易进行签名
        ptx.sign(adminKey);

        // 提交并等待共识返回；
        TransactionResponse txResp = ptx.commit();

        // 获取合约地址
        Bytes contractAddress = contractDeployKey.getAddress();

        // 打印交易返回信息
        System.out.printf("Tx[%s] -> BlockHeight = %s, BlockHash = %s, isSuccess = %s, ExecutionState = %s \r\n",
                txResp.getContentHash().toBase58(), txResp.getBlockHeight(), txResp.getBlockHash().toBase58(),
                txResp.isSuccess(), txResp.getExecutionState());

        // 打印合约地址
        System.out.printf("ContractAddress = %s \r\n", contractAddress.toBase58());

        // 注册一个数据账户
        BlockchainIdentity dataAccount = createDataAccount();
        // 获取数据账户地址
        String dataAddress = dataAccount.getAddress().toBase58();
        // 打印数据账户地址
        System.out.printf("DataAccountAddress = %s \r\n", dataAddress);

        // 创建两个账号：
        String account0 = "jd_zhangsan";
        String content = "{\"dest\":\"KA006\",\"id\":\"cc-fin08-01\",\"items\":\"FIN001|3030\",\"source\":\"FIN001\"}";
        System.out.println("executeContractByHalf="+createBif(dataAddress, account0, content, contractAddress, isHalf));
    }

    private String createBif(String address, String account, String content, Bytes contractAddress, String isHalf) {
        TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
        // 使用合约创建
        TransferContract guanghu = txTpl.contract(contractAddress, TransferContract.class);
        GenericValueHolder<String> result = decode(guanghu.putvalBifurcation(address, account, content, isHalf));
        commitA(txTpl);
        return result.get();
    }

    /**
     * 生成一个区块链用户，并注册到区块链；
     */
    @Test
    public void registerUserTest() {
        this.registerUser();
    }

    @Test
    public void rigisterUserMore() {
        for (int i = 0; i < 15; i++) {
            this.registerUser();
        }
    }

    @Test
    public void insertDataMore() {
        for (int i = 0; i < 15; i++) {
            this.insertData();
        }
    }

    /**
     * use the exist user to sign;
     */
    @Test
    public void registerNewUserByExistUser() {
        //first invoke the case: registerExistUser(), to rigister user in the ledger;
        //now use the existUser to sign;
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);

        BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();
        System.out.println("user'id=" + user.getAddress());
        txTemp.users().register(user.getIdentity());
        // TX 准备就绪；
        commitA(txTemp,existUser);
    }

    /**
     * insert data by the exist user; no think the permission;
     */
    @Test
    public void insertDataByExistUser() {
        if (!isTest) return;
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        //采用KeyGenerator来生成BlockchainKeypair;
        BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();

        txTemp.dataAccounts().register(dataAccount.getIdentity());
        txTemp.dataAccount(dataAccount.getAddress()).setText("key1", "value1", -1);
        //add some data for retrieve;
        this.strDataAccount = dataAccount.getAddress().toBase58();
        System.out.println("current dataAccount=" + dataAccount.getAddress());
        txTemp.dataAccount(dataAccount.getAddress()).setText("cc-fin01-01", "{\"dest\":\"KA001\",\"id\":\"cc-fin01-01\",\"items\":\"FIN001|5000\",\"source\":\"FIN001\"}", -1);

        // TX 准备就绪
        commitA(txTemp,existUser);
    }

    private void registerRole(String roleName) {
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);

        // 定义角色权限；
        txTemp.security().roles().configure(roleName).enable(LedgerPermission.APPROVE_TX)
                .enable(LedgerPermission.REGISTER_USER).disable(LedgerPermission.REGISTER_DATA_ACCOUNT)
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
    private void registerExistUser(String roleName) {
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        System.out.println("user'id=" + existUser.getAddress());

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

    private void checkInsertDataByExistUser() {
        System.out.println("checkInsertDataByExistUser() start...");
        if (!isTest) return;
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        //采用KeyGenerator来生成BlockchainKeypair;
        BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();

        txTemp.dataAccounts().register(dataAccount.getIdentity());
        txTemp.dataAccount(dataAccount.getAddress()).setText("key1", "value1", -1);
        //add some data for retrieve;
        this.strDataAccount = dataAccount.getAddress().toBase58();
        System.out.println("current dataAccount=" + dataAccount.getAddress());
        txTemp.dataAccount(dataAccount.getAddress()).setText("cc-fin01-01", "{\"dest\":\"KA001\",\"id\":\"cc-fin01-01\",\"items\":\"FIN001|5000\",\"source\":\"FIN001\"}", -1);

        // TX 准备就绪
        PreparedTransaction prepTx = txTemp.prepare();
        prepTx.sign(existUser);

        // 提交交易；
        TransactionResponse transactionResponse = prepTx.commit();
        if (transactionResponse.isSuccess()) {
            System.out.println("result=" + transactionResponse.isSuccess());
        } else {
            System.out.println("exception=" + transactionResponse.getExecutionState().toString());
        }
    }

    /**
     * use the same blockchainService(connected by adminKey);
     * because it's policy is at_least_one(see: DataAccountRegisterOperationHandle.java),
     * although signed by existUser, the operation of "txTemp.dataAccounts().register(...)" also can passed;
     * so you will use a new user to connect the gateway and signed by it. you can see the demo {@link SDKDemo_RegisterUser#checkPermission()}
     */
//    @Test
    public void checkPermission() {
        String roleName = "ROLE-ADD-DATA";
        registerRole(roleName);
        registerExistUser(roleName);
        checkInsertDataByExistUser();
    }

    @Test
    public void insertDataByInvalidUsers() throws InterruptedException {
        for(int i=0;i<100;i++){
            this.insertDataByInvalidUser();
            Thread.sleep(100L);
        }
    }

    /**
     * 生成一个区块链数据账户，并注册到区块链；
     */
    @Test
    public void insertData() {
        this.insertData(null,null);
    }

    public BlockchainKeypair insertData(BlockchainKeypair dataAccount, BlockchainKeypair signAdminKey) {
        if (!isTest) return null;
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        //采用KeyGenerator来生成BlockchainKeypair;
        if(dataAccount == null){
            dataAccount = BlockchainKeyGenerator.getInstance().generate();
        }

        txTemp.dataAccounts().register(dataAccount.getIdentity());
        txTemp.dataAccount(dataAccount.getAddress()).setText("key1", "value1", -1);
//        //add some data for retrieve;
        this.strDataAccount = dataAccount.getAddress().toBase58();
        System.out.println("current dataAccount=" + dataAccount.getAddress());
        txTemp.dataAccount(dataAccount.getAddress()).setText("cc-fin01-01", "{\"dest\":\"KA001\",\"id\":\"cc-fin01-01\",\"items\":\"FIN001|5000\",\"source\":\"FIN001\"}", -1);

        // TX 准备就绪
        commit(txTemp,signAdminKey,useCommitA);

        //get the version
        TypedKVEntry[] kvData = blockchainService.getDataEntries(ledgerHash,
                dataAccount.getAddress().toBase58(), "key1");
        System.out.println(String.format("key1 info:key=%s,value=%s,version=%d",
                kvData[0].getKey(),kvData[0].getValue().toString(),kvData[0].getVersion()));

        return dataAccount;
    }

    public BlockchainKeypair insertData(BlockchainKeypair dataAccount, BlockchainKeypair signAdminKey,
                             String key, String value, long version) {
        if (!isTest) return null;
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        //采用KeyGenerator来生成BlockchainKeypair;
        if(dataAccount == null){
            dataAccount = BlockchainKeyGenerator.getInstance().generate();
            txTemp.dataAccounts().register(dataAccount.getIdentity());
        }

        this.strDataAccount = dataAccount.getAddress().toBase58();
        System.out.println("current dataAccount=" + dataAccount.getAddress());
        txTemp.dataAccount(dataAccount.getAddress()).setText(key, value, version);

        // TX 准备就绪
        commit(txTemp,signAdminKey,useCommitA);

        //get the version
        TypedKVEntry[] kvData = blockchainService.getDataEntries(ledgerHash,
                dataAccount.getAddress().toBase58(), key);
        System.out.println(String.format("key1 info:key=%s,value=%s,version=%d",
                kvData[0].getKey(),kvData[0].getValue().toString(),kvData[0].getVersion()));

        return dataAccount;
    }

    @Test
    public void insertDataByInvalidUser() {
        if (!isTest) return;
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
        //采用KeyGenerator来生成BlockchainKeypair;
        BlockchainKeypair dataAccount = BlockchainKeyGenerator.getInstance().generate();

        txTemp.dataAccounts().register(dataAccount.getIdentity());
        txTemp.dataAccount(dataAccount.getAddress()).setText("key1", "value1", -1);

        // TX 准备就绪
        BlockchainKeypair invalidUser= BlockchainKeyGenerator.getInstance().generate();
        commitA(txTemp, invalidUser);
    }

    /**
     * 用类似keygen.sh方式生成一个新用户，注册至链上，然后使用其sign;
     */
    @Test
    public void registerNewUserThenSign(){
        //keygen.sh;
        AsymmetricKeypair kp = Crypto.getSignatureFunction("ED25519").generateKeypair();
        String base58PubKey = KeyGenUtils.encodePubKey(kp.getPubKey());
        byte[] pwdBytes = ShaUtils.hash_256(ByteArray.fromString("abc", "UTF-8"));
        String base58PwdKey = Base58Utils.encode(pwdBytes);
        String base58PrivKey = KeyGenUtils.encodePrivKey(kp.getPrivKey(), pwdBytes);
        System.out.println("pubKey="+base58PubKey);
        System.out.println("privKey="+base58PrivKey);
        System.out.println("base58PwdKey="+base58PwdKey);

        //根据如上提供的公私钥，将此用户注册至链;
        // 生成连接网关的账号
        PrivKey privKey = KeyGenUtils.decodePrivKey(base58PrivKey, base58PwdKey);
        PubKey pubKey = KeyGenUtils.decodePubKey(base58PubKey);
        BlockchainKeypair newAdminKey = new BlockchainKeypair(pubKey, privKey);
        //用原先网关的节点，将此newAdmin写入链;
        registerUser(null,newAdminKey);
        //用newAdmin签名;
        registerUser(newAdminKey,null);
    }

    @Test
    public void testSuit(){
        useCommitA = true;
        this.registerUserTest();
        this.insertData();
        this.executeContractOK();
        useCommitA = false;
        System.out.println("######userCommitA==false");
//        this.registerUserTest();
//        this.insertData();
//        this.executeContractOK();
//        getData(null);
    }

    /**
     * 哈希上链; 提取一个图片的hash值，然后上链；
     * 从链上获取这个信息，然后跟原图片对比，看是否被篡改;
     * 1) 原版图片生成hash，上链;
     * 2)从链上获得hash，然后从文件存储系统(内存模拟)获得图片，将图片重新生成hash，对比是否一致；
     * 3）假定图片存储系统有瑕疵，篡改图片，验证是否能够被识别;
     */
    @Test
    public void testHash2Ledger(){
        String zhizhaoPath = "zhizhao-OK.jpg";
        byte[] fileBytes = readChainCodes(zhizhaoPath);
        byte[] bytes = ShaUtils.hash_256(fileBytes);
        String zhizhaoHash256 = Hex.toHexString(bytes);
        System.out.println("zhizhaoHash256="+zhizhaoHash256);
        //将图片存储存储系统;
        Map<String,byte[]> fileStoreSys = new HashMap<>();
        fileStoreSys.put(zhizhaoHash256,fileBytes);

        BlockchainKeypair dataAccount = this.insertData(null,null);
        this.insertData(dataAccount,null,"zhizhao1",zhizhaoHash256,-1);
        //从区块链上获取企业执照对应的hash;
        TypedKVEntry[] kvData = blockchainService.getDataEntries(ledgerHash,
                dataAccount.getAddress().toBase58(), "zhizhao1");
        String hashInLedger = kvData[0].getValue().toString();
        System.out.println(String.format("zhizhao1 info:key=%s,value=%s,version=%d",
                kvData[0].getKey(),kvData[0].getValue().toString(),kvData[0].getVersion()));

        //从文件系统中根据hash值获得图片，然后进行对比;
        Assert.assertEquals(hashInLedger,
                Hex.toHexString(ShaUtils.hash_256(fileStoreSys.get(hashInLedger))));

        //对文件系统中图片做点手脚;
        zhizhaoPath = "zhizhao-fake.jpg";
        byte[] fakeZhizhaoBytes = readChainCodes(zhizhaoPath);
        fileStoreSys.put(zhizhaoHash256,fakeZhizhaoBytes);
        Assert.assertNotEquals(hashInLedger,
                Hex.toHexString(ShaUtils.hash_256(fileStoreSys.get(hashInLedger))));
    }

    @Test
    public void executeContract4DataAcount() {
        BlockchainIdentity contractIdentity =
                this.contractHandle(null,null,null,true,false);
        BlockchainKeypair dataAccount = this.insertData(null,null);

        System.out.println("###dataAccountIdentity's pubKey="+KeyGenUtils.encodePubKey(dataAccount.getPubKey()));
        System.out.println("###contractIdentity's pubKey="+KeyGenUtils.encodePubKey(contractIdentity.getPubKey()));
        this.contractHandle(null,null,contractIdentity,
                false,true, dataAccount.getIdentity(),"bizName","jd");
    }

    @Test
    public void execOldContract(){
        String dataAccountBase58PubKey = "3snPdw7i7PdPyJ44k4UhDCB5qcbJeBAfWd7yfpYanygcS34475Ajew";
        String ContractBase58PubKey= "3snPdw7i7PXgXvGcUAJJw2HSiRxcWZzp1UThEpsUeXLb6EBpKtgsBU";
        PubKey DataAccountPubKey = KeyGenUtils.decodePubKey(dataAccountBase58PubKey);
        PubKey ContractPubKey = KeyGenUtils.decodePubKey(ContractBase58PubKey);
        BlockchainIdentity dataAccount = new BlockchainIdentityData(DataAccountPubKey);
        BlockchainIdentity contractIdentity = new BlockchainIdentityData(ContractPubKey);
        System.out.println("dataAccountAddress="+dataAccount.getAddress().toBase58());
        System.out.println("contractAddress="+contractIdentity.getAddress().toBase58());

        this.contractHandle(null,null,contractIdentity,
                false,true, dataAccount,"bizName","jd1");
        this.contractHandle(null,null,contractIdentity,
                false,true, dataAccount,"bizName","jd2");
        this.contractHandle(null,null,contractIdentity,
                false,true, dataAccount,"bizName","jd3");
        String bizInfo = "{\"dest\":\"KA001\",\"id\":\"cc-fin01-01\",\"items\":\"FIN001|5000\",\"source\":\"FIN001\"}";
        this.contractHandle(null,null,contractIdentity,
                false,true, dataAccount,"bizInfo",bizInfo);
    }

    @Test
    public void insertDataByVersion() {
        BlockchainKeypair dataAccount = this.insertData(null,null);
        this.insertData(dataAccount,null,"key1","value1",-1);
        for(int i=0;i<5;i++){
            //get the version
            TypedKVEntry[] kvData = blockchainService.getDataEntries(ledgerHash,
                    dataAccount.getAddress().toBase58(), "k1");
            this.insertData(dataAccount,null,"k1","v"+i,kvData[0].getVersion());
        }

        TypedKVEntry[] kvData = blockchainService.getDataEntries(ledgerHash,
                dataAccount.getAddress().toBase58(), "k1");
        System.out.println(String.format("after loop, info:key=%s,value=%s,version=%d",
                kvData[0].getKey(),kvData[0].getValue().toString(),kvData[0].getVersion()));
    }

    //5.使用相同的用户注册;
    @Test
    public void test_registerUser1() {
        PrivKey privKey = KeyGenUtils.decodePrivKey("177gjskNxyjGaFVWHfVVngVGHzLf7YrHSBrw8hNRaqQSNoApR1sC1rjE92uM73NVrbLhRwK",
                "DYu3G8aGTMBW1WrTw76zxQJQU4DHLw9MLyy7peG4LKkY");
        PubKey pubKey = KeyGenUtils.decodePubKey("3snPdw7i7Phr1e5PPn61Z5VVLdy4PmMo6RnTpBSEmM5EZiUEFDcEja");
        BlockchainKeypair newUser = new BlockchainKeypair(pubKey, privKey);
        this.registerUser(null, newUser);
    }

    /**
     * 验证通过：mvn clean deploy，发布合约后，再执行合约;
     */
    @Test
    public void executeContract1(){
        Bytes contractAddress = Bytes.fromBase58("LdeNetUByGkkrY2WmtHsbpa2hKYNPiJ6rV3oN");
        // 注册一个数据账户
        BlockchainIdentity dataAccount = createDataAccount();
        String key = "jd_zhangsan";
        String value = "{\"dest\":\"KA006\",\"id\":\"cc-fin08-01\",\"items\":\"FIN001|3030\",\"source\":\"FIN001\"}";
        // 获取数据账户地址x
        String dataAddress = dataAccount.getAddress().toBase58();
        // 打印数据账户地址
        System.out.printf("DataAccountAddress = %s \r\n", dataAddress);
        System.out.println("return value = "+create1(contractAddress, dataAddress, key, value));

        System.out.println(getTxSigners(contractAddress));
    }

    @Test
    public void getContentByContentHash(){
        this.registerUser();
        LedgerTransaction ledgerTransactin = blockchainService.getTransactionByContentHash(ledgerHash,contentHash);
        System.out.println(ledgerTransactin.toString());
    }

    @Test
    public void getUserTotalCount(){
        blockchainService.getUserTotalCount(ledgerHash);
    }
}
