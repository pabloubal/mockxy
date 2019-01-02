package io.github.pabloubal.mockxy.core.requests;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private String statusCode;
    private Map<String, String> header = new HashMap<>();
    private String body;


    public String getBody(){
        return this.body;
    }

    public void setBody(String body){
        this.body = body;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }
}
