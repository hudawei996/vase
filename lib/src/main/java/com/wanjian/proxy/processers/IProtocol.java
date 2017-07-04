package com.wanjian.proxy.processers;

import com.wanjian.proxy.Config;

import java.net.Socket;

/**
 * Created by wanjian on 2017/7/3.
 */

public interface IProtocol {
    boolean accept(String stateLine);

    void process(Config config, String stateLine, Socket socket);
}
