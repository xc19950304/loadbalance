package com.aliware.tianchi;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.logger.Logger;
import org.apache.dubbo.common.logger.LoggerFactory;
import org.apache.dubbo.rpc.*;

import java.util.Date;

import static com.aliware.tianchi.Constants.*;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端过滤器
 * 可选接口
 * 用户可以在客户端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.CONSUMER)
public class TestClientFilter implements Filter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestClientFilter.class);

    //long startTime = 0;
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
//        return new RpcResult();
        if (!threadCountInit) {
            Result result = invoker.invoke(invocation);
            return result;
        }
        try {
            //startTime = System.currentTimeMillis();
  /*          System.out.println("invoke large: " + longAdderLarge.longValue());
            System.out.println("invoke medium: " + longAdderMedium.longValue());
            System.out.println("invoke small: " + longAdderSmall.longValue());*/
            URL url = invoker.getUrl();
            int port = url.getPort();
            if (port == 20880) {
                if (longAdderSmall.longValue() <= 0)
                    return new RpcResult();
                longAdderSmall.decrement();
//                LOGGER.info(new Date().getTime() + ":small:" + (com.aliware.tianchi.Constants.activeThreadCount.get("small") + ":" + com.aliware.tianchi.Constants.longAdderSmall.longValue()));
            } else if (port == 20870) {
                if (longAdderMedium.longValue() <= 0)
                    return new RpcResult();
                longAdderMedium.decrement();
//                LOGGER.info(new Date().getTime() + ":medium:" + com.aliware.tianchi.Constants.activeThreadCount.get("medium") + ":" + com.aliware.tianchi.Constants.longAdderMedium.longValue());
            } else {
                if (longAdderLarge.longValue() <= 0)
                    return new RpcResult();
                longAdderLarge.decrement();
//                LOGGER.info(new Date().getTime() + ":large:" + (com.aliware.tianchi.Constants.activeThreadCount.get("large") + ":" + com.aliware.tianchi.Constants.longAdderLarge.longValue()));
            }
            Result result = invoker.invoke(invocation);
            return result;
        } catch (Exception e) {
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
        // long endTime = System.currentTimeMillis();
        //System.out.println( "request time : " +(endTime - startTime));
        if (!threadCountInit) {
            return result;
        }
        if (result.getResult() == null) {
            return result;
        }
        URL url = invoker.getUrl();
        int port = url.getPort();
        if (port == 20880) {
            longAdderSmall.increment();
        } else if (port == 20870) {
            longAdderMedium.increment();
        } else {
            longAdderLarge.increment();
        }
        return result;
    }
}
