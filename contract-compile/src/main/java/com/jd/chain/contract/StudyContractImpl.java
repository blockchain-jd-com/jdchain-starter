package com.jd.chain.contract;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.EventProcessingAware;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.KVDataVO;
import com.jd.blockchain.ledger.KVInfoVO;
import com.jd.blockchain.ledger.TypedKVEntry;

public class StudyContractImpl implements EventProcessingAware, StudyContract{

    private ContractEventContext eventContext;
    private HashDigest hashDigest;

    @Override
    public void beforeEvent(ContractEventContext eventContext) {
        this.eventContext = eventContext;
        this.hashDigest = eventContext.getCurrentLedgerHash();
    }

    @Override
    public void postEvent(ContractEventContext eventContext, Exception error) {

    }

    @Override
    public String create(String dataAccount, String name, long initMoney) {
        TypedKVEntry[] typedKVEntries = eventContext.getLedger().getDataEntries(hashDigest, dataAccount, name);
        //判断name是否存在
        if (typedKVEntries != null && typedKVEntries.length > 0) {
            //就算没有这个name，typedKVEntries也会返回一个值，只不过version=-1，value=null
            if (typedKVEntries[0].getVersion() > -1) {
                //说明name已经存在了，并且存过值了
                throw new IllegalStateException(String.format("Account %s is already existed in %s", name, dataAccount));
            } else {
                eventContext.getLedger().dataAccount(dataAccount).setInt64(name, initMoney, -1L);
            }
        } else {
            throw new IllegalStateException(String.format("Ledger[%s] inner Error !!!", hashDigest.toBase58()));
        }
        return String.format("DataAccountAddress[%s] -> Create(By Contract Operation) Account = %s Success!!! \r\n", dataAccount, name);
    }

    @Override
    public String modify(String dataAccount, String name, String score) {
        long award = 0;
        if ("A".equals(score)) {
            award = 10;
        } else if ("B".equals(score)) {
            award = 5;
        }

        if (award != 0) {
            TypedKVEntry[] typedKVEntries = eventContext.getLedger().getDataEntries(hashDigest, dataAccount, name);
            if (typedKVEntries != null && typedKVEntries.length > 0) {
                long currentVersion = typedKVEntries[0].getVersion();
                if (currentVersion == -1) {
                    //name这个账户还没被创建
                    eventContext.getLedger().dataAccount(dataAccount).setInt64(name, award, -1L);
                } else {
                    //有name这个账户了
                    long currentMoney = (long) typedKVEntries[0].getValue();
                    long totalMoney = currentMoney + award;
                    eventContext.getLedger().dataAccount(dataAccount).setInt64(name, totalMoney, currentVersion);
                }
                return String.format("%s dollar has been added to %s account.", award, name);
            } else {
                throw new IllegalStateException(String.format("Ledger[%s] inner Error !!!", hashDigest.toBase58()));
            }
        } else {
            return String.format("%s account not change.", name);
        }
    }

    @Override
    public long totalMoney(String dataAccount, String name) {
        TypedKVEntry[] typedKVEntries = eventContext.getLedger().getDataEntries(hashDigest, dataAccount, name);
        if (typedKVEntries != null & typedKVEntries.length > 0) {
            if (typedKVEntries[0].getVersion() == -1) {
                return 0;
            } else {
                return (long) typedKVEntries[0].getValue();
            }
        } else {
            throw new IllegalStateException(String.format("Ledger[%s] inner Error !!!", hashDigest.toBase58()));
        }
    }

    @Override
    public long queryScore(String dataAccount, String name, String score) {
        TypedKVEntry[] typedKVEntries = eventContext.getLedger().getDataEntries(hashDigest, dataAccount, name);
        if (typedKVEntries == null || typedKVEntries.length == 0) {
            throw new IllegalStateException(String.format("Ledger[%s] inner Error !!!", hashDigest.toBase58()));
        } else {
            long currentVersion = typedKVEntries[0].getVersion();
            if (currentVersion == -1) {
                return 0;
            } else {
                long[] versions = new long[(int) currentVersion + 1];
                for (int i = 0 ; i <= currentVersion; i ++) {
                    versions[i] = i;
                }
                //kvDataVO包括一个key：name和所有的version，即versions数组
                KVDataVO kvDataVO = new KVDataVO(name, versions);
                //kvDataVOS数组可以包含很多个kvDataVO，本例中只有一个kvDataVO；如果多个的话就是KVDataVO[] kvDataVOS = new KVDataVO[] {kvDataVO, ......};
                KVDataVO[] kvDataVOS = new KVDataVO[] {kvDataVO};
                //把kvDataVOS数组封装成一个kvInfoVO对象
                KVInfoVO kvInfoVO = new KVInfoVO(kvDataVOS);
                TypedKVEntry[] allHistoryDatas = eventContext.getLedger().getDataEntries(hashDigest, dataAccount, kvInfoVO);

                long certainScoreNumber = 0;
                long comparedMoney = 0;
                if ("A".equals(score)) {
                    comparedMoney = 10;
                } else if ("B".equals(score)) {
                    comparedMoney = 5;
                }
                for (int i = 0; i <= currentVersion; i++) {
                    long delta = 0;
                    if (i == 0) {
                        delta = (long) allHistoryDatas[i].getValue();
                    } else {
                        delta = (long) allHistoryDatas[i].getValue() - (long) allHistoryDatas[i-1].getValue();
                    }
                    if (delta == comparedMoney) {
                        certainScoreNumber++;
                    }
                }
                return certainScoreNumber;
            }
        }
    }

    @Override
    public String test(String dataAccount, String name) {
        return "This is a test by Qiye";
    }
}
