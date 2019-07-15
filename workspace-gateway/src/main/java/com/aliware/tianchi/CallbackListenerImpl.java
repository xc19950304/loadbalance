package com.aliware.tianchi;

import org.apache.dubbo.rpc.listener.CallbackListener;

/**
 * @author daofeng.xjf
 *
 * 客户端监听器
 * 可选接口
 * 用户可以基于获取获取服务端的推送信息，与 CallbackService 搭配使用
 *
 */
public class CallbackListenerImpl implements CallbackListener {

    @Override
    public void receiveServerMsg(String msg) {
        String[] data = msg.split(":");
        //Integer number = Constants.activeThreadCount.get(data[0]) + 1;
        //Integer number = Integer.parseInt(data[1]);
        Constants.activeThreadCount.put(data[0],Integer.parseInt(data[1]));
/*        switch (data[0])
        {
            case "small":
                number++;
                Constants.activeThreadCount.put(data[0],data[1]);
                break;
            case "medium":
                Constants.threadMedium++;
                break;
            case "large":
                Constants.threadLarge++;
                break;
        }*/
       System.out.println("receive msg from server 111111:" + msg);
    }



}
