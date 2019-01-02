package io.github.pabloubal.mockxy.core.handlers.generic;

import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.requests.Response;
import io.github.pabloubal.mockxy.core.handlers.BaseHandler;
import io.github.pabloubal.mockxy.core.cache.CacheManager;
import io.github.pabloubal.mockxy.core.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;

public class RemoveCache extends BaseHandler {
    @Autowired
    private CacheManager cacheManager;

    @Override
    public int run(Request request, Response response) {

        if(request.getHeader().get(Constants.MAPPINGS_PROTOCOL).equals(Constants.MAPPINGS_PROTO_HTTP) ||
                request.getHeader().get(Constants.MAPPINGS_PROTOCOL).equals(Constants.MAPPINGS_PROTO_HTTPS)){

            String deleteCache = request.getHeader().get(Constants.HTTP_HEADER_DELETE_CACHE);

            if(!Objects.isNull(deleteCache) && deleteCache.toLowerCase().equals("true")){
                this.cacheManager.delete(request, response);
            }

        }
        else{
            if(request.getBody().contains(Constants.TCP_DELETE_CACHE)){
                request.setBody( request.getBody().replaceAll(Constants.TCP_DELETE_CACHE, "") );

                this.cacheManager.delete(request, response);
            }

        }


        return super.run(request, response);
    }
}


