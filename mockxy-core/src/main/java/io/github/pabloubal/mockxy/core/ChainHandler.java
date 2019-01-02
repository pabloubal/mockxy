package io.github.pabloubal.mockxy.core;

import io.github.pabloubal.mockxy.core.handlers.HandlerType;
import io.github.pabloubal.mockxy.core.handlers.IHandler;
import io.github.pabloubal.mockxy.core.handlers.generic.HandlerEnd;
import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.requests.Response;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChainHandler {
    @Autowired
    HandlerEnd handlerEnd;

    private Map<HandlerType, IHandler> firstHandlers;
    private Map<HandlerType, IHandler> lastHandlers;

    public ChainHandler(){
        firstHandlers = new ConcurrentHashMap<>();
        lastHandlers = new ConcurrentHashMap<>();
        handlerEnd = new HandlerEnd();
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
