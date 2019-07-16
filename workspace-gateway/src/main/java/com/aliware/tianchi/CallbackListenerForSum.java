package com.aliware.tianchi;

import org.apache.dubbo.rpc.listener.CallbackListener;

import static com.aliware.tianchi.Constants.*;

public class CallbackListenerForSum implements CallbackListener {
    @Override
    public void receiveServerMsg(String msg) {
        //System.out.println("CallbackListenerForSum接受到数据："+ msg);
        if(threadCountInit)
            return ;

        String[] threadSum = msg.split(":");
        switch(threadSum[0])
        {
            case "small":
                smallProducerThreadSum = Integer.parseInt(threadSum[1]);
            case "medium":
                mediumProducerThreadSum = Integer.parseInt(threadSum[1]);
            case "large":
                largeProducerThreadSum = Integer.parseInt(threadSum[1]);
        }
        if(smallProducerThreadSum  != 0 && mediumProducerThreadSum != 0 && largeProducerThreadSum != 0)
        {
            //线程参数初始化(采用服务端传值)
            activeThreadCount.put("small",smallProducerThreadSum);
            activeThreadCount.put("medium",mediumProducerThreadSum);
            activeThreadCount.put("large",largeProducerThreadSum);

            longAdderLarge.add(smallProducerThreadSum);
            longAdderMedium.add(mediumProducerThreadSum);
            longAdderSmall.add(largeProducerThreadSum);

            //线程参数初始化(直接在gateway统计)
            threadCountInit = true;

            //System.out.println("threadCountInit = true");
        }
    }
}
