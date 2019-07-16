package com.aliware.tianchi.strategy;

import com.aliware.tianchi.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.Invocation;

import java.util.Date;
import java.util.PriorityQueue;

/**
 * Author: eamon
 * Email: eamon@eamon.cc
 * Time: 2019-07-15 16:30:53
 */
public class AResStrategy extends AbstractStrategy {
    private static final Logger LOGGER = LoggerFactory.getLogger(AResStrategy.class);


    private static AResStrategy strategy = new AResStrategy();

    public static AResStrategy getInstance() {
        return strategy;
    }

    public static AResStrategy getInstance(String dataFrom) {
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

        PriorityQueue<Double> queue = new PriorityQueue<>((o1, o2) -> o2.compareTo(o1));
        double k1 = Math.log(rand.nextDouble()) / (smallActiveCount * 1);
        queue.offer(k1);
        double k2 = Math.log(rand.nextDouble()) / (mediumActiveCount * 1.5);
        queue.offer(k2);
        double k3 = Math.log(rand.nextDouble()) / (largeActiveCount * 2);
        queue.offer(k3);

        double result = queue.poll();

        if (result == k1) {
            return 0;
        }
        if (result == k2) {
            return 1;
        }
        return 2;
    }
}
