package com.wanjian.proxy.hostmapper;

import com.wanjian.proxy.http.Headers;

import java.net.InetSocketAddress;

/**
 * Created by wanjian on 2017/7/4.
 */

public class DefaultHostMapper implements IHostMapper {

    @Override
    public InetSocketAddress map(String stateLine, Headers headers) {
        return null;
    }
}
