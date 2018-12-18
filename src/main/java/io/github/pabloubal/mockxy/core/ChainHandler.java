package io.github.pabloubal.mockxy.core;

import io.github.pabloubal.mockxy.core.Request;
import io.github.pabloubal.mockxy.core.Response;
import io.github.pabloubal.mockxy.core.handlers.HandlerType;
import io.github.pabloubal.mockxy.core.handlers.IHandler;
import io.github.pabloubal.mockxy.core.handlers.generic.HandlerEnd;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChainHandler {
    @Autowired
    HandlerEnd handlerEnd;

    private Map<HandlerType, IHandler> firstHandlers;
    private Map<HandlerType, IHandler> lastHandlers;

    public ChainHandler(){
        firstHandlers = new ConcurrentHashMap<>();
        lastHandlers = new ConcurrentHashMap<>();
    }

    public int use(HandlerType ht, IHandler handler){
        handler.setNextHandler(handlerEnd);

        if(firstHandlers.get(ht) == null){
            firstHandlers.put(ht, handler);
            lastHandlers.put(ht, handler);
            return 0;
        }

        lastHandlers.get(ht).setNextHandler(handler);
        lastHandlers.put(ht, handler);

        return 0;
    }

    public Response run(HandlerType ht, Request request){
        Response resp = new Response();

        firstHandlers.get(ht).run(request, resp);

        return resp;
    }
}
