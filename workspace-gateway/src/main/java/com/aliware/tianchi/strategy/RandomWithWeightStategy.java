package com.aliware.tianchi.strategy;

import com.aliware.tianchi.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;

import java.util.Date;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Author: eamon
 * Email: eamon@eamon.cc
 * Time: 2019-07-15 16:46:35
 */
public class RandomWithWeightStategy extends AbstractStrategy {
    static {
        strategy = new RandomWithWeightStategy();
    }

    public static UserLoadBalanceStrategy getInstance() {
        return strategy;
    }

    public static UserLoadBalanceStrategy getInstance(String dataFrom) {
        strategy.dataFrom = dataFrom;
        return strategy;
    }

    @Override
    public int select(URL url, Invocation invocation) {
        int smallActiveCount;
        int mediumActiveCount;
        int largeActiveCount;

        if (dataFrom.equals("client")) {
            smallActiveCount = (int) Constants.longAdderSmall.longValue();
            mediumActiveCount = (int) Constants.longAdderMedium.longValue();
            largeActiveCount = (int) Constants.longAdderLarge.longValue();
        } else {
            smallActiveCount = Constants.activeThreadCount.get("small");
            mediumActiveCount = Constants.activeThreadCount.get("medium");
            largeActiveCount = Constants.activeThreadCount.get("large");
        }

        int randNumber = rand.nextInt(smallActiveCount + mediumActiveCount + largeActiveCount);
        if (randNumber < smallActiveCount) {
            System.out.println(new Date().getTime() + ":small:" + (Constants.activeThreadCount.get("small") + ":" + Constants.longAdderSmall.longValue()));
            return 0;
        } else if (randNumber >= smallActiveCount && randNumber < smallActiveCount + mediumActiveCount) {
            System.out.println(new Date().getTime() + ":medium:" + Constants.activeThreadCount.get("medium") + ":" + Constants.longAdderMedium.longValue());
            return 1;
        } else {
            System.out.println(new Date().getTime() + ":large:" + (Constants.activeThreadCount.get("large") + ":" + Constants.longAdderLarge.longValue()));
            return 2;
        }
    }

}
