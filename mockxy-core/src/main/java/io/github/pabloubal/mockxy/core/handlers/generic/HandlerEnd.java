package io.github.pabloubal.mockxy.core.handlers.generic;

import io.github.pabloubal.mockxy.core.ChainLink;
import io.github.pabloubal.mockxy.core.handlers.BaseHandler;
import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.requests.Response;
import io.github.pabloubal.mockxy.core.handlers.IHandler;

public class HandlerEnd extends BaseHandler {
    @Override
    public int run(Request request, Response response, ChainLink nextLink) {
        return 0;
    }

}
