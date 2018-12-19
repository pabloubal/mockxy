package io.github.pabloubal.mockxy.utils;

public class Mapping {
    private String host;
    private Integer port;
    private String dir;
    private Boolean matchHeaders = true;
    private Integer timeout = 3000;


    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }


    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Boolean getMatchHeaders() {
        return matchHeaders;
    }

    public void setMatchHeaders(Boolean matchHeaders) {
        this.matchHeaders = matchHeaders;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
