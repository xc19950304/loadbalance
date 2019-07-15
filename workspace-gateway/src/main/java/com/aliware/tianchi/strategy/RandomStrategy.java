package com.aliware.tianchi.strategy;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Author: eamon
 * Email: eamon@eamon.cc
 * Time: 2019-07-15 16:43:01
 */
public class RandomStrategy implements UserLoadBalanceStrategy {

    private static RandomStrategy strategy = new RandomStrategy();

    public static RandomStrategy getInstance(){
        return strategy;
    }

    @Override
    public int select(URL url, Invocation invocation) {
        return ThreadLocalRandom.current().nextInt(3);
    }
}
