package com.aliware.tianchi;

import com.aliware.tianchi.strategy.AResStrategy;
import com.aliware.tianchi.strategy.RandomStrategy;
import com.aliware.tianchi.strategy.UserLoadBalanceStrategy;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;

import static com.aliware.tianchi.Constants.threadCountInit;

/**
 * @author daofeng.xjf
 * <p>
 * 负载均衡扩展接口
 * 必选接口，核心接口
 * 此类可以修改实现，不可以移动类或者修改包名
 * 选手需要基于此类实现自己的负载均衡算法
 */
public class UserLoadBalance implements LoadBalance {
    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, URL url, Invocation invocation) throws RpcException {

        UserLoadBalanceStrategy strategy;

        if (!threadCountInit) {
            strategy = RandomStrategy.getInstance();
            //System.out.println("随机算法");
        } else {
            strategy = AResStrategy.getInstance("client");
            //System.out.println("AResStrategy权重算法");
        }

//        strategy = AResStrategy.getInstance("client");

        return invokers.get(strategy.select(url, invocation));
    }

}
