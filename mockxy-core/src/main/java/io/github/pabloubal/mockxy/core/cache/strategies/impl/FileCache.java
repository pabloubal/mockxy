package io.github.pabloubal.mockxy.core.cache.strategies.impl;

import io.github.pabloubal.mockxy.core.cache.strategies.CacheStrategy;
import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.requests.Response;
import io.github.pabloubal.mockxy.core.utils.Constants;
import io.github.pabloubal.mockxy.core.utils.Mapping;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.xml.bind.DatatypeConverter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.stream.Collectors;

public class FileCache implements CacheStrategy {
    @Value("${mockxy.cache.baseDir}")
    private String baseDir;

    @PostConstruct
    public void init(){
        return;
    }

    @Override
    public Response get(Request request) {

        Response resp = new Response();

        try {
            String[] filePath = this.getFileName(request);

            BufferedReader br = new BufferedReader(new FileReader(new java.io.File(
                    filePath[0] + File.separator + filePath[1])));

            String status = br.readLine();
            resp.setStatusCode(status);

            String readline = "";

            while((readline = br.readLine()) != null && readline.length()>0){
                String[] splitHeader = readline.split(": ");
                resp.getHeader().put(splitHeader[0], splitHeader[1]);
            }

            String body = "";

            while((readline = br.readLine()) != null){
                if(body.length()>0){
                    body+="\n";
                }

                body+=readline;
            }

            resp.setBody(body);

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return resp;
    }

    @Override
    public int set(Request request, Response response) {
        try {

            String[] filePath = this.getFileName(request);

            File f = new File(filePath[0] + File.separator + filePath[1]);

            if(!f.exists()){
                new File(filePath[0]).mkdirs();
                f.createNewFile();
            }

            BufferedWriter bw = new BufferedWriter(new FileWriter(f));

            if(request.getHeader().get(Constants.MAPPINGS_PROTOCOL).equals(Constants.MAPPINGS_PROTO_TCP)){
                this.writeTCP(request, response, bw);
            }
            else{
                this.writeHTTP(request, response, bw);
            }

            bw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    @Override
    public int delete(Request request, Response response) {
        try {
            String[] filePath = this.getFileName(request);
            File f = new File(filePath[0] + File.separator + filePath[1]);

            f.delete();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return -1;
        }

        return 0;
    }

    private void writeHTTP(Request request, Response response, BufferedWriter bw) throws IOException {
        String[] method = request.getHeader().get(Constants.HTTP_HEADER_METHOD).split(" ");
        //String proto = method[method.length - 1];

        bw.write(response.getStatusCode());
        bw.newLine();

        response.getHeader().entrySet().stream()
                .forEach(e -> {
                    try {
                        bw.write(String.join(": ", e.getKey(), e.getValue()));
                        bw.newLine();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                });

        bw.newLine();
        bw.write(response.getBody());

    }

    private void writeTCP(Request request, Response response, BufferedWriter bw) throws IOException {
        bw.write(response.getBody());
    }

    private String[] getFileName(Request request) throws NoSuchAlgorithmException {
        String key = (String)request.getAuxiliar().get(Constants.AUX_MAPPING_KEY);
        Mapping mapping = (Mapping)request.getAuxiliar().get(Constants.AUX_MAPPING);

        String contentToHash = "";
        String method;

        if(request.getHeader().get(Constants.MAPPINGS_PROTOCOL).equals(Constants.MAPPINGS_PROTO_TCP)){
            contentToHash = request.getBody();
            method = Constants.MAPPINGS_PROTO_TCP;
        }
        else{

            if(mapping.getMatchHeaders()){

                contentToHash += "HEADERS:" + request.getHeader().entrySet().stream()
                        .map(Object::toString)
                        .collect(Collectors.joining("~"));
            }

            String[] tmpMethod = request.getHeader().get(Constants.HTTP_HEADER_METHOD).split(" ");

            method = tmpMethod[0];

            String query = String.join(" ", Arrays.copyOfRange(tmpMethod, 1, tmpMethod.length-1));

            contentToHash += "#" + query;

            if(method != "GET"){
                contentToHash += "#" + request.getBody();
            }
        }


        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update(contentToHash.getBytes());

        String fileName = DatatypeConverter.printHexBinary(md5.digest()).toUpperCase();


        return new String[] {MessageFormat.format("{0}/{1}/{2}/{3}",
                baseDir, mapping.getDir(), key, method), fileName};
    }

    public String getBaseDir() {
        return baseDir;
    }

    public void setBaseDir(String baseDir) {
        this.baseDir = baseDir;
    }
}
