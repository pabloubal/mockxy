package io.github.pabloubal.mockxy.core.handlers.socket;

import io.github.pabloubal.mockxy.core.ChainLink;
import io.github.pabloubal.mockxy.core.requests.Request;
import io.github.pabloubal.mockxy.core.requests.Response;
import io.github.pabloubal.mockxy.core.handlers.BaseHandler;
import io.github.pabloubal.mockxy.core.requests.SOCKSHandler;
import io.github.pabloubal.mockxy.core.utils.Constants;
import io.github.pabloubal.mockxy.core.utils.Mapping;

import javax.annotation.PostConstruct;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Socket;
import java.net.SocketTimeoutException;

public class RemoteTCPCall extends BaseHandler {
    private static final byte[] BUFFER = new byte[1024];
    private static final byte[] NEWLINE = {'\r','\n' };

    @PostConstruct
    public void init(){
        return;
    }

    @Override
    public int run(Request request, Response response, ChainLink nextLink) {
        boolean retError = false;

        Mapping mapping = (Mapping) request.getAuxiliar().get(Constants.AUX_MAPPING);

        Socket sock = new Socket(Proxy.NO_PROXY);
        String body = "";
        String readline;

        try {

            SOCKSHandler socksHandler = (SOCKSHandler) request.getAuxiliar().get(SOCKSHandler.SOCKSHANDLER);

            if( socksHandler != null ){
                sock.connect(new InetSocketAddress(socksHandler.getIpAddress(), socksHandler.getPort()), 3000);
                sock.setSoTimeout(3000);
            }

            InputStream in = sock.getInputStream();
            clearSocket(in);

            BufferedOutputStream bos = new BufferedOutputStream(sock.getOutputStream());
            bos.write(request.getBody().getBytes());
            bos.flush();

            byte[] read = new byte[1];

            do{
                in.read(read);
                body+=new String(read);
            }while(in.available()>0 && (read=new byte[in.available()])!=null);

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

    private String readWithTimeout(InputStream in, int timeoutMilis) throws IOException {
        byte[] read;
        String aux = "";

        long untilMilis = System.currentTimeMillis()+timeoutMilis;

        for(read=new byte[in.available()];
            in.read(read) >= 0 && System.currentTimeMillis()<untilMilis;
            read=new byte[in.available()]){

            aux+=new String(read);
        }

        return aux;

    }

    private void clearSocket(InputStream in) throws IOException {
        while (in.available() > 0) {
            in.read(BUFFER, 0, 1024);
        }
    }
}
