package io.github.pabloubal.mockxy.core.utils;

public class Mapping {
    private String dir;
    private Boolean matchHeaders = true;
    private Integer timeout = 3000;


    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }


    public Boolean getMatchHeaders() {
        return matchHeaders;
    }

    public void setMatchHeaders(Boolean matchHeaders) {
        this.matchHeaders = matchHeaders;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

}
