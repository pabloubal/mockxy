package io.github.pabloubal.mockxy.core.utils;

public class Mapping {
    private String dir;
    private MappingType type;
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

    public MappingType getType() {
        return type;
    }

    public void setType(MappingType type) {
        this.type = type;
    }

    public enum MappingType{
        TCP,
        HTTP
    }

}
