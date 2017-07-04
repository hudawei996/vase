package com.wanjian.proxy.log;

import java.io.OutputStream;
import java.util.Date;
import java.util.Map;

/**
 * Created by wanjian on 2017/6/28.
 */

public class Logger {
    static ThreadLocal<OutputStream> sThreadLocal = new ThreadLocal();

    public static void log(String... args) {


        try {
//            File file = new File(Thread.currentThread().toString() + ".log");
//
//            if (!file.exists()) {
//                file.createNewFile();
//            }

            OutputStream outputStream = sThreadLocal.get();
            if (outputStream == null) {
                outputStream = System.out;
//                outputStream = new BufferedOutputStream(new FileOutputStream(file));
                sThreadLocal.set(outputStream);
            }
            String s = new Date() + " : " + Thread.currentThread() + " >>";

            for (String arg : args) {
                outputStream.write((s + arg + "\r\n").getBytes());
            }
            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void log(Map<String, String> map) {
        if (map == null) {
            return;
        }
        try {
//            File file = new File(Thread.currentThread().toString() + ".log");
//            if (!file.exists()) {
//                file.createNewFile();
//            }
            OutputStream outputStream = sThreadLocal.get();
            if (outputStream == null) {
                outputStream = System.out;
//                outputStream = new BufferedOutputStream(new FileOutputStream(file));
                sThreadLocal.set(outputStream);
            }

            String s = new Date() + " : " + Thread.currentThread() + " >>";

            for (Map.Entry<String, String> entry : map.entrySet()) {
                outputStream.write((s + entry.getKey() + " : " + entry.getValue() + "\r\n").getBytes());
            }

            outputStream.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
