package com.wanjian.proxy.utils;


/**
 * Created by wanjian on 2017/7/4.
 */

public class Proxy {

    public static String host() {
        return android.net.Proxy.getDefaultHost();
    }

    public static int port() {
        return android.net.Proxy.getDefaultPort();

    }


}
