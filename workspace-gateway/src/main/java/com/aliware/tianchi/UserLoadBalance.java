package com.aliware.tianchi;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author daofeng.xjf
 *
 * 负载均衡扩展接口
 * 必选接口，核心接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance  {

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {
        int smallActiveCount = Constants.activeThreadCount.get("small");
        int mediumActiveCount = Constants.activeThreadCount.get("medium");
        int largeActiveCount = Constants.activeThreadCount.get("large");

/*        Random rand =new Random( );

        int randNumber = rand.nextInt(smallActiveCount + mediumActiveCount + largeActiveCount);
        if(randNumber < smallActiveCount)
        {
            return invokers.get(0);
        }
        else if(randNumber >= smallActiveCount && randNumber < smallActiveCount + mediumActiveCount)
        {
            return invokers.get(1);
        }
        else
            return invokers.get(2);*/

        PriorityQueue<Double> queue = new PriorityQueue<>((o1, o2) -> o2.compareTo(o1));
        double k1 = Math.log(Math.random()) / smallActiveCount;
        queue.offer(k1);
        double k2 = Math.log(Math.random()) / mediumActiveCount;
        queue.offer(k2);
        double k3 = Math.log(Math.random()) / largeActiveCount;
        queue.offer(k3);

        double result = queue.poll();

        if (result == k1) {
            return invokers.get(0);
        }
        if (result == k2) {
            return invokers.get(1);
        }

        return invokers.get(2);

/*        int invokerNumber = ThreadLocalRandom.current().nextInt(invokers.size());
        return invokers.get(invokerNumber);*/
    }
}
