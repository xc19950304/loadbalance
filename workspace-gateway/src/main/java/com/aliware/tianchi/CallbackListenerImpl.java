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

    private Map<String, ReceiveItem> receiveItemMap = new HashMap<>();

    private static final boolean IS_DEBUG = Boolean.parseBoolean(System.getProperty("debug"));

    public CallbackListenerImpl() {
//        if (IS_DEBUG) {
//            receiveItemMap.put("small", new ReceiveItem("", new Date()));
//            receiveItemMap.put("medium", new ReceiveItem("", new Date()));
//            receiveItemMap.put("large", new ReceiveItem("", new Date()));
//            Timer timer = new Timer();
//            timer.scheduleAtFixedRate(new TimerTask() {
//                public void run() {
//                    System.err.println("small: " + receiveItemMap.get("small").toString());
//                    System.err.println("medium: " + receiveItemMap.get("medium").toString());
//                    System.err.println("large: " + receiveItemMap.get("large").toString());
//                }
//            }, 500, 500);
//        }
    }

/*    @Override
    public void receiveServerMsg(String msg) {
        String[] data = msg.split(":");
        int data_old = Constants.activeThreadCount.get(data[0]);
        if (IS_DEBUG) {
            String message = "receive msg from server :" + msg + " delta: " + (data_old - Integer.parseInt(data[1]));
            receiveItemMap.put(data[0], new ReceiveItem(message, new Date()));
        }
        Constants.activeThreadCount.put(data[0] + "_old", data_old);
        Constants.activeThreadCount.put(data[0], Integer.parseInt(data[1]));
    }*/


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

                //System.out.println("threadCountInit = true");
            }
        }

    }
}