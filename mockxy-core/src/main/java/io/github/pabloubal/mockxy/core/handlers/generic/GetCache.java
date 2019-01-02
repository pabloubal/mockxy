package io.github.pabloubal.mockxy.core.handlers.generic;

import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.requests.Response;
import io.github.pabloubal.mockxy.core.handlers.BaseHandler;
import io.github.pabloubal.mockxy.core.utils.Constants;
import io.github.pabloubal.mockxy.core.cache.CacheManager;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class GetCache extends BaseHandler {
    @Autowired
    private CacheManager cacheManager;

    @Override
    public int run(Request request, Response response) {
        Response cachedResp = this.cacheManager.get(request);

        //If present, return cached response
        if(!Objects.isNull(cachedResp)){
            cachedResp.getHeader().put(Constants.CACHED_RESPONSE_HEADER_KEY, Constants.CACHED_RESPONSE_HEADER_VALUE);

            if(request.getHeader().get(Constants.MAPPINGS_PROTOCOL).equals(Constants.MAPPINGS_PROTO_TCP)){
                response.setBody(cachedResp.getStatusCode());
            }
            else {

                response.setBody(cachedResp.getBody());
                response.setHeader(cachedResp.getHeader());
                response.setStatusCode(cachedResp.getStatusCode());
            }

            return 0;
        }

        //If not present, move to next handler
        return super.run(request, response);
    }
}


