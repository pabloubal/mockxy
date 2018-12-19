package io.github.pabloubal.mockxy.core;

import io.github.pabloubal.mockxy.core.handlers.HandlerType;
import io.github.pabloubal.mockxy.utils.Constants;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestHandler implements Runnable {

    Socket clientSocket;
    BufferedReader proxyToClientBr;
    BufferedWriter proxyToClientBw;
    ChainHandler chainHandler;

    /**
     * Thread that is used to transmit data read from client to server when using HTTPS
     * Reference to this is required so it can be closed once completed.
     */
    private Thread httpsClientToServer;


    public RequestHandler(Socket clientSocket, ChainHandler chainHandler){
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
        try {
            String firstLine = proxyToClientBr.readLine();

            if(firstLine.contains("HTTP/1.1") || firstLine.contains("HTTP/2")){
                this.manageHTTP(firstLine);
            }
            else{
                this.manageSocket(firstLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void manageHTTP(String firstLine){
        List<String> header = new ArrayList<>();
        String body = "";
        String tmpHdr;

        try {
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
            response += resp.getStatusCode() + "\n";
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

    private void manageSocket(String firstLine){
        String message = firstLine;

        try {
            while (proxyToClientBr.ready()) {
                message += (char) proxyToClientBr.read();
            }

            Request req = new Request();
            req.getHeader().put(Constants.MAPPINGS_PROTOCOL, Constants.MAPPINGS_PROTO_TCP);

            req.setBody(message);

            Response resp = this.chainHandler.run(HandlerType.SOCKET, req);

            String response = "";
            response+=resp.getBody();

            proxyToClientBw.write(response);
            proxyToClientBw.flush();
            proxyToClientBw.close();

        }
        catch(IOException e) {
            e.printStackTrace();
        }
    }




    /**
     * Handles HTTPS requests between client and remote server
     * @param urlString desired file to be transmitted over https
     */
    private void handleHTTPSRequest(String urlString){
        // Extract the URL and port of remote
        String url = urlString.substring(7);
        String pieces[] = url.split(":");
        url = pieces[0];
        int port  = Integer.valueOf(pieces[1]);

        try{
            // Only first line of HTTPS request has been read at this point (CONNECT *)
            // Read (and throw away) the rest of the initial data on the stream
            for(int i=0;i<5;i++){
                proxyToClientBr.readLine();
            }

            // Get actual IP associated with this URL through DNS
            InetAddress address = InetAddress.getByName(url);

            // Open a socket to the remote server
            Socket proxyToServerSocket = new Socket(address, port);
            proxyToServerSocket.setSoTimeout(5000);

            // Send Connection established to the client
            String line = "HTTP/1.0 200 Connection established\r\n" +
                    "Proxy-Agent: ProxyServer/1.0\r\n" +
                    "\r\n";
            proxyToClientBw.write(line);
            proxyToClientBw.flush();



            // Client and Remote will both start sending data to proxy at this point
            // Proxy needs to asynchronously read data from each party and send it to the other party


            //Create a Buffered Writer betwen proxy and remote
            BufferedWriter proxyToServerBW = new BufferedWriter(new OutputStreamWriter(proxyToServerSocket.getOutputStream()));

            // Create Buffered Reader from proxy and remote
            BufferedReader proxyToServerBR = new BufferedReader(new InputStreamReader(proxyToServerSocket.getInputStream()));



            // Create a new thread to listen to client and transmit to server
            ClientToServerHttpsTransmit clientToServerHttps =
                    new ClientToServerHttpsTransmit(clientSocket.getInputStream(), proxyToServerSocket.getOutputStream());

            httpsClientToServer = new Thread(clientToServerHttps);
            httpsClientToServer.start();


            // Listen to remote server and relay to client
            try {
                byte[] buffer = new byte[4096];
                int read;
                do {
                    read = proxyToServerSocket.getInputStream().read(buffer);
                    if (read > 0) {
                        clientSocket.getOutputStream().write(buffer, 0, read);
                        if (proxyToServerSocket.getInputStream().available() < 1) {
                            clientSocket.getOutputStream().flush();
                        }
                    }
                } while (read >= 0);
            }
            catch (SocketTimeoutException e) {

            }
            catch (IOException e) {
                e.printStackTrace();
            }


            // Close Down Resources
            if(proxyToServerSocket != null){
                proxyToServerSocket.close();
            }

            if(proxyToServerBR != null){
                proxyToServerBR.close();
            }

            if(proxyToServerBW != null){
                proxyToServerBW.close();
            }

            if(proxyToClientBw != null){
                proxyToClientBw.close();
            }


        } catch (SocketTimeoutException e) {
            String line = "HTTP/1.0 504 Timeout Occured after 10s\n" +
                    "User-Agent: ProxyServer/1.0\n" +
                    "\r\n";
            try{
                proxyToClientBw.write(line);
                proxyToClientBw.flush();
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        catch (Exception e){
            System.out.println("Error on HTTPS : " + urlString );
            e.printStackTrace();
        }
    }




    /**
     * Listen to data from client and transmits it to server.
     * This is done on a separate thread as must be done
     * asynchronously to reading data from server and transmitting
     * that data to the client.
     */
    class ClientToServerHttpsTransmit implements Runnable{

        InputStream proxyToClientIS;
        OutputStream proxyToServerOS;

        /**
         * Creates Object to Listen to Client and Transmit that data to the server
         * @param proxyToClientIS Stream that proxy uses to receive data from client
         * @param proxyToServerOS Stream that proxy uses to transmit data to remote server
         */
        public ClientToServerHttpsTransmit(InputStream proxyToClientIS, OutputStream proxyToServerOS) {
            this.proxyToClientIS = proxyToClientIS;
            this.proxyToServerOS = proxyToServerOS;
        }

        @Override
        public void run(){
            try {
                // Read byte by byte from client and send directly to server
                byte[] buffer = new byte[4096];
                int read;
                do {
                    read = proxyToClientIS.read(buffer);
                    if (read > 0) {
                        proxyToServerOS.write(buffer, 0, read);
                        if (proxyToClientIS.available() < 1) {
                            proxyToServerOS.flush();
                        }
                    }
                } while (read >= 0);
            }
            catch (SocketTimeoutException ste) {
                // TODO: handle exception
            }
            catch (IOException e) {
                System.out.println("Proxy to client HTTPS read timed out");
                e.printStackTrace();
            }
        }
    }

}



