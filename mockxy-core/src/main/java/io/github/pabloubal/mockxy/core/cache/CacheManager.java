package io.github.pabloubal.mockxy.core.cache;

import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.requests.Response;
import org.springframework.beans.factory.annotation.Autowired;
import io.github.pabloubal.mockxy.core.cache.strategies.CacheStrategy;

public class CacheManager {
    @Autowired
    private CacheStrategy cacheStrategy;

    public Response get(Request request){
        return this.getCacheStrategy().get(request);
    }
    public int set(Request request, Response response) { return this.getCacheStrategy().set(request, response); }

    public int delete(Request request, Response response) { return this.getCacheStrategy().delete(request, response); }


    public CacheStrategy getCacheStrategy() {
        return cacheStrategy;
    }

    public void setCacheStrategy(CacheStrategy cacheStrategy) {
        this.cacheStrategy = cacheStrategy;
    }
}
