package com.aliware.tianchi.strategy;

import com.aliware.tianchi.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;

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


    @Override
    public int select(URL url, Invocation invocation) {

        int smallActiveCount = (int) Constants.longAdderSmall.longValue() * 1;
        int mediumActiveCount = (int) Constants.longAdderMedium.longValue() * 2;
        int largeActiveCount = (int) Constants.longAdderLarge.longValue() * 3;

        int randNumber = rand.nextInt(smallActiveCount + mediumActiveCount + largeActiveCount);
        if (randNumber < smallActiveCount) {
            return 0;
        } else if (randNumber >= smallActiveCount && randNumber < smallActiveCount + mediumActiveCount) {
            return 1;
        } else {
            return 2;
        }
    }

}
