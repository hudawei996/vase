package com.wanjian.proxy.hostmapper;

import com.wanjian.proxy.http.Headers;

import java.net.InetSocketAddress;

/**
 * Created by wanjian on 2017/7/3.
 */

public interface IHostMapper {

    InetSocketAddress map(String stateLine, Headers headers);

}
