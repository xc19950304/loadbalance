package com.aliware.tianchi;

import org.apache.dubbo.rpc.listener.CallbackListener;

import java.util.*;

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

    @Override
    public void receiveServerMsg(String msg) {
        String[] data = msg.split(":");
        int data_old = Constants.activeThreadCount.get(data[0]);
        if (IS_DEBUG) {
            String message = "receive msg from server :" + msg + " delta: " + (data_old - Integer.parseInt(data[1]));
            receiveItemMap.put(data[0], new ReceiveItem(message, new Date()));
        }
        Constants.activeThreadCount.put(data[0] + "_old", data_old);
        Constants.activeThreadCount.put(data[0], Integer.parseInt(data[1]));
    }

}
