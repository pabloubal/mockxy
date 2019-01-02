package io.github.pabloubal.mockxy.core.handlers.generic;

import io.github.pabloubal.mockxy.core.requests.Response;
import io.github.pabloubal.mockxy.core.handlers.BaseHandler;
import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

public class SetCache extends BaseHandler {
    @Autowired
    private CacheManager cacheManager;

    @Override
    public int run(Request request, Response response) {
        this.cacheManager.set(request, response);

        return super.run(request, response);
    }
}


