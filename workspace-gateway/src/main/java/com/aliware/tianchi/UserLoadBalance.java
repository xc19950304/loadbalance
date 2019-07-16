package com.aliware.tianchi;

import com.aliware.tianchi.strategy.*;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.LoadBalance;

import java.util.List;

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

        // TODO: 测试其他算法时只需要切换Strategy即可
        UserLoadBalanceStrategy strategy = AResStrategy.getInstance("client");
//       UserLoadBalanceStrategy strategy = DynamicWeightStrategy.getInstance();
        return invokers.get(strategy.select(url, invocation));
    }

}
