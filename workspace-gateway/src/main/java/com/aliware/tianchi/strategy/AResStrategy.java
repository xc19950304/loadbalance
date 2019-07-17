package com.aliware.tianchi.strategy;

import com.aliware.tianchi.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;

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


    @Override
    public int select(URL url, Invocation invocation) {

        int smallActiveCount = (int) Constants.longAdderSmall.longValue();
        int mediumActiveCount = (int) Constants.longAdderMedium.longValue();
        int largeActiveCount = (int) Constants.longAdderLarge.longValue();


        smallActiveCount =  smallActiveCount == 0 ? 1 : smallActiveCount;
        mediumActiveCount = mediumActiveCount == 0 ? 1 : mediumActiveCount;
        largeActiveCount = largeActiveCount == 0 ? 1 : largeActiveCount;

        double k1 = Math.log(rand.nextDouble()) / (smallActiveCount * 1);
        double k2 = Math.log(rand.nextDouble()) / (mediumActiveCount * 2);
        double k3 = Math.log(rand.nextDouble()) / (largeActiveCount * 2.3);

        double result = Math.max(Math.max(k1, k2), k3);

        if (result == k1) {
            return 0;
        }
        if (result == k2) {
            return 1;
        }
        return 2;
    }
}
