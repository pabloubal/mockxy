package io.github.pabloubal.mockxy.core.handlers.generic;

import io.github.pabloubal.mockxy.core.ChainLink;
import io.github.pabloubal.mockxy.core.requests.Response;
import io.github.pabloubal.mockxy.core.handlers.BaseHandler;
import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.cache.CacheManager;
import io.github.pabloubal.mockxy.core.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class SetCache extends BaseHandler {
    @Autowired
    private CacheManager cacheManager;

    @Override
    public int run(Request request, Response response, ChainLink nextLink) {

        //If NO mapping found, then just act as a simple proxy
        if(! Objects.isNull(request.getAuxiliar().get(Constants.AUX_MAPPING)) ) {
            this.cacheManager.set(request, response);
        }

        return super.run(request, response, nextLink);
    }
}


