package com.jd.blockchain.contract;

import java.util.Arrays;

import static com.jd.blockchain.contract.SDKDemo_Constant.*;

/**
 * This is a test suit for jenkins;
 */
public class SDK4Jenkins extends SDK_Base_Demo {
    public SDK4Jenkins(){
    }
    public static void main(String[] args) {
        if(args == null || args.length!=5){
            System.out.println("param's format: ip port pubKey privKey password");
            return;
        }
        System.out.println(Arrays.toString(args));

        //update the params;
        GW_IPADDR = args[0];
        GW_PORT = Integer.parseInt(args[1]);
        GW_PUB_KEY = args[2];
        GW_PRIV_KEY = args[3];
        GW_PASSWORD = args[4];
        System.out.println(String.format("GW_IPADDR=%s,port=%d,pub-key=%s, priv-key=%s, password=%s",
                GW_IPADDR,GW_PORT,GW_PUB_KEY,GW_PRIV_KEY,GW_PASSWORD));

        //register user;
        SDKDemo_User.main(null);
        //insert data;
        SDKDemo_InsertData.main(null);

        SDK4Jenkins sdk4Jenkins = new SDK4Jenkins();
        sdk4Jenkins.handleContract();

    }

    public void handleContract(){
        //deploy and execute the contract;
        this.contractHandle(null,null,null,true,true);
    }
}
