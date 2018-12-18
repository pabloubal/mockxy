package io.github.pabloubal.mockxy.core.handlers;

import io.github.pabloubal.mockxy.core.Request;
import io.github.pabloubal.mockxy.core.Response;

public interface IHandler {
    int run(Request request, Response response);
    int setNextHandler(IHandler handler);
    IHandler getNextHandler();
}
