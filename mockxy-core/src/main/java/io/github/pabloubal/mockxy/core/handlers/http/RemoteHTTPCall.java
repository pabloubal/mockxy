package io.github.pabloubal.mockxy.core.handlers.http;

import io.github.pabloubal.mockxy.core.ChainLink;
import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.requests.Response;
import io.github.pabloubal.mockxy.core.handlers.BaseHandler;
import io.github.pabloubal.mockxy.core.utils.Constants;
import io.github.pabloubal.mockxy.core.utils.Mapping;
import io.github.pabloubal.mockxy.core.utils.MockxyProxySelector;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.routing.HttpRoutePlanner;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.SystemDefaultRoutePlanner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;
import java.util.Map;

public class RemoteHTTPCall extends BaseHandler {
    HttpRoutePlanner routePlanner;

    public RemoteHTTPCall(){
        this.routePlanner = new SystemDefaultRoutePlanner(new MockxyProxySelector());
    }


    @Override
    public int run(Request request, Response response, ChainLink nextLink) {
        Mapping mapping = (Mapping) request.getAuxiliar().get(Constants.AUX_MAPPING);
        String method;

        String[] tmpMethod = request.getHeader().get(Constants.HTTP_HEADER_METHOD).split(" ");
        method = tmpMethod[0];
        String query = String.join(" ", Arrays.copyOfRange(tmpMethod, 1, tmpMethod.length-1));

        if(!query.contains("http://")){
            query = "http://" + query;
        }

        HttpClient httpClient = HttpClientBuilder.create().setRoutePlanner(this.routePlanner).build();

        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));

        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        request.getHeader().entrySet().stream()
                .forEach(e->{
                    if(e.getKey()==Constants.HTTP_HEADER_METHOD)
                        return;

                    headers.add(e.getKey(), e.getValue());
                });
        HttpEntity<?> rqObject = new HttpEntity<Object>(request.getBody(), headers);


        String respStatusCode="";
        String respBody="";
        HttpHeaders respHeaders=null;


        try {
            ResponseEntity<String> responseEntity = restTemplate.exchange(query, HttpMethod.resolve(method), rqObject, String.class);

            respStatusCode = responseEntity.getStatusCode().toString() +
                    " " +
                    responseEntity.getStatusCode().getReasonPhrase();

            respHeaders = responseEntity.getHeaders();
            respBody = responseEntity.getBody();
        }
        catch(HttpStatusCodeException e){
            respStatusCode = e.getStatusCode().toString() + " " + e.getStatusCode().getReasonPhrase();
            respHeaders = e.getResponseHeaders();
            respBody = e.getResponseBodyAsString();
        }
        finally {
            response.setStatusCode("HTTP/1.1 " + respStatusCode);
            respHeaders.entrySet().forEach(e -> {
                if (e.getKey() == null) {
                    return;
                }
                response.getHeader().put(e.getKey(), e.getValue().get(0));
            });

            response.setBody(respBody);

            return super.run(request, response, nextLink);
        }
    }
}
