package com.jd.blockchain.contract;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.crypto.SignatureFunction;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionTemplate;

public class SDKDemo_User extends SDK_Base_Demo{

	public static void main(String[] args) {
		SDKDemo_User sdkDemo_user = new SDKDemo_User();
		sdkDemo_user.registerUser();
	}
	/**
	 * 生成一个区块链用户，并注册到区块链；
	 */
	public BlockchainKeypair registerUser() {
		// 在本地定义注册账号的 TX；
		TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);
		//采用原始的方式来生成BlockchainKeypair;
//		SignatureFunction signatureFunction = Crypto.getSignatureFunction("ED25519");
//		AsymmetricKeypair cryptoKeyPair = signatureFunction.generateKeypair();
//		BlockchainKeypair user = new BlockchainKeypair(cryptoKeyPair.getPubKey(), cryptoKeyPair.getPrivKey());
		//采用KeyGenerator来生成BlockchainKeypair;
		BlockchainKeypair user = BlockchainKeyGenerator.getInstance().generate();
		System.out.println("user="+user.getAddress());

		txTemp.users().register(user.getIdentity());

		// TX 准备就绪；
		commit(txTemp);
		return user;
	}
}
