package com.jd.blockchain.contract;

import com.jd.blockchain.ledger.*;
import com.jd.blockchain.transaction.GenericValueHolder;
import com.jd.blockchain.transaction.LongValueHolder;
import com.jd.blockchain.utils.Bytes;
import com.jd.chain.contract.TransferContract;

import static com.jd.blockchain.contract.SDKDemo_Constant.readChainCodes;
import static com.jd.blockchain.transaction.ContractReturnValue.decode;

public class SDK_Contract_Demo extends SDK_Base_Demo {

	public static void main(String[] args) {
		new SDK_Contract_Demo().executeContract();
	}

	public void executeContract() {

		// 发布jar包
		// 定义交易模板
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);

		// 将jar包转换为二进制数据
		byte[] contractCode = readChainCodes("contract-JDChain-Contract.jar");

		// 生成一个合约账号
		BlockchainKeypair contractDeployKey = BlockchainKeyGenerator.getInstance().generate();

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
		String account0 = "jd_zhangsan", account1 = "jd_lisi";
		long account0Money = 3000L, account1Money = 2000L;
		// 创建两个账户
		// 使用KV操作创建一个账户
		System.out.println(create(dataAddress, account0, account0Money, false, null));
		// 使用合约创建一个账户
		System.out.println(create(dataAddress, account1, account1Money, true, contractAddress));

		// 转账，使得双方钱达到一致
		System.out.println(transfer(dataAddress, account0, account1, 500L, contractAddress));

		// 通过合约读取account0的当前信息
		System.out.printf("Read DataAccountAddress[%s] Account = %s 's money = %s (By Contract)\r\n", dataAddress,
				account0, readByContract(dataAddress, account0, contractAddress));
		// 通过KV读取account1的当前信息
		System.out.printf("Read DataAccountAddress[%s] Account = %s 's money = %s (By KV Operation)\r\n", dataAddress,
				account1, readByKvOperation(dataAddress, account1));

		// 通过合约读取account0的历史信息
		System.out.println(readAll(dataAddress, account0, contractAddress));
		// 通过合约读取account1的历史信息
		System.out.println(readAll(dataAddress, account1, contractAddress));
	}

	private String readAll(String address, String account, Bytes contractAddress) {
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		// 使用合约创建
		TransferContract transferContract = txTpl.contract(contractAddress, TransferContract.class);
		GenericValueHolder<String> result = decode(transferContract.readAll(address, account));
		commitA(txTpl);
		return result.get();
	}

	private long readByContract(String address, String account, Bytes contractAddress) {
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		// 使用合约创建
		TransferContract transferContract = txTpl.contract(contractAddress, TransferContract.class);
		LongValueHolder result = decode(transferContract.read(address, account));
		commitA(txTpl);
		return result.get();
	}

	private long readByKvOperation(String address, String account) {
		TypedKVEntry[] kvDataEntries = blockchainService.getDataEntries(ledgerHash, address, account);
		if (kvDataEntries == null || kvDataEntries.length == 0) {
			throw new IllegalStateException(String.format("Ledger %s Service inner Error !!!", ledgerHash.toBase58()));
		}
		TypedKVEntry kvDataEntry = kvDataEntries[0];
		if (kvDataEntry.getVersion() == -1) {
			return 0L;
		}
		return (long) (kvDataEntry.getValue());
	}

	private String transfer(String address, String from, String to, long money, Bytes contractAddress) {
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		// 使用合约创建
		TransferContract transferContract = txTpl.contract(contractAddress, TransferContract.class);
		GenericValueHolder<String> result = decode(transferContract.transfer(address, from, to, money));
		commitA(txTpl);
		return result.get();
	}

	private String create(String address, String account, long money, boolean useContract, Bytes contractAddress) {
		TransactionTemplate txTpl = blockchainService.newTransaction(ledgerHash);
		if (useContract) {
			// 使用合约创建
			TransferContract transferContract = txTpl.contract(contractAddress, TransferContract.class);
			GenericValueHolder<String> result = decode(transferContract.create(address, account, money));
			commitA(txTpl);
			return result.get();
		} else {
			// 通过KV创建
			txTpl.dataAccount(address).setInt64(account, money, -1);
			TransactionResponse txResp = commitA(txTpl);
			return String.format(
					"DataAccountAddress[%s] -> Create(By KV Operation) Account = %s and Money = %s Success!!! \r\n",
					address, account, money);
		}
	}
}
