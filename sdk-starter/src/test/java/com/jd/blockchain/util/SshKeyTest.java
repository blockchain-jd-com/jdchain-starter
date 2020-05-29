package com.jd.blockchain.util;

import com.jd.blockchain.contract.SDK_Base_Demo;
import com.jd.blockchain.crypto.AsymmetricKeypair;
import com.jd.blockchain.crypto.CryptoAlgorithm;
import com.jd.blockchain.crypto.PrivKey;
import com.jd.blockchain.crypto.PubKey;
import com.jd.blockchain.crypto.service.classic.ClassicAlgorithm;
import com.jd.blockchain.crypto.utils.classic.RSAUtils;
import com.jd.blockchain.crypto.utils.classic.SSHKeyParser;
import com.jd.blockchain.ledger.BlockchainKeypair;
import com.jd.blockchain.ledger.PreparedTransaction;
import com.jd.blockchain.ledger.TransactionTemplate;
import org.bouncycastle.crypto.params.*;
import org.junit.Before;
import org.junit.Test;

import java.math.BigInteger;

/**
 * @author zhaogw
 * date 2019/8/26 18:18
 */
public class SshKeyTest extends SDK_Base_Demo {
    private SSHKeyParser parser;
    private static final CryptoAlgorithm RSA = ClassicAlgorithm.RSA;
    private static final CryptoAlgorithm ECDSA = ClassicAlgorithm.ECDSA;
    private static final CryptoAlgorithm ED25519 = ClassicAlgorithm.ED25519;

    @Before
    public void setup(){
        parser = new SSHKeyParser();
    }

    /**
     * 利用已知的公私钥构建用户，并提交;
     */
    @Test
    public void registerUser_Rsa_ByExistPubAndPrivKey() {
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);

        //use the existing pubKey and privKey;
        String rsaPubKeyWithHead = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQDoicH3x72soMmxQvSWTnJ7H+XdZVTKTtHHP5bzkiHd/Eay8m2/FHuUHtBLS6E1mqAiHYRIOzyMFGSGpSS73nou0iTJxhHgrxO+U+eSmHsn16nXQO73shvWRkbEzk+41qM7/9alC/7qqKAelnTOzC1NH5Y2hckZZMemFKS8EI5ET4oEPbFMTBGMsb71ENEkCXuM\n" +
                "fr4FiLZaKxOf9xnSfthddqQcSjEC5EV/VgZoHkCk3KNcdBAhepP4dshlb/UbkrVE9afvpSHmfu3o0OdY9rQUy59xv+4nhNRvdgGTkuFLV62ZeGCWrv8iM8ZVztSdwPIkuum8GIncPGvOAXHli0v5 root@vdevops";
        String rsaPrivKeyWithHead = "-----BEGIN RSA PRIVATE KEY-----\n" +
                "MIIEowIBAAKCAQEA6InB98e9rKDJsUL0lk5yex/l3WVUyk7Rxz+W85Ih3fxGsvJt\n" +
                "vxR7lB7QS0uhNZqgIh2ESDs8jBRkhqUku956LtIkycYR4K8TvlPnkph7J9ep10Du\n" +
                "97Ib1kZGxM5PuNajO//WpQv+6qigHpZ0zswtTR+WNoXJGWTHphSkvBCORE+KBD2x\n" +
                "TEwRjLG+9RDRJAl7jH6+BYi2WisTn/cZ0n7YXXakHEoxAuRFf1YGaB5ApNyjXHQQ\n" +
                "IXqT+HbIZW/1G5K1RPWn76Uh5n7t6NDnWPa0FMufcb/uJ4TUb3YBk5LhS1etmXhg\n" +
                "lq7/IjPGVc7UncDyJLrpvBiJ3DxrzgFx5YtL+QIDAQABAoIBACn+LpwLYxJYmvO6\n" +
                "7qZyFjRr5zva/7vlDfcYYa8stZb75tyL8QuvrD1sbxsZ1kvy13YxtGDC6cU2LY0v\n" +
                "J3oRzCjGZERX04SquTsGtewAQ0VHqfEoVb8/V82TNVZ3F2h+wk2kfuOhTfCccQX1\n" +
                "D7UEpC2oRZ0k/dLrIy63zaP0rWxiRJeGscyzhQhJtOw5r3QNF1Q+5aiqlfLf1RIa\n" +
                "7qmU9JkycYqbmIrYmGvwYrEGzcpiSry6xxlQ8GP2S03Zi7T/R3tHQzbknY23ejr2\n" +
                "6grPW2kRdMFaEPdsBcw2+QEv41EKoRyh7X2lwgjeZj8+hMx95yugS8bnxazETYKz\n" +
                "Nr+7ggECgYEA+sYOqBDT24WD5nTQoeCmhhYbNqRSUddHDyXmufxL00jkBQ3vb+Z8\n" +
                "qnAxFk07VreYI+zjO7aVr6vSeNq7LEfzJy/sCQ82pcld0gc3yxot+Wzg0JCOQIdd\n" +
                "BdlINr9BqbeTOlhLD6VNsRMD7Z7a3+kB0P/6w6SvhJxBTWW+/FhiUUECgYEA7WJo\n" +
                "t97W+yzHa2WcL+r4ShMez1rN8i7LiFxi8aj/0OHN8EG/CAHLwBRW/91sNUjJF3pZ\n" +
                "LXKdj1X+PuIN7zWh7I2ic++vWIYlXw6LLhMwgjvUyrWGgdWa9uEtfBcHYWQYKG2b\n" +
                "EWaikmCYfl7mMqJ5nZLn60XnjEHoE+UIIglFlLkCgYAhqkEBcvDeAXiJkZkIgj4Y\n" +
                "thMFLN4YOoxBjlcBFMuhG6Hww8QnA4ws3U7ZrZiPtano+L4wn/xMTwa8TrDd6vLL\n" +
                "ifVn6fDuJLBBp9Jno6YUW/d6SE2wf/uO83JsVxSbkhdjt8Kr4QcjBGh8l9zOPluO\n" +
                "ES864HrnlIp3t3cCLWNVgQKBgQCEBCmWsEBA2cthx9WPtI42q+GDkc1c9XdNs4t3\n" +
                "N/Rk118vUXEK4+zLOsSTcTG/hGYJP4qewtIrJ7jIgfEb6mjN5IrHDTIn+hqIfc8b\n" +
                "ZaKhmuKhhUhzpjaTO/GOxEPffsoMmcAWckzRORU1mlNNL5yQ7GU8K9k04GZPwhy+\n" +
                "yQdASQKBgCJZsMdl8DahYQFhROsmL4uRj+LoBz4B5z9T14qUsOp5cWvZgoNcjvkk\n" +
                "Dr4zs6i1t6KeE8RMmRdw37hBk4y/JF2PSXW8BpoCq40zLp4PsKpsuGm7dFv6PEGN\n" +
                "hqXl6n4ME4b/sW805nEjN8btczxFktXLZHePPAUC08j41Xc+3x1P\n" +
                "-----END RSA PRIVATE KEY-----\n";

        RSAKeyParameters pubKey = (RSAKeyParameters) parser.pubKeyParse(rsaPubKeyWithHead);
        RSAPrivateCrtKeyParameters privKey = (RSAPrivateCrtKeyParameters) parser.privKeyParse(rsaPrivKeyWithHead);

        byte[] pubKeyBytes = RSAUtils.pubKey2Bytes_RawKey(pubKey);
        byte[] privKeyBytes = RSAUtils.privKey2Bytes_RawKey(privKey);

        AsymmetricKeypair asymmetricKeypair = new AsymmetricKeypair(new PubKey(RSA, pubKeyBytes), new PrivKey(RSA, privKeyBytes));
        BlockchainKeypair user = new BlockchainKeypair(asymmetricKeypair.getPubKey(), asymmetricKeypair.getPrivKey());
        System.out.println("user="+user.getAddress());
        txTemp.users().register(user.getIdentity());

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();
        prepTx.sign(adminKey);

        // 提交交易；
        prepTx.commit();
    }

    /**
     * 利用已知的公私钥构建用户，并提交;
     * ssh-keygen -t ecdsa
     */
    @Test
    public void registerUser_ecdsa_ByExistPubAndPrivKey() {
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);

        //use the existing pubKey and privKey;
        String ecdsaPubKeyWithHead = "ecdsa-sha2-nistp256 AAAAE2VjZHNhLXNoYTItbmlzdHAyNTYAAAAIbmlzdHAyNTYAAABBBOTKTmCWGBAx/ibQG15olqqubOIouSVcqzdLzA8IXX0eiaDX5WD+6GYX+4jdXLN8Ppkznu7BD0eVk5sH/sjzXHQ= root@vdevops";
        String ecdsaPrivKeyWithHead = "-----BEGIN EC PRIVATE KEY-----\n" +
                "MHcCAQEEIC4oJ9UTT2Bd/NZsuu2JsJR8cy5wLNgBKpy0ujQ9hCqcoAoGCCqGSM49\n" +
                "AwEHoUQDQgAE5MpOYJYYEDH+JtAbXmiWqq5s4ii5JVyrN0vMDwhdfR6JoNflYP7o\n" +
                "Zhf7iN1cs3w+mTOe7sEPR5WTmwf+yPNcdA==\n" +
                "-----END EC PRIVATE KEY-----";

        ECPublicKeyParameters pubKey = (ECPublicKeyParameters) parser.pubKeyParse(ecdsaPubKeyWithHead);
        ECPrivateKeyParameters privKey = (ECPrivateKeyParameters) parser.privKeyParse(ecdsaPrivKeyWithHead);

        byte[] privKeyBytes = BigIntegerTo32Bytes(privKey.getD());
        byte[] pubKeyBytes = pubKey.getQ().getEncoded(false);
        AsymmetricKeypair asymmetricKeypair = new AsymmetricKeypair(new PubKey(ECDSA, pubKeyBytes), new PrivKey(ECDSA, privKeyBytes));
        BlockchainKeypair user = new BlockchainKeypair(asymmetricKeypair.getPubKey(), asymmetricKeypair.getPrivKey());
        System.out.println("user="+user.getAddress());
        txTemp.users().register(user.getIdentity());

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();
        prepTx.sign(adminKey);

        // 提交交易；
        prepTx.commit();
    }

    // To convert BigInteger to byte[] whose length is 32
    private static byte[] BigIntegerTo32Bytes(BigInteger b){
        byte[] tmp = b.toByteArray();
        byte[] result = new byte[32];
        if (tmp.length > result.length) {
            System.arraycopy(tmp, tmp.length - result.length, result, 0, result.length);
        }
        else {
            System.arraycopy(tmp,0,result,result.length-tmp.length,tmp.length);
        }
        return result;
    }

    /**
     * 利用已知的公私钥构建用户，并提交;
     * ssh-keygen -t ecdsa
     */
    @Test
    public void registerUser_ed25519_ByExistPubAndPrivKey() {
        // 在本地定义注册账号的 TX；
        TransactionTemplate txTemp = blockchainService.newTransaction(ledgerHash);

        //use the existing pubKey and privKey;
        String ed25519PubKeyWithHead = "ssh-ed25519 AAAAC3NzaC1lZDI1NTE5AAAAIIaznHMQxUjkiUHHF1Go6JxPwsmRhms5wALiimI5anxe root@vdevops";
        String ed25519PrivKeyWithHead = "-----BEGIN OPENSSH PRIVATE KEY-----\n" +
                "b3BlbnNzaC1rZXktdjEAAAAABG5vbmUAAAAEbm9uZQAAAAAAAAABAAAAMwAAAAtzc2gtZW\n" +
                "QyNTUxOQAAACCGs5xzEMVI5IlBxxdRqOicT8LJkYZrOcAC4opiOWp8XgAAAJBi8qFPYvKh\n" +
                "TwAAAAtzc2gtZWQyNTUxOQAAACCGs5xzEMVI5IlBxxdRqOicT8LJkYZrOcAC4opiOWp8Xg\n" +
                "AAAEADWOn377p12if6kNbbejJmH9v9flwoqTZDw98/XvcW8oaznHMQxUjkiUHHF1Go6JxP\n" +
                "wsmRhms5wALiimI5anxeAAAADHJvb3RAdmRldm9wcwE=\n" +
                "-----END OPENSSH PRIVATE KEY-----";

        Ed25519PublicKeyParameters pubKey = (Ed25519PublicKeyParameters) parser.pubKeyParse(ed25519PubKeyWithHead);
        Ed25519PrivateKeyParameters privKey = (Ed25519PrivateKeyParameters) parser.privKeyParse(ed25519PrivKeyWithHead);

        byte[] privKeyBytes = privKey.getEncoded();
        byte[] pubKeyBytes = pubKey.getEncoded();

        AsymmetricKeypair asymmetricKeypair = new AsymmetricKeypair(new PubKey(ED25519, pubKeyBytes), new PrivKey(ED25519, privKeyBytes));
        BlockchainKeypair user = new BlockchainKeypair(asymmetricKeypair.getPubKey(), asymmetricKeypair.getPrivKey());
        System.out.println("user="+user.getAddress());
        txTemp.users().register(user.getIdentity());

        // TX 准备就绪；
        PreparedTransaction prepTx = txTemp.prepare();
        prepTx.sign(adminKey);

        // 提交交易；
        prepTx.commit();
    }
}
