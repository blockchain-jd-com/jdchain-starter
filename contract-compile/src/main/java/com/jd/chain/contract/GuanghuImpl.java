package com.jd.chain.contract;

import com.jd.blockchain.contract.ContractEventContext;
import com.jd.blockchain.contract.EventProcessingAware;
import com.jd.blockchain.crypto.HashDigest;
import com.jd.blockchain.ledger.TypedKVEntry;

/*
 * 广互链码实现类 add by syf 2019.09.03
 * */
public class GuanghuImpl implements EventProcessingAware,Guanghu {
    private ContractEventContext eventContext;
    private HashDigest ledgerHash;

    @Override
    public String putvalBifurcation(String address, String account, String content, String isHalf) {
        TypedKVEntry[] kvDataEntries=eventContext.getLedger().getDataEntries(ledgerHash,address,account);
        if(kvDataEntries!=null && kvDataEntries.length>0){
            long currVersion = kvDataEntries[0].getVersion();
            if (currVersion > -1) {
                throw new IllegalStateException(String.format("%s -> %s already have created !!!", address, account));
            }
            eventContext.getLedger().dataAccount(address).setText(account,content,-1L);
        }

        String userDir = System.getProperty("user.dir");
        //contruct 2:2;
        if("half".equals(isHalf)){
            if(userDir.contains("peer0") || userDir.contains("peer1")){
//                System.out.println("2:2,curNode=peer0/1");
                eventContext.getLedger().dataAccount(address).setText(account+"-peer","01",-1L);
            }else {
//                System.out.println("2:2,curNode=peer2/3");
                eventContext.getLedger().dataAccount(address).setText(account+"-peer","23",-1L);
            }
        }else {
            //contruct 3:1;
            if(userDir.contains("peer0") || userDir.contains("peer1") || userDir.contains("peer2")){
//                System.out.println("curNode=peer0/1/2");
                eventContext.getLedger().dataAccount(address).setText(account+"-peer","012",-1L);
            }else {
//                System.out.println("curNode=peer3");
                eventContext.getLedger().dataAccount(address).setText(account+"-peer","3",-1L);
            }
        }

        return String.format("DataAccountAddress[%s] -> Create(By Contract Operation) Account = %s and Money = %s Success!!! \r\n",
                address, account, content);
    }

    @Override
    public String putval(String address, String account, String content) {
        TypedKVEntry[] kvDataEntries=eventContext.getLedger().getDataEntries(ledgerHash,address,account);
        if(kvDataEntries!=null && kvDataEntries.length>0){
            long currVersion = kvDataEntries[0].getVersion();
            if (currVersion > -1) {
                throw new IllegalStateException(String.format("%s -> %s already have created !!!", address, account));
            }
            eventContext.getLedger().dataAccount(address).setText(account,content,-1L);
        }

        return String.format("DataAccountAddress[%s] -> Create(By Contract Operation) Account = %s and Money = %s Success!!! \r\n",
                address, account, content);
    }

    @Override
    public String putval(String address, String account, String content, Long time) {
        TypedKVEntry[] kvDataEntries=eventContext.getLedger().getDataEntries(ledgerHash,address,account);
        if(kvDataEntries!=null && kvDataEntries.length>0){
            long currVersion = kvDataEntries[0].getVersion();
            eventContext.getLedger().dataAccount(address).setText(account,content,currVersion);
        }

        return String.format("DataAccountAddress[%s] -> Create(By Contract Operation) key = %s and value = %s  and time= %d Success!!! \r\n",
                address, account, content, time);
    }

    @Override
    public String getval(String address, String account) {
        TypedKVEntry[] kvDataEntries = eventContext.getLedger().getDataEntries(ledgerHash, address, account);
        if (kvDataEntries == null || kvDataEntries.length == 0) {
            return "";
        }
        return kvDataEntries[0].getValue().toString();
    }

    @Override
    public void beforeEvent(ContractEventContext contractEventContext) {
        this.eventContext = contractEventContext;
        this.ledgerHash = contractEventContext.getCurrentLedgerHash();
    }

    @Override
    public void postEvent(ContractEventContext contractEventContext, Exception e) {

    }
}
