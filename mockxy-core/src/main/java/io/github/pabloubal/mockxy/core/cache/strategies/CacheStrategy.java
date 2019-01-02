package io.github.pabloubal.mockxy.core.cache.strategies;

import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.requests.Response;

public interface CacheStrategy {

    Response get(Request request);

    int set(Request request, Response response);

    int delete(Request request, Response response);
}
