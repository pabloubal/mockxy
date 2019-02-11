package io.github.pabloubal.mockxy.core.utils;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

public class MockxyProxySelector extends ProxySelector {

    ArrayList<Proxy> noProxy = new ArrayList<Proxy>();

    public MockxyProxySelector(){
        noProxy.add(Proxy.NO_PROXY);
    }

    @Override
    public List<Proxy> select(URI uri) {
        return noProxy;
    }

    @Override
    public void connectFailed(URI uri, SocketAddress socketAddress, IOException e) {
    }
}
