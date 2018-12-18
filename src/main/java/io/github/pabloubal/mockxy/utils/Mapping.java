package io.github.pabloubal.mockxy.utils;

public class Mapping {
    private String host;
    private String dir;
    private Boolean matchHeaders = true;


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
}
