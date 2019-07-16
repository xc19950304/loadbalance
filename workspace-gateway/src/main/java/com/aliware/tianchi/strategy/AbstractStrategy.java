package com.aliware.tianchi.strategy;

import com.aliware.tianchi.strategy.UserLoadBalanceStrategy;

import java.util.Random;

public abstract class AbstractStrategy implements UserLoadBalanceStrategy {
    protected static final Random rand = new Random();
    protected static AbstractStrategy strategy ;
    protected static String dataFrom = "client";



}
