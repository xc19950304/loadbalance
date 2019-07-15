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

        int smallActiveCount = Constants.activeThreadCount.get("small");
        int mediumActiveCount = (int) (Constants.activeThreadCount.get("medium") * 1.5);
        int largeActiveCount = Constants.activeThreadCount.get("large") * 2;
       Random rand =new Random( );

        int randNumber = rand.nextInt(smallActiveCount + mediumActiveCount + largeActiveCount);
        if(randNumber < smallActiveCount)
        {
            return 0;
        }
        else if(randNumber >= smallActiveCount && randNumber < smallActiveCount + mediumActiveCount)
        {
            return 1;
        }
        else
            return 2;
    }

}
