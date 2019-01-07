package io.github.pabloubal.mockxy.core.requests;

import io.github.pabloubal.mockxy.core.ChainHandler;
import io.github.pabloubal.mockxy.core.handlers.HandlerType;
import io.github.pabloubal.mockxy.core.utils.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class HTTPHandler implements Runnable {

    Socket clientSocket;
    BufferedReader proxyToClientBr;
    BufferedWriter proxyToClientBw;
    ChainHandler chainHandler;


    public HTTPHandler(Socket clientSocket, ChainHandler chainHandler){
        this.chainHandler = chainHandler;
        this.clientSocket = clientSocket;
        try{
            this.clientSocket.setSoTimeout(2000);
            proxyToClientBr = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            proxyToClientBw = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String firstLine;
        List<String> header = new ArrayList<>();
        String body = "";
        String tmpHdr;

        try {
            firstLine = proxyToClientBr.readLine();
            while ((tmpHdr = proxyToClientBr.readLine()).length() != 0) {
                header.add(tmpHdr);
            }

            if(!firstLine.split(" ")[0].contains("GET")) {
                while (proxyToClientBr.ready()) {
                    body += (char) proxyToClientBr.read();
                }
            }

            Request req = new Request();
            header.stream()
                    .map(s -> s.split(": "))
                    .forEach(a -> req.getHeader().put(a[0], a[1]));

            req.getHeader().put(Constants.MAPPINGS_PROTOCOL, Constants.MAPPINGS_PROTO_HTTP);
            req.getHeader().put(Constants.HTTP_HEADER_METHOD, firstLine);
            req.setBody(body);

            Response resp = this.chainHandler.run(HandlerType.HTTP, req);

            String response = "";
            response += resp.getStatusCode() + "\n"; //HTTP version and StatusCode HEADER

            for(Map.Entry<String, String> h : resp.getHeader().entrySet()){
                response += String.join(": ", h.getKey(), h.getValue()) + "\n";
            }
            response+="\r\n";
            response+=resp.getBody();

            proxyToClientBw.write(response);
            proxyToClientBw.flush();
            proxyToClientBw.close();
        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }

}



