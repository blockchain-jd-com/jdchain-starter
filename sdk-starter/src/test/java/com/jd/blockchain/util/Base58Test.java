package com.jd.blockchain.util;

import com.jd.blockchain.utils.codec.Base58Utils;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.util.Random;

public class Base58Test {

    //base58效率验证;
    @Test
    public void testBase58(){
        System.out.println("字节数组转为字符串...");
        for (int i=1;i<=256;i=i*2){
            String fileName= "1-"+i+"k";
            byte[] arr = new byte[i*1024];
            new Random().nextBytes(arr);
            long startTime = System.currentTimeMillis();
            Base64.encodeBase64String(arr);
            System.out.println("base64,"+fileName+",spendTime="+(System.currentTimeMillis()-startTime));
            startTime = System.currentTimeMillis();
            Base58Utils.encode(arr);
            System.out.println("base58,"+fileName+",spend time="+(System.currentTimeMillis()-startTime));
        }
    }
}
