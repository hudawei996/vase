package com.wanjian.proxy;


import com.wanjian.proxy.processers.IProtocol;
import com.wanjian.proxy.utils.IOUtils;
import com.wanjian.proxy.utils.Proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by wanjian on 2017/7/3.
 */

class Manager {
    private final Config mConfig;

    Manager(Config config) {
        mConfig = config;
        final String host = Proxy.host();
        final int port = Proxy.port();
        if ("localhost".equals(host) || "127.0.0.1".equals(host)) {
            ThreadPool.submit(new Runnable() {
                @Override
                public void run() {
                    ServerSocket serverSocket;
                    try {
                        serverSocket = new ServerSocket(port);
                    } catch (IOException e) {
                        e.printStackTrace();
                        System.err.println("代理已停止运行，端口可能被占用或者没有加入网络访问权限");
                        return;
                    }
                    listen(serverSocket);
                }
            });
        } else {
            System.err.println("代理无效");
        }


    }


    private void listen(ServerSocket serverSocket) {
        while (true) {
            Socket socket;
            try {
                socket = serverSocket.accept();
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("代理失败，请尝试重启Vase");
                return;
            }
            try {
//                socket.setSoTimeout(mConfig.mTransportTimeOut*3);
                InputStream inputStream = socket.getInputStream();
                OutputStream outputStream = socket.getOutputStream();
                final String stateLine = IOUtils.nextLine(inputStream);
                for (final IProtocol protocol : mConfig.mProtocols) {
                    if (protocol.accept(stateLine)) {
                        proxy(protocol, stateLine, socket, inputStream, outputStream);
                        break;
                    }
                }


            } catch (IOException e) {
                e.printStackTrace();
                try {
                    socket.close();
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }

        }
    }

    private void proxy(final IProtocol protocol, final String stateLine, final Socket socket, InputStream inputStream, OutputStream outputStream) {
        ThreadPool.submit(new Runnable() {
            @Override
            public void run() {
                protocol.process(mConfig, stateLine, socket);
            }
        });
    }
}
