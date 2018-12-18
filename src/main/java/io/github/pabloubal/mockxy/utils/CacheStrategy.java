package io.github.pabloubal.mockxy.utils;

import io.github.pabloubal.mockxy.core.Request;
import io.github.pabloubal.mockxy.core.Response;

public interface CacheStrategy {

    Response get(Request request);

    int set(Request request, Response response);
}
