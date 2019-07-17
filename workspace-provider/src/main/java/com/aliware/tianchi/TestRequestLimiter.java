package com.aliware.tianchi;

import org.apache.dubbo.remoting.exchange.Request;
import org.apache.dubbo.remoting.transport.RequestLimiter;

/**
 * @author daofeng.xjf
 * <p>
 * 服务端限流
 * 可选接口
 * 在提交给后端线程池之前的扩展，可以用于服务端控制拒绝请求
 */
public class TestRequestLimiter implements RequestLimiter {


    /**
     * @param request         服务请求
     * @param activeTaskCount 服务端对应线程池的活跃线程数
     * @return false 不提交给服务端业务线程池直接返回，客户端可以在 Filter 中捕获 RpcException
     * true 不限流
     */
    @Override
    public boolean tryAcquire(Request request, int activeTaskCount) {
       /* String env = System.getProperty("quota");
        if (env.equals("small")) {
            if (Constants.threadSmallTotal == 0) return true;
            Constants.threadSmall = (Constants.threadSmallTotal - activeTaskCount);
            if(Constants.threadSmall <= 0)
                return false;
        } else if (env.equals("medium")) {
            if (Constants.threadMediumTotal == 0) return true;
            Constants.threadMedium = (Constants.threadMediumTotal - activeTaskCount);
            if(Constants.threadMedium <= 0)
                return false;
        } else {
            if (Constants.threadLargeTotal == 0) return true;
            Constants.threadLarge = (Constants.threadLargeTotal - activeTaskCount);
            if(Constants.threadLarge <= 0)
                return false;
        }*/
        return true;
    }

}