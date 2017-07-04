package com.wanjian.proxy.utils;

import com.wanjian.proxy.log.Logger;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by wanjian on 2017/7/3.
 */

public class IOUtils {
    /**
     * \r\n   or   \n  or  EOF
     *
     * @param inputStream
     * @return next line
     * @throws IOException
     */
    public static String nextLine(InputStream inputStream) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int ch;
        while ((ch = inputStream.read()) != -1) {
            if (ch == '\r') {
                int n = inputStream.read();
                if (n == -1) {
                    buffer.write('\r');
                    break;
                } else if (n == '\n') {
                    break;
                } else {
                    buffer.write(ch);
                    buffer.write(n);
                    continue;
                }
            } else if (ch == '\n') {
                break;
            }

            buffer.write(ch);
        }

        return buffer.toString();
    }

    public static void closeStream(Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception e) {
        }

    }

    public static ByteArrayOutputStream copyStream(InputStream inputStream, OutputStream outputStream) throws IOException {
        Logger.log("copyStream started....");

        BufferedInputStream bufferedIn = new BufferedInputStream(inputStream);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int len;
        byte[] buff = new byte[1024];
        while ((len = bufferedIn.read(buff)) != -1) {
            outputStream.write(buff, 0, len);
            // Logger.log(new String(buff, 0, len));
            appendBody(buff, len, byteArrayOutputStream);
            outputStream.flush();
        }
        Logger.log("copyStream finished....");

        return byteArrayOutputStream;
    }

    private static void appendBody(byte[] bytes, int len, ByteArrayOutputStream byteArrayOutputStream) {
        if (byteArrayOutputStream.size() > 2 * 1024 * 1024) {
            return;
        }
        byteArrayOutputStream.write(bytes, 0, len);
    }

}
