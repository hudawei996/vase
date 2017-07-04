package com.wanjian.proxy.utils;

/**
 * Created by wanjian on 2017/7/3.
 */

public class Check {

    public static void ifNull(Object o, String err) {
        if (o == null) {
            throw new RuntimeException(err);
        }
    }

    public static boolean ifEmpty(String s) {
        return s == null || s.length() == 0;
    }
}
