package com.jd.blockchain.util;

import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.Crypto;
import com.jd.blockchain.ledger.BlockchainKeyGenerator;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.security.ShaUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.jd.blockchain.crypto.KeyGenUtils.encodePrivKey;
import static com.jd.blockchain.crypto.KeyGenUtils.encodePubKey;

/**
 * @author zhaogw
 * @date 2020/8/5 10:56
 */
public class SM2Test {
    private static final Logger logger = LoggerFactory.getLogger(SM2Test.class);

    @Test
    public void testGenSm2(){
        this.generateKeyPair();
    }

    /**
     * 生成密钥，要求输入密码用于保护私钥文件；
     */
    private void generateKeyPair() {
        //use generate;
        BlockchainKeypair blockchainKeypair = BlockchainKeyGenerator.getInstance().generate("SM2");
        //register user by blockchainKeypair.getIdentity();
        logger.info("generate by SM2, address={}",blockchainKeypair.getIdentity().getAddress());

        //use another method;
        AsymmetricKeypair kp = Crypto.getSignatureFunction("SM2").generateKeypair();
        String base58PubKey = encodePubKey(kp.getPubKey());
        String pwd="abc";
        byte[] pwdBytes = ShaUtils.hash_256(ByteArray.fromString(pwd, "UTF-8"));
        String base58PrivKey = encodePrivKey(kp.getPrivKey(), pwdBytes);
        String base58PwdKey = Base58Utils.encode(pwdBytes);
        logger.info("pubKey={}, privKey={}, pwdKey={}",base58PubKey,base58PrivKey,base58PwdKey);
    }
}
