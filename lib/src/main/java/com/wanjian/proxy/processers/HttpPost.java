package com.wanjian.proxy.processers;

import com.wanjian.proxy.Config;
import com.wanjian.proxy.ThreadPool;
import com.wanjian.proxy.event.Message;
import com.wanjian.proxy.http.Headers;
import com.wanjian.proxy.log.Logger;
import com.wanjian.proxy.utils.HttpUtils;
import com.wanjian.proxy.utils.IOUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by wanjian on 2017/7/3.
 */

public class HttpPost implements IProtocol {
    private Config mConfig;

    @Override
    public boolean accept(String stateLine) {
        if (stateLine == null) {
            return false;
        }
        String line = stateLine.trim().toUpperCase();
        return line.startsWith("POST") && line.contains("HTTP");
    }

    @Override
    public void process(Config config, String stateLine, Socket socket) {
        this.mConfig = config;
        Message request = new Message();

        InputStream inputStream = null;
        OutputStream outputStream = null;
        Headers headers;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            headers = HttpUtils.readHeaders(inputStream);
            request.reqHeaders = (Headers) headers.clone();
            headers = HttpUtils.filterHeaders(headers);
        } catch (Exception e) {
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
            return;
        }
        request.reqStateLine = stateLine;

        proxyHttpPost(stateLine, headers, inputStream, outputStream, request);
    }


    private void proxyHttpPost(String stateLine, Headers headers, InputStream inputStream, OutputStream outputStream, Message request) {

        InputStream proxyIn = null;
        OutputStream proxyOut = null;
        try {
            Logger.log(">>>>>>>>>>>");
            Logger.log(stateLine);
            Logger.log(headers);

            Socket server = HttpUtils.connect2server(mConfig, stateLine, headers);

            stateLine = HttpUtils.relativePath(stateLine);

            proxyIn = server.getInputStream();
            proxyOut = server.getOutputStream();

            HttpUtils.copyStateLine(stateLine, proxyOut);

            HttpUtils.copyHeaders(headers, proxyOut);

            copyBody(headers, inputStream, proxyOut, request);

            stateLine = IOUtils.nextLine(proxyIn);//阻塞，直到body发送完成
            headers = HttpUtils.readHeaders(proxyIn);
            request.respStateLine = stateLine;
            request.respHeaders = (Headers) headers.clone();
            Logger.log("<<<<<<<<<<<");
            Logger.log(stateLine);
            Logger.log(headers);

            headers = HttpUtils.filterHeaders(headers);

            HttpUtils.copyStateLine(stateLine, outputStream);

            HttpUtils.copyHeaders(headers, outputStream);

            ByteArrayOutputStream byteArrayOutputStream = IOUtils.copyStream(proxyIn, outputStream);
            request.respBody = byteArrayOutputStream.toByteArray();
            EventBus.getDefault().post(request);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(proxyOut);
            IOUtils.closeStream(proxyIn);
            IOUtils.closeStream(outputStream);
        }


    }

    protected void copyBody(final Headers headers, final InputStream inputStream, final OutputStream outputStream, final Message request) {
        //独立线程中发送数据，避免实现了 http1.0+ 规范的客户端发送完成后不关闭连接导致的阻塞
        ThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                copy(headers, inputStream, outputStream, request);
            }
        });

    }

    private void copy(Headers headers, InputStream inputStream, OutputStream outputStream, Message request) {
        try {
            long length = headers.contentLength(-1);
            if (length == -1) {
                length = Long.MAX_VALUE;
            }
            long count = 0;
            int len;
            BufferedInputStream buff = new BufferedInputStream(inputStream);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] bytes = new byte[1024];
            while ((len = buff.read(bytes)) != -1) {
                count += len;
                outputStream.write(bytes, 0, len);
                // Logger.log(new String(bytes, 0, len));
                appendBody(bytes, len, byteArrayOutputStream);
                outputStream.flush();
                if (count >= length) {
                    return;
                }
            }
            request.reqBody = byteArrayOutputStream.toByteArray();
        } catch (Exception e) {
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
        }
    }

    private void appendBody(byte[] bytes, int len, ByteArrayOutputStream byteArrayOutputStream) {
        if (byteArrayOutputStream.size() > 1024 * 1024) {
            return;
        }
        byteArrayOutputStream.write(bytes, 0, len);
    }


}
