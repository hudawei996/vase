package com.wanjian.proxy;


/**
 * Created by wanjian on 2017/7/3.
 */

public class Vase {
    private Vase() {
    }

    public static void init(Config config) {
        if (config == null) {
            config = new Config.Build().build();
        }
        new Manager(config);
    }
}
