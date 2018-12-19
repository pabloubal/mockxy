package io.github.pabloubal.mockxy.core.handlers.generic;

import io.github.pabloubal.mockxy.core.Request;
import io.github.pabloubal.mockxy.core.Response;
import io.github.pabloubal.mockxy.core.handlers.BaseHandler;
import io.github.pabloubal.mockxy.utils.Constants;
import io.github.pabloubal.mockxy.utils.Mapping;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Component
@ConfigurationProperties
public class Mappings extends BaseHandler {
    private Map<String, Mapping> mappings = new HashMap<>();
    private String tcpSeparator;


    @Override
    public int run(Request request, Response response) {
        String key;

        String protocol = request.getHeader().get(Constants.MAPPINGS_PROTOCOL);

        if(protocol.equals(Constants.MAPPINGS_PROTO_HTTP) || protocol.equals(Constants.MAPPINGS_PROTO_HTTPS)){
            key = request.getHeader().get(Constants.HTTP_HEADER_HOST).split("\\.")[0];
        }
        else{
            key = request.getBody().split(tcpSeparator)[0];
        }

        if(!mappings.containsKey(key)){
            response.setBody("No mapping found for " + key);

            response.getHeader().put("HTTP/1.1", "404");
            response.getHeader().put("Content-Type","text/plain");
            response.getHeader().put("Content-Length", String.valueOf(response.getBody().length()));
            return -1;
        }

        Mapping mapping = mappings.get(key);

        request.getAuxiliar().put(Constants.AUX_MAPPING_KEY, key);
        request.getAuxiliar().put(Constants.AUX_MAPPING, mapping);

        return super.run(request, response);
    }

    public Map<String, Mapping> getMappings() {
        return mappings;
    }

    public void setMappings(Map<String, Mapping> mappings) {
        this.mappings = mappings;
    }

    public void setTcpSeparator(String tcpSeparator) {
        this.tcpSeparator = tcpSeparator;
    }
}


