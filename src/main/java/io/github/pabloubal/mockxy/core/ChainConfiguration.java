package io.github.pabloubal.mockxy.core;

import io.github.pabloubal.mockxy.core.handlers.HandlerType;
import io.github.pabloubal.mockxy.core.handlers.generic.GetCache;
import io.github.pabloubal.mockxy.core.handlers.generic.Mappings;
import io.github.pabloubal.mockxy.core.handlers.generic.SetCache;
import io.github.pabloubal.mockxy.core.handlers.http.RemoteHTTPCall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChainConfiguration {
    @Autowired
    Mappings mappings;

    @Autowired
    GetCache getCache;

    @Autowired
    SetCache setCache;

    @Autowired
    RemoteHTTPCall remoteHTTPCall;

    @Bean
    ChainHandler chainHandler(){

        ChainHandler chainHandler = new ChainHandler();
        chainHandler.use(HandlerType.HTTP, mappings);
        chainHandler.use(HandlerType.HTTP, getCache);
        chainHandler.use(HandlerType.HTTP, remoteHTTPCall);
        chainHandler.use(HandlerType.HTTP, setCache);

        chainHandler.use(HandlerType.SOCKET, mappings);
        chainHandler.use(HandlerType.SOCKET, getCache);
        //chainHandler.use(HandlerType.SOCKET, remoteTCPCall);
        chainHandler.use(HandlerType.SOCKET, setCache);

        return chainHandler;
    }
}
