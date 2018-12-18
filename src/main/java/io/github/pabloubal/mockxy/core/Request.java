package io.github.pabloubal.mockxy.core;


import java.util.HashMap;
import java.util.Map;

public class Request {

    private Map<String, String> header = new HashMap<>();
    private String body;
    private Map<String, Object> auxiliar = new HashMap<>();

    public String getBody(){
        return this.body;
    }

    public void setBody(String body){
        this.body = body;
    }


    public Map<String, Object> getAuxiliar() {
        return auxiliar;
    }

    public void setAuxiliar(Map<String, Object> auxiliar) {
        this.auxiliar = auxiliar;
    }

    public Map<String, String> getHeader() {
        return header;
    }

    public void setHeader(Map<String, String> header) {
        this.header = header;
    }
}
