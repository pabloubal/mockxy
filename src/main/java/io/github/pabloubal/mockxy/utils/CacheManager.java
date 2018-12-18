package io.github.pabloubal.mockxy.utils;

import io.github.pabloubal.mockxy.core.Request;
import io.github.pabloubal.mockxy.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CacheManager {
    @Autowired
    private CacheStrategy cacheStrategy;

    public Response get(Request request){
        return this.getCacheStrategy().get(request);
    }
    public int set(Request request, Response response) { return this.getCacheStrategy().set(request, response); }


    public CacheStrategy getCacheStrategy() {
        return cacheStrategy;
    }

    public void setCacheStrategy(CacheStrategy cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
    }
}
