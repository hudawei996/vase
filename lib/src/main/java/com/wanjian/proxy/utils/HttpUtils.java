package com.wanjian.proxy.utils;

import com.wanjian.proxy.Config;
import com.wanjian.proxy.exception.HostNotExistException;
import com.wanjian.proxy.http.Headers;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URI;
import java.util.Map;

/**
 * Created by wanjian on 2017/7/3.
 */

public class HttpUtils {
    public static Headers readHeaders(InputStream inputStream) throws IOException {
        Headers headers = new Headers();
        String line;
        while (!(line = IOUtils.nextLine(inputStream)).equals("")) {
            int position = line.indexOf(":");
            if (position != -1) {
                headers.put(line.substring(0, position), line.substring(position + 1));
            }
        }
        return headers;
    }

    /**
     * 过滤掉请求头中 Connection 对应的值中的请求头
     * <p>
     * 参考 &lt;&lt;HTTP权威指南&gt;&gt;
     *
     * @param headers
     * @return
     */
    public static Headers filterHeaders(Headers headers) {
        String connection = null;
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (entry.getKey().trim().equalsIgnoreCase("Connection")) {
                connection = headers.remove(entry.getKey());
                break;
            }
        }
        if (connection == null) {
            return headers;
        }

        String[] cons = connection.split(" *, *");

        for (String con : cons) {
            headers.remove(con.trim());
        }
        headers.put("Connection", "close");
        return headers;
    }

    public static Socket connect2server(Config config, String line, Headers headers) throws IOException, RuntimeException, HostNotExistException {
        InetSocketAddress socketAddress = config.mHostMapper.map(line, headers);
        if (socketAddress != null) {
            return initSocket(config, socketAddress);
        }
        String url = line.split(" +")[1];
        if (url.toLowerCase().startsWith("http://")) {
            URI uri = Uri.create(url);
            return initSocket(config, new InetSocketAddress(uri.getHost(), uri.getPort() == -1 ? 80 : uri.getPort()));
        } else {
            String host = headers.host();
            int port = headers.port();
            if (host == null || port == -1) {
                throw new HostNotExistException("state line: " + line + " headers: " + headers);
            }
            return initSocket(config, new InetSocketAddress(host, port));
        }

    }

    private static Socket initSocket(Config config, InetSocketAddress address) throws IOException {
        Socket socket = new Socket();
        socket.setSoTimeout(config.mTransportTimeOut);
        socket.connect(address, config.mConnectTimeOut);
        socket.setKeepAlive(true);
        return socket;
    }

    public static String relativePath(String line) throws RuntimeException {
        String connects[] = line.split(" +");
        if (connects[1].toLowerCase().startsWith("http://")) {
            URI uri = Uri.create(connects[1]);
            String path = uri.getRawPath();
            String query = uri.getRawQuery();
            String frag = uri.getRawFragment();

            if (path == null) {
                path = "/";
            }

            if (query == null) {
                query = "";
            } else {
                query = "?" + query;
            }

            if (frag == null) {
                frag = "";
            } else {
                frag = "#" + frag;
            }
            return connects[0] + " " + path + query + frag + " " + connects[2];
        } else {
            return line;
        }
    }

    public static void copyHeaders(Headers headers, OutputStream outputStream) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(outputStream);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            out.write(entry.getKey().trim().getBytes());
            out.write(": ".getBytes());
            out.write(entry.getValue().trim().getBytes());
            out.write("\r\n".getBytes());
        }
        out.write("\r\n".getBytes());
        out.flush();
    }


    public static void copyStateLine(String line, OutputStream proxyOut) throws IOException {
        proxyOut.write(line.getBytes());
        proxyOut.write("\r\n".getBytes());
    }


}
