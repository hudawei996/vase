package com.wanjian.proxy.utils;

import java.net.URI;
import java.net.URLEncoder;

/**
 * Created by wanjian on 2017/6/30.
 */

public class Uri {
    public static URI create(String s) {
        //避免url中含有 | 时导致的错误
        return URI.create(URLEncoder.encode(s).replace("%3A", ":").replace("%2F", "/").replace("%3F", "?").replace("%26", "&").replace("%3D", "="));
    }
}
