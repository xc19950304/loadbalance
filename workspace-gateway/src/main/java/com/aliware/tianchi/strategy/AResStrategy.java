package com.aliware.tianchi.strategy;

import com.aliware.tianchi.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;

import java.util.PriorityQueue;

/**
 * Author: eamon
 * Email: eamon@eamon.cc
 * Time: 2019-07-15 16:30:53
 */
public class AResStrategy implements UserLoadBalanceStrategy{

    private static AResStrategy strategy = new AResStrategy();

    public static AResStrategy getInstance(){
        return strategy;
    }

    @Override
    public int select(URL url, Invocation invocation) {
        int smallActiveCount = Constants.activeThreadCount.get("small");
        int mediumActiveCount = Constants.activeThreadCount.get("medium");
        int largeActiveCount = Constants.activeThreadCount.get("large");

        PriorityQueue<Double> queue = new PriorityQueue<>((o1, o2) -> o2.compareTo(o1));
        double k1 = Math.log(Math.random()) / ( smallActiveCount * 1);
        queue.offer(k1);
        double k2 = Math.log(Math.random()) / ( mediumActiveCount * 1.5 );
        queue.offer(k2);
        double k3 = Math.log(Math.random()) / (largeActiveCount * 2);
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
