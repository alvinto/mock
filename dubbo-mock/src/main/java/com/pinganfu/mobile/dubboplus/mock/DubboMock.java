package com.pinganfu.mobile.dubboplus.mock;


import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.rpc.Exporter;
import com.alibaba.dubbo.rpc.Invoker;
import com.alibaba.dubbo.rpc.Protocol;
import com.alibaba.dubbo.rpc.RpcException;

/**
 * Created by wangqiaodong581 on 2016-07-12.
 */
public class DubboMock implements Protocol {

    private Protocol protocol;

    public DubboMock(Protocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public int getDefaultPort() {
        return protocol.getDefaultPort();
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        return protocol.export(invoker);
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, com.alibaba.dubbo.common.URL url) throws RpcException {
        if (Constants.REGISTRY_PROTOCOL.equals(url.getProtocol())) {
            Invoker invoker = protocol.refer(type, url);
            return new RemoteMockInvoker(invoker);
        }
        return protocol.refer(type,url);
    }

    @Override
    public void destroy() {
        protocol.destroy();
    }
}
