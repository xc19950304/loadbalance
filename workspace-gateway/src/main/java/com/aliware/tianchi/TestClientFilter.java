package com.aliware.tianchi;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

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
    //long startTime = 0;
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            //startTime = System.currentTimeMillis();
            URL url = invoker.getUrl();
            int port = url.getPort();
            if (port == 20880) {
                longAdderSmall.decrement();
            } else if (port == 20870) {
                longAdderMedium.decrement();
            } else {
                longAdderLarge.decrement();
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
