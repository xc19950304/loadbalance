package com.aliware.tianchi.strategy;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;

import java.util.Random;

public class BasicStrategy implements UserLoadBalanceStrategy{

    private static BasicStrategy strategy = new BasicStrategy();

    public static BasicStrategy getInstance(){
        return strategy;
    }

    @Override
    public int select(URL url, Invocation invocation) {

        Random rand =new Random( );

        int randNumber = rand.nextInt(200 + 450 + 650);
        if(randNumber < 200)
        {
            return 0;
        }
        else if(randNumber >= 200 && randNumber < 650)
        {
            return 1;
        }
        else
            return 2;
    }
}
