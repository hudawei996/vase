package com.wanjian.proxy.processers;

import com.wanjian.proxy.Config;

import java.net.Socket;

/**
 * Created by wanjian on 2017/7/3.
 */

public class NotSupport implements IProtocol {

    @Override
    public boolean accept(String stateLine) {
        System.err.println("不支持");
        return true;
    }

    @Override
    public void process(Config config, String stateLine, Socket socket) {
        try {
            socket.close();
        } catch (Exception e) {
        }
    }
}
