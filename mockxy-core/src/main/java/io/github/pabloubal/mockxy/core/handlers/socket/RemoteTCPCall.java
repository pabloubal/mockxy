package io.github.pabloubal.mockxy.core.handlers.socket;

import io.github.pabloubal.mockxy.core.ChainLink;
import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.requests.Response;
import io.github.pabloubal.mockxy.core.handlers.BaseHandler;
import io.github.pabloubal.mockxy.core.utils.Constants;
import io.github.pabloubal.mockxy.core.utils.Mapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;

import javax.annotation.PostConstruct;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.List;

public class RemoteTCPCall extends BaseHandler {
    private static final byte[] BUFFER = new byte[1024];
    private static final byte[] NEWLINE = {'\r','\n' };

    private DiscoveryClient discoveryClient;

    public RemoteTCPCall(DiscoveryClient discoveryClient){
        this.discoveryClient = discoveryClient;
    }

    @PostConstruct
    public void init(){
        return;
    }

    @Override
    public int run(Request request, Response response, ChainLink nextLink) {
        boolean retError = false;

        Mapping mapping = (Mapping) request.getAuxiliar().get(Constants.AUX_MAPPING);

        Socket sock = new Socket();
        String body = "";
        String readline;

        try {

            if(mapping.getDiscovery()){
                List<ServiceInstance> instances = discoveryClient.getInstances(mapping.getHost());

                if(instances.size()>0){
                    sock.connect(new InetSocketAddress(instances.get(0).getHost(), instances.get(0).getPort()), mapping.getTimeout());
                }
            }
            else {
                sock.connect(new InetSocketAddress(mapping.getHost(), mapping.getPort()), mapping.getTimeout());
            }

            sock.setSoTimeout(mapping.getTimeout());

            InputStream in = sock.getInputStream();
            clearSocket(in);

            BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());
            out.write(request.getBody().getBytes());
            out.write(NEWLINE);
            out.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(in));

            for(int i=0;
                (mapping.getLinesToRead()==0 || i<mapping.getLinesToRead()) && (readline = br.readLine()) != null;
                i++){

                if(body.length()>0){
                    body+="\n";
                }

                body+=readline;
            }

            sock.close();
        } catch (MalformedURLException e) {
            body = e.getMessage();
            retError=true;
        } catch (ProtocolException e) {
            body = e.getMessage();
            retError=true;
        } catch (SocketTimeoutException e){
            if(body.length()==0) {
                body = e.getMessage();
                retError = true;
            }
        } catch (IOException e) {
            body = e.getMessage();
            retError=true;
        }
        finally {
            response.setBody(body);
        }

        if(retError)
            return -1;

        return super.run(request, response, nextLink);
    }

    private void clearSocket(InputStream in) throws IOException {
        while (in.available() > 0) {
            in.read(BUFFER, 0, 1024);
        }
    }
}
