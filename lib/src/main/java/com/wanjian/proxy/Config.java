package com.wanjian.proxy;

import com.wanjian.proxy.hostmapper.DefaultHostMapper;
import com.wanjian.proxy.hostmapper.IHostMapper;
import com.wanjian.proxy.processers.HttpGet;
import com.wanjian.proxy.processers.HttpPost;
import com.wanjian.proxy.processers.IProtocol;
import com.wanjian.proxy.processers.NotSupport;
import com.wanjian.proxy.processers.Tunnel;
import com.wanjian.proxy.utils.Check;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wanjian on 2017/7/3.
 */

public final class Config {

    public final List<IProtocol> mProtocols;
    public final int mConnectTimeOut;
    public final int mTransportTimeOut;
    public final IHostMapper mHostMapper;

    private Config(Build build) {
        mProtocols = build.mProtocols;
        mConnectTimeOut = build.mConnectTimeOut;
        mTransportTimeOut = build.mTransportTimeOut;
        mHostMapper = build.mHostMapper;
    }


    public static final class Build {
        private List<IProtocol> mProtocols = new ArrayList<>();
        private int mConnectTimeOut;
        private int mTransportTimeOut;
        private IHostMapper mHostMapper;

        public Build() {
            mProtocols.add(new HttpGet());
            mProtocols.add(new HttpPost());
            mProtocols.add(new Tunnel());
            mProtocols.add(new NotSupport());

            mConnectTimeOut = 20 * 1000;
            mTransportTimeOut = 10 * 1000;

            mHostMapper = new DefaultHostMapper();
        }

        public Build addProtocolProcesser(IProtocol protocol) {
            Check.ifNull(protocol, "protocol processer can not be null !");
            mProtocols.add(0, protocol);
            return this;
        }

        public Build connectTimeOut(int second) {
            mConnectTimeOut = second;
            return this;
        }

        public Build transportTimeOut(int second) {
            mTransportTimeOut = second;
            return this;
        }


        public Build hostMapper(IHostMapper hostMapper) {
            this.mHostMapper = hostMapper;
            return this;
        }

        public Config build() {
            return new Config(this);
        }
    }
}
