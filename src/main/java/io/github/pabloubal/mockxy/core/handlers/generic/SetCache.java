package io.github.pabloubal.mockxy.core.handlers.generic;

import io.github.pabloubal.mockxy.core.Request;
import io.github.pabloubal.mockxy.core.Response;
import io.github.pabloubal.mockxy.core.handlers.BaseHandler;
import io.github.pabloubal.mockxy.utils.CacheManager;
import io.github.pabloubal.mockxy.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class SetCache extends BaseHandler {
    @Autowired
    private CacheManager cacheManager;

    @Override
    public int run(Request request, Response response) {
        this.cacheManager.set(request, response);

        return super.run(request, response);
    }
}


