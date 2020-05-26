//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.jd.chain.contract;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;

@Contract
public interface MyContract {
    @ContractEvent(
        name = "put"
    )
    String put(String var1, String var2, String var3, long var4);

    @ContractEvent(
        name = "get"
    )
    String get(String var1, String var2, String var3);

    @ContractEvent(
        name = "read"
    )
    String read(String var1, String var2, String var3);

    @ContractEvent(
        name = "readAll"
    )
    String readAll(String var1, String var2, String var3);
}
