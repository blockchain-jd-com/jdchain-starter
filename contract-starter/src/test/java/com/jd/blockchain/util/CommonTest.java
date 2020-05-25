package com.jd.blockchain.util;

import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.utils.codec.Base58Utils;
import org.junit.Assert;
import org.junit.Test;

public class CommonTest {

    @Test
    public void testHashDegist(){
        String ledgerHash = "j5tjJfCBrssTCmweCcbfkgbhC54YviJTyE2dvTEaPjPwez";
        HashDigest contentHash = new HashDigest(Base58Utils.decode(ledgerHash));
        Assert.assertEquals(contentHash.toBase58(), ledgerHash);
    }
}
