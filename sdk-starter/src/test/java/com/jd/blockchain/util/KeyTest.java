package com.jd.blockchain.util;

import com.jd.blockchain.crypto.*;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.TypedValue;
import com.jd.blockchain.transaction.KVData;
import com.jd.blockchain.utils.codec.Base58Utils;
import com.jd.blockchain.utils.io.ByteArray;
import com.jd.blockchain.utils.security.ShaUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.UnsupportedEncodingException;

import static com.jd.blockchain.crypto.KeyGenUtils.encodePubKey;
import static com.jd.blockchain.crypto.KeyGenUtils.encryptPrivKey;

/**
 * @author zhaogw
 * date 2019/7/26 13:39
 */
public class KeyTest {

    @Test
    public void genKeyPlusPasswd(){
        String pubKeyStr = "3snPdw7i7PbUX47tXkHVuRd2mP8mXhEJbaBM441h9wa67TAeMfiqUX";
        String privKeyStr = "177gjuzAyvF8W2KYST8tVPmvGBsPLhXsf55HpHxSbHF7Va995ekXvxjNimEYNt5wP6GxTpW";
        String passwd = "abc";
        // 生成连接网关的账号
        PrivKey privKey = KeyGenUtils.decodePrivKeyWithRawPassword(privKeyStr, passwd);
        PubKey pubKey = KeyGenUtils.decodePubKey(pubKeyStr);
        BlockchainKeypair adminKey = new BlockchainKeypair(pubKey, privKey);

        Assert.assertEquals(pubKeyStr,encodePubKey(adminKey.getPubKey()));
        byte[] pwdBytes = ShaUtils.hash_256(passwd.getBytes());
        Assert.assertEquals(privKeyStr,encodePrivKey(adminKey.getPrivKey(),pwdBytes));
    }

    public static String encodePrivKey(PrivKey privKey, byte[] pwdBytes) {
        byte[] encodedPrivKeyBytes = encryptPrivKey(privKey, pwdBytes);
        String base58PrivKey = Base58Utils.encode(encodedPrivKeyBytes);
        return base58PrivKey;
    }

    /**
     * 生成一组跟keygen.sh -n xxx 结果已知的公私钥;
     */
    @Test
    public void keygenTest(){
        AsymmetricKeypair kp = Crypto.getSignatureFunction("ED25519").generateKeypair();
        String base58PubKey = KeyGenUtils.encodePubKey(kp.getPubKey());
        byte[] pwdBytes = ShaUtils.hash_256(ByteArray.fromString("abc", "UTF-8"));
        String base58PwdKey = Base58Utils.encode(pwdBytes);
        String base58PrivKey = KeyGenUtils.encodePrivKey(kp.getPrivKey(), pwdBytes);
        System.out.println("pubKey="+base58PubKey);
        System.out.println("privKey="+base58PrivKey);
        System.out.println("base58PwdKey="+base58PwdKey);
    }

    @Test
    public void checkBytesData() throws UnsupportedEncodingException {
        String keyStr = "key2";
        String valueStr = "value2";
        TypedValue bytesValue = TypedValue.fromText(valueStr);
        System.out.println(bytesValue.getBytes().toUTF8String());
        KVData kvdata = new KVData(keyStr, bytesValue, 0);
        System.out.println(kvdata.getValue().toString());
        String base58Str = bytesValue.getBytes().toBase58();
        System.out.println("base58Str = "+base58Str);
        Assert.assertEquals(new String(Base58Utils.decode(base58Str),"utf-8"),valueStr);
    }
}
