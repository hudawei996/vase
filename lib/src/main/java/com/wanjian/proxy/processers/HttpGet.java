package com.wanjian.proxy.processers;

import com.wanjian.proxy.event.Message;
import com.wanjian.proxy.http.Headers;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wanjian on 2017/7/3.
 */

public class HttpGet extends HttpPost {


    @Override
    public boolean accept(String stateLine) {
        if (stateLine == null) {
            return false;
        }
        String line = stateLine.trim().toUpperCase();
        return line.startsWith("GET") && line.contains("HTTP");
    }

    @Override
    protected void copyBody(Headers headers, InputStream inputStream, OutputStream outputStream, Message request) {
        // get 请求不需要发送body
    }

}
