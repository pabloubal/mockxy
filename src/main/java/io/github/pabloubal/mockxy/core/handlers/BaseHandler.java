package io.github.pabloubal.mockxy.core.handlers;

import io.github.pabloubal.mockxy.core.Request;
import io.github.pabloubal.mockxy.core.Response;

import java.util.Objects;

public class BaseHandler implements IHandler {

    private IHandler next = null;

    @Override
    public int run(Request request, Response response) {

        if(Objects.isNull(next)){
            return 0;
        }

        return next.run(request, response);
    }

    @Override
    public int setNextHandler(IHandler handler) {
        this.next = handler;
        return 0;
    }

    @Override
    public IHandler getNextHandler() {
        return this.next;
    }
}
