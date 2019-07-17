package com.aliware.tianchi;

import org.apache.dubbo.rpc.listener.CallbackListener;

import java.util.HashMap;
import java.util.Map;

import static com.aliware.tianchi.Constants.*;

/**
 * @author daofeng.xjf
 * <p>
 * 客户端监听器
 * 可选接口
 * 用户可以基于获取获取服务端的推送信息，与 CallbackService 搭配使用
 */
public class CallbackListenerImpl implements CallbackListener {

    public CallbackListenerImpl() { }


    @Override
    public void receiveServerMsg(String msg) {
        if (threadCountInit)
            return;
        synchronized (this) {
            if (threadCountInit) return;
            String[] threadSum = msg.split(":");
            switch (threadSum[0]) {
                case "small":
                    smallProducerThreadSum = Integer.parseInt(threadSum[1]);
                    break;
                case "medium":
                    mediumProducerThreadSum = Integer.parseInt(threadSum[1]);
                    break;
                case "large":
                    largeProducerThreadSum = Integer.parseInt(threadSum[1]);
                    break;
            }
            if (smallProducerThreadSum != 0 && mediumProducerThreadSum != 0 && largeProducerThreadSum != 0) {
                //线程参数初始化(采用服务端传值)
                activeThreadCount.put("small", smallProducerThreadSum);
                activeThreadCount.put("medium", mediumProducerThreadSum);
                activeThreadCount.put("large", largeProducerThreadSum);

                longAdderLarge.add(largeProducerThreadSum);
                longAdderMedium.add(mediumProducerThreadSum);
                longAdderSmall.add(smallProducerThreadSum);

                //线程参数初始化(直接在gateway统计)
                threadCountInit = true;
            }
        }

    }
}