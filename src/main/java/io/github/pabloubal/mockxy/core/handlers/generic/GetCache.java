package io.github.pabloubal.mockxy.core.handlers.generic;

import io.github.pabloubal.mockxy.core.Request;
import io.github.pabloubal.mockxy.core.Response;
import io.github.pabloubal.mockxy.core.handlers.BaseHandler;
import io.github.pabloubal.mockxy.utils.CacheManager;
import io.github.pabloubal.mockxy.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class GetCache extends BaseHandler {
    @Autowired
    private CacheManager cacheManager;

    @Override
    public int run(Request request, Response response) {
        Response cachedResp = this.cacheManager.get(request);

        //If present, return cached response
        if(!Objects.isNull(cachedResp)){
            cachedResp.getHeader().put(Constants.CACHED_RESPONSE_HEADER_KEY, Constants.CACHED_RESPONSE_HEADER_VALUE);

            response.setBody(cachedResp.getBody());
            response.setHeader(cachedResp.getHeader());
            response.setStatusCode(cachedResp.getStatusCode());

            return 0;
        }

        //If not present, move to next handler
        return super.run(request, response);
    }
}


