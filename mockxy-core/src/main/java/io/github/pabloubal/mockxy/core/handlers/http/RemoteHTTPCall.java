package io.github.pabloubal.mockxy.core.handlers.http;

import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.requests.Response;
import io.github.pabloubal.mockxy.core.handlers.BaseHandler;
import io.github.pabloubal.mockxy.core.utils.Constants;
import io.github.pabloubal.mockxy.core.utils.Mapping;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

public class RemoteHTTPCall extends BaseHandler {

    private DiscoveryClient discoveryClient;

    public RemoteHTTPCall(DiscoveryClient discoveryClient){
        this.discoveryClient = discoveryClient;
    }

    @Override
    public int run(Request request, Response response) {
        Mapping mapping = (Mapping) request.getAuxiliar().get(Constants.AUX_MAPPING);
        String method;

        String[] tmpMethod = request.getHeader().get(Constants.HTTP_HEADER_METHOD).split(" ");
        method = tmpMethod[0];
        String query = String.join(" ", Arrays.copyOfRange(tmpMethod, 1, tmpMethod.length-1));

        if(!query.contains("http://")){
            query = "http://" + mapping.getHost() + "/" + query;
        }

        try {
            URL url = new URL(query);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod(method);
            request.getHeader().entrySet().stream()
                    .forEach(e->{
                        if(e.getKey()==Constants.HTTP_HEADER_METHOD)
                            return;

                        conn.setRequestProperty(e.getKey(), e.getValue());
                    });

            if(method != "GET"){
                conn.setDoOutput(true);

                OutputStream os = conn.getOutputStream();
                os.write(request.getBody().getBytes());
                os.flush();
            }

            int statusCode = conn.getResponseCode();

            InputStream is;

            if(statusCode<400){
                is = conn.getInputStream();
            }
            else{
                is = conn.getErrorStream();
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((is)));


            String body = "";
            String readline;

            while((readline = br.readLine()) != null){
                if(body.length()>0){
                    body+="\n";
                }

                body+=readline;
            }

            conn.disconnect();

            response.setStatusCode(String.valueOf(statusCode));
            conn.getHeaderFields().entrySet().forEach(e-> {
                if(Objects.isNull(e.getKey())) {
                    return;
                }
                response.getHeader().put(e.getKey(), e.getValue().get(0));
            });

            response.setBody(body);


        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return super.run(request, response);
    }
}
