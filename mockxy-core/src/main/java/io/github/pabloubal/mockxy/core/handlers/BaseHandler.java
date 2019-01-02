package io.github.pabloubal.mockxy.core.handlers;

import io.github.pabloubal.mockxy.core.ChainLink;
import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.requests.Response;

import java.util.Objects;

public class BaseHandler implements IHandler {

    @Override
    public int run(Request request, Response response, ChainLink nextLink) {

        if(Objects.isNull(nextLink)){
            return 0;
        }

        return nextLink.getHandler().run(request, response, nextLink.getNextLink());
    }
}
