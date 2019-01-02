package io.github.pabloubal.mockxy.core.handlers.generic;

import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.requests.Response;
import io.github.pabloubal.mockxy.core.handlers.IHandler;

public class HandlerEnd implements IHandler {
    @Override
    public int run(Request request, Response response) {
        return 0;
    }

    @Override
    public int setNextHandler(IHandler handler) {
        return 0;
    }

    @Override
    public IHandler getNextHandler() {
        return null;
    }
}
