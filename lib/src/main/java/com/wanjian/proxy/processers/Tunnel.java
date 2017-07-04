package com.wanjian.proxy.processers;

import com.wanjian.proxy.Config;
import com.wanjian.proxy.ThreadPool;
import com.wanjian.proxy.http.Headers;
import com.wanjian.proxy.log.Logger;
import com.wanjian.proxy.utils.HttpUtils;
import com.wanjian.proxy.utils.IOUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Created by wanjian on 2017/7/3.
 */

public class Tunnel implements IProtocol {
    private Config mConfig;

    @Override
    public boolean accept(String stateLine) {
        if (stateLine == null) {
            return false;
        }
        return stateLine.trim().toUpperCase().startsWith("CONNECT");
    }

    @Override
    public void process(Config config, String stateLine, Socket socket) {
        this.mConfig = config;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        Headers headers;
        try {
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            headers = HttpUtils.readHeaders(inputStream);
        } catch (Exception e) {
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
            return;
        }
        proxyConnect(stateLine, headers, inputStream, outputStream);
    }

    private void proxyConnect(String line, Headers headers, final InputStream inputStream, final OutputStream outputStream) {

        try {
            ////Logger.log(">>>>>>>>>>>");
            //Logger.log(line);
            //Logger.log(headers);

            InetSocketAddress address = mConfig.mHostMapper.map(line, headers);
            if (address == null) {
                String[] connect = line.split(" +");
                String dp[] = connect[1].split(" *: *");
                String domain = dp[0].trim();
                int port = 443;
                try {
                    port = Integer.parseInt(dp[1].trim());
                } catch (Exception e) {
                }
                address = new InetSocketAddress(domain, port);
            }
            final Socket socket = new Socket();
            socket.connect(address, mConfig.mConnectTimeOut);
            socket.setSoTimeout(mConfig.mTransportTimeOut);
            socket.setKeepAlive(true);
            final OutputStream proxyOut = socket.getOutputStream();
            final InputStream proxyIn = socket.getInputStream();

            outputStream.write("HTTP/1.0 200 Connection Established\r\n".getBytes());
            outputStream.write("Connection: close\r\n".getBytes());
            outputStream.write("\r\n".getBytes());
            outputStream.flush();


            ThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    //Logger.log("connect client->proxy  copyStream....");

                    try {
                        IOUtils.copyStream(inputStream, proxyOut);
                    } catch (Exception e) {
                        IOUtils.closeStream(inputStream);
                        IOUtils.closeStream(proxyOut);

//                        IOUtils.closeStream(proxyIn);
//                        IOUtils.closeStream(outputStream);
                    }
                    //Logger.log("connect client->proxy finished....");

                }
            });


            ThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    //Logger.log("connect server->proxy  copyStream....");

                    try {
                        IOUtils.copyStream(proxyIn, outputStream);
                    } catch (Exception e) {

                    }
                    //Logger.log("connect server->proxy finished....");

                    IOUtils.closeStream(inputStream);
                    IOUtils.closeStream(proxyOut);
                    IOUtils.closeStream(proxyIn);
                    IOUtils.closeStream(outputStream);
                }
            });
        } catch (Exception e) {
            IOUtils.closeStream(inputStream);
            IOUtils.closeStream(outputStream);
        }
    }

}
