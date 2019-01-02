package io.github.pabloubal.mockxy.core;

import io.github.pabloubal.mockxy.core.handlers.IHandler;

public class ChainLink{
    private IHandler handler;
    private ChainLink nextLink;


    public IHandler getHandler() {
        return handler;
    }

    public void setHandler(IHandler handler) {
        this.handler = handler;
    }

    public ChainLink getNextLink() {
        return nextLink;
    }

    public void setNextLink(ChainLink nextHandler) {
        this.nextLink = nextHandler;
    }
}