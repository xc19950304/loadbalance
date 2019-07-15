package com.aliware.tianchi;

import java.util.HashMap;
import java.util.Map;

public class Constants {
//    public static final ThreadLocal<Integer>  threadLocal = new ThreadLocal();
/*    public static  Integer threadSmall = 0;
    public static  Integer threadMedium = 0;
    public static  Integer threadLarge = 0;*/

    public static Map<String,Integer> activeThreadCount = new HashMap<>();

    static
    {
        activeThreadCount.put("small",200);
        activeThreadCount.put("medium",450);
        activeThreadCount.put("large",650);
        activeThreadCount.put("small_old", 200);
        activeThreadCount.put("medium_old", 450);
        activeThreadCount.put("large_old", 650);
    }

}
