package com.aliware.tianchi.strategy;

import com.aliware.tianchi.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;

import java.util.Date;
import java.util.PriorityQueue;

/**
 * Author: eamon
 * Email: eamon@eamon.cc
 * Time: 2019-07-15 16:30:53
 */
public class AResStrategy extends AbstractStrategy {

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
        int smallActiveCount = Constants.activeThreadCount.get("small");
        int mediumActiveCount = Constants.activeThreadCount.get("medium");
        int largeActiveCount = Constants.activeThreadCount.get("large");
        if (dataFrom.equals("client")) {
            smallActiveCount = (int) Constants.longAdderSmall.longValue();
            mediumActiveCount = (int) Constants.longAdderMedium.longValue();
            largeActiveCount = (int) Constants.longAdderLarge.longValue();
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
            System.out.println(new Date().getTime() + ":small:" + (Constants.activeThreadCount.get("small") + ":" + Constants.longAdderSmall.longValue()));
            return 0;
        }
        if (result == k2) {
            System.out.println(new Date().getTime() + ":medium:" + Constants.activeThreadCount.get("medium") + ":" + Constants.longAdderMedium.longValue());
            return 1;
        }
        System.out.println(new Date().getTime() + ":large:" + (Constants.activeThreadCount.get("large") + ":" + Constants.longAdderLarge.longValue()));
        return 2;
    }
}
