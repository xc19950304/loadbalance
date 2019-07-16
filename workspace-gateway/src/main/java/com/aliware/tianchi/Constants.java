package com.aliware.tianchi;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.LongAdder;

public class Constants {
//    public static final ThreadLocal<Integer>  threadLocal = new ThreadLocal();
/*    public static  Integer threadSmall = 0;
    public static  Integer threadMedium = 0;
    public static  Integer threadLarge = 0;*/

    public static Map<String, Integer> activeThreadCount = new HashMap<>();
    public static LongAdder longAdderLarge = new LongAdder();
    public static LongAdder longAdderMedium = new LongAdder();
    public static LongAdder longAdderSmall = new LongAdder();

    static {
        longAdderLarge.add(650);
        longAdderMedium.add(450);
        longAdderSmall.add(200);
        activeThreadCount.put("small", 200);
        activeThreadCount.put("medium", 450);
        activeThreadCount.put("large", 650);
    }

}
