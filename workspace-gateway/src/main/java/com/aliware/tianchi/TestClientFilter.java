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

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        if (!threadCountInit) {
            Result result = invoker.invoke(invocation);
            return result;
        }
        try {
            URL url = invoker.getUrl();
            int port = url.getPort();
            if (port == 20880) {
                if (longAdderSmall.longValue() <= 0)
                    return new RpcResult();
                longAdderSmall.decrement();
            } else if (port == 20870) {
                if (longAdderMedium.longValue() <= 0)
                    return new RpcResult();
                longAdderMedium.decrement();
            } else {
                if (longAdderLarge.longValue() <= 0)
                    return new RpcResult();
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
