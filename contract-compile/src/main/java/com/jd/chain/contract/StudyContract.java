package com.jd.chain.contract;

import com.jd.blockchain.contract.Contract;
import com.jd.blockchain.contract.ContractEvent;

/*设计一个功能，一个小孩，一门功课得了A，可奖励10元；功课得了B，可奖励5元；其它分数，不奖励。
最后需要能够查询到小孩最后的总奖励金额，以及得A的次数，得B的次数，得其它分数的次数。
 */
@Contract
public interface StudyContract {
    @ContractEvent(name = "create")
    String create(String dataAccount, String name, long initMoney);

    @ContractEvent(name = "modify")
    String modify(String dataAccount, String name, String score);

    @ContractEvent(name = "totalMoney")
    long totalMoney(String dataAccount, String name);

    @ContractEvent(name = "queryScore")
    long queryScore(String dataAccount, String name, String score);

    @ContractEvent(name = "test")
    String test(String dataAccount, String name);
}
