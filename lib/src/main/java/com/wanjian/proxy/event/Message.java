package com.wanjian.proxy.event;

import com.wanjian.proxy.http.Headers;

/**
 * Created by wanjian on 2017/7/4.
 */

public class Message {
    public String reqStateLine;
    public Headers reqHeaders;
    public  byte[] reqBody;

    public String respStateLine;
    public Headers respHeaders;
    public byte[] respBody;

    public Message() {

    }

}
