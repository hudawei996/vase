package com.wanjian.proxy.http;

import java.util.HashMap;

/**
 * Created by wanjian on 2017/7/3.
 */

public class Headers extends HashMap<String, String> {

    public String host() {
        String host = get("Host");
        if (host == null) {
            return null;
        }
        if (host.contains(":")) {
            String dp[] = host.trim().split(" *: *");
            if (dp.length > 0) {
                return dp[0].trim();
            } else {
                return null;
            }

        } else {
            return host;
        }
    }

    public int port() {

        String host = get("Host");
        if (host == null) {
            return -1;
        }
        if (host.contains(":")) {
            String dp[] = host.trim().split(" *: *");
            if (dp.length > 1) {
                try {
                    return Integer.parseInt(dp[1]);
                } catch (Exception e) {
                    return -1;
                }
            } else {
                return -1;
            }

        } else {
            return 80;
        }
    }

    public int contentLength(int defaultValue) {
        try {
            return Integer.parseInt(get("Content-Length").trim());
        } catch (Exception e) {
            return defaultValue;
        }
    }

}
