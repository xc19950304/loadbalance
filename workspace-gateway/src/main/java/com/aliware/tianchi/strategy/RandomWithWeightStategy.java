package com.aliware.tianchi.strategy;

import com.aliware.tianchi.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Author: eamon
 * Email: eamon@eamon.cc
 * Time: 2019-07-15 16:46:35
 */
public class RandomWithWeightStategy implements UserLoadBalanceStrategy {

    private static RandomWithWeightStategy strategy = new RandomWithWeightStategy();

    public static RandomWithWeightStategy getInstance(){
        return strategy;
    }

    @Override
    public int select(URL url, Invocation invocation) {
       Random rand =new Random( );

        int randNumber = rand.nextInt(Constants.activeThreadCount.get("small") + Constants.activeThreadCount.get("medium") + Constants.activeThreadCount.get("large"));
        if(randNumber < Constants.activeThreadCount.get("small"))
        {
            return 0;
        }
        else if(randNumber >= Constants.activeThreadCount.get("small") && randNumber < Constants.activeThreadCount.get("small") + Constants.activeThreadCount.get("medium"))
        {
            return 1;
        }
        else
            return 2;
    }

}
