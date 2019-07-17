package com.aliware.tianchi;

import org.apache.dubbo.common.Constants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import java.util.Map;
import java.util.Properties;

/**
 * @author daofeng.xjf
 *
 * 服务端过滤器
 * 可选接口
 * 用户可以在服务端拦截请求和响应,捕获 rpc 调用时产生、服务端返回的已知异常。
 */
@Activate(group = Constants.PROVIDER)
public class TestServerFilter implements Filter {
    private static  boolean init = false;
    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try{
            Result result = invoker.invoke(invocation);
            return result;
        }catch (Exception e){
            throw e;
        }

    }

    @Override
    public Result onResponse(Result result, Invoker<?> invoker, Invocation invocation) {
//        if(!init){
//            URL url=invoker.getUrl();
//            int port = url.getPort();
//            int total=url.getParameter("threads",0);
//            if (port==20880){
//                com.aliware.tianchi.Constants.threadSmallTotal=total;
//            }else if(port==20870){
//                com.aliware.tianchi.Constants.threadMediumTotal=total;
//            }else {
//                com.aliware.tianchi.Constants.threadLargeTotal=total;
//            }
//            init=true;
//        }
        return result;
    }

}
