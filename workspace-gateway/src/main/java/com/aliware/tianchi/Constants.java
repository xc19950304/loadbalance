package com.aliware.tianchi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

public class Constants {

    //服务端-获取剩余线程数
    public static Map<String, Integer> activeThreadCount = new HashMap<>();

    //客户端-获取剩余线程数
    public static LongAdder longAdderLarge = new LongAdder();
    public static LongAdder longAdderMedium = new LongAdder();
    public static LongAdder longAdderSmall = new LongAdder();

    //初始总线程数是否初始化
    public static Boolean threadCountInit = false;

    //初始总线程数
    public static int smallProducerThreadSum = 0;
    public static int mediumProducerThreadSum = 0;
    public static int largeProducerThreadSum = 0;

    static {
        longAdderLarge.add(0);
        longAdderMedium.add(0);
        longAdderSmall.add(0);
        activeThreadCount.put("small", 0);
        activeThreadCount.put("medium", 0);
        activeThreadCount.put("large", 0);
    }

}
