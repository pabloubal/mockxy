package io.github.pabloubal.mockxy.core.requests;

import io.github.pabloubal.mockxy.core.ChainHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import javax.annotation.PostConstruct;

@ConfigurationProperties
public class Proxy{
    public static final int TYPE_HTTP=0;
    public static final int TYPE_SOCKS=1;

    @Autowired
    ChainHandler chainHandler;

    @Value("${proxy.httpPort}")
    private Integer httpPort;

    @Value("${proxy.socksPort}")
    private Integer socksPort;

    private Thread httpThread;
    private Thread socksThread;

    @PostConstruct
    public void init(){
        if(this.httpThread == null) {
            this.httpThread = new Thread(new RequestHandler(TYPE_HTTP, httpPort, chainHandler));
            this.socksThread = new Thread(new RequestHandler(TYPE_SOCKS, socksPort, chainHandler));

            httpThread.start();
            socksThread.start();
        }
    }


    public Integer getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(Integer httpPort) {
        this.httpPort = httpPort;
    }

    public Integer getSocksPort() {
        return socksPort;
    }

    public void setSocksPort(Integer socksPort) {
        this.socksPort = socksPort;
    }
}