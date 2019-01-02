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

    private ChainLink endLink;

    private Map<HandlerType, ChainLink> firstLink;
    private Map<HandlerType, ChainLink> lastLink;

    public ChainHandler(){
        firstLink = new ConcurrentHashMap<>();
        lastLink = new ConcurrentHashMap<>();
        handlerEnd = new HandlerEnd();

        endLink = new ChainLink();
        endLink.setHandler(handlerEnd);
        endLink.setNextLink(null);
    }

    public int use(HandlerType ht, IHandler handler){

        ChainLink cl = new ChainLink();
        cl.setHandler(handler);
        cl.setNextLink(endLink);

        if(firstLink.get(ht) == null){
            firstLink.put(ht, cl);
            lastLink.put(ht, cl);
            return 0;
        }

        lastLink.get(ht).setNextLink(cl);
        lastLink.put(ht, cl);

        return 0;
    }

    public Response run(HandlerType ht, Request request){
        Response resp = new Response();

        ChainLink link = firstLink.get(ht);

        link.getHandler().run(request, resp, link.getNextLink());

        return resp;
    }


}
