package io.github.pabloubal.mockxy.core;

import io.github.pabloubal.mockxy.core.cache.CacheManager;
import io.github.pabloubal.mockxy.core.cache.strategies.impl.FileCache;
import io.github.pabloubal.mockxy.core.handlers.HandlerType;
import io.github.pabloubal.mockxy.core.handlers.generic.GetCache;
import io.github.pabloubal.mockxy.core.handlers.generic.HandlerEnd;
import io.github.pabloubal.mockxy.core.handlers.generic.Mappings;
import io.github.pabloubal.mockxy.core.handlers.generic.RemoveCache;
import io.github.pabloubal.mockxy.core.handlers.generic.SetCache;
import io.github.pabloubal.mockxy.core.handlers.http.RemoteHTTPCall;
import io.github.pabloubal.mockxy.core.handlers.socket.RemoteTCPCall;
import io.github.pabloubal.mockxy.core.requests.Proxy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MockxyConfiguration {
    @Bean
    public HandlerEnd handlerEnd(){
        return new HandlerEnd();
    }

    @Bean
    public Mappings mappings(){
        return new Mappings();
    }

    @Bean
    public RemoveCache removeCache(){
        return new RemoveCache();
    }
    @Bean
    public GetCache getCache(){
        return new GetCache();
    }
    @Bean
    public RemoteHTTPCall remoteHTTPCall(DiscoveryClient discoveryClient){
        return new RemoteHTTPCall(discoveryClient);
    }
    @Bean
    public SetCache setCache(){
        return new SetCache();
    }
    @Bean
    public RemoteTCPCall remoteTCPCall(DiscoveryClient discoveryClient){
        return new RemoteTCPCall(discoveryClient);
    }

    @Bean
    public ChainHandler chainHandler(
            Mappings mappings,
            RemoveCache removeCache,
            GetCache getCache,
            RemoteHTTPCall remoteHTTPCall,
            SetCache setCache,
            RemoteTCPCall remoteTCPCall){

        ChainHandler chainHandler = new ChainHandler();

        chainHandler.use(HandlerType.HTTP, mappings);
        chainHandler.use(HandlerType.HTTP, removeCache);
        chainHandler.use(HandlerType.HTTP, getCache);
        chainHandler.use(HandlerType.HTTP, remoteHTTPCall);
        chainHandler.use(HandlerType.HTTP, setCache);

        chainHandler.use(HandlerType.SOCKET, mappings);
        chainHandler.use(HandlerType.SOCKET, removeCache);
        chainHandler.use(HandlerType.SOCKET, getCache);
        chainHandler.use(HandlerType.SOCKET, remoteTCPCall);
        chainHandler.use(HandlerType.SOCKET, setCache);

        return chainHandler;
    }

    @Bean
    public Proxy proxy(){
        return new Proxy();
    }

    @Bean
    @ConditionalOnProperty(value = "proxy.cache.strategy", havingValue = "FileCache")
    public FileCache cacheStrategyFile(){
        return new FileCache();
    }

    @Bean
    public CacheManager cacheManager(){
        return new CacheManager();
    }
}
