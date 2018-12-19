package io.github.pabloubal.mockxy.core.handlers.socket;

import io.github.pabloubal.mockxy.core.Request;
import io.github.pabloubal.mockxy.core.Response;
import io.github.pabloubal.mockxy.core.handlers.BaseHandler;
import io.github.pabloubal.mockxy.utils.Constants;
import io.github.pabloubal.mockxy.utils.Mapping;
import org.springframework.stereotype.Component;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Socket;
import java.net.URL;
import java.util.Arrays;
import java.util.Objects;

@Component
public class RemoteTCPCall extends BaseHandler {
    private static final byte[] BUFFER = new byte[1024];
    private static final byte[] NEWLINE = {'\r','\n' };

    @Override
    public int run(Request request, Response response) {
        Mapping mapping = (Mapping) request.getAuxiliar().get(Constants.AUX_MAPPING);

        Socket sock = new Socket();
        String body = "";
        String readline;

        try {
            sock.connect(new InetSocketAddress(mapping.getHost(), mapping.getPort()), mapping.getTimeout());
            sock.setSoTimeout(mapping.getTimeout());

            InputStream in = sock.getInputStream();
            clearSocket(in);

            BufferedOutputStream out = new BufferedOutputStream(sock.getOutputStream());
            out.write(request.getBody().getBytes());
            out.write(NEWLINE);
            out.flush();

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            while((readline = br.readLine()) != null){
                if(body.length()>0){
                    body+="\n";
                }

                body+=readline;
            }
            sock.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        response.setBody(body);

        return super.run(request, response);
    }

    private void clearSocket(InputStream in) throws IOException {
        while (in.available() > 0) {
            in.read(BUFFER, 0, 1024);
        }
    }
}
