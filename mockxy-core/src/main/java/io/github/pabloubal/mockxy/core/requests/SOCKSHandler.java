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

public class SOCKSHandler implements Runnable {
    public static final String SOCKSHANDLER="SOCKSHANDLER";

    Socket clientSocket;
    BufferedReader proxyToClientBr;
    BufferedWriter proxyToClientBw;
    ChainHandler chainHandler;



    String ipAddress;

    Integer port;

    /**
     * Thread that is used to transmit data read from client to server when using HTTPS
     * Reference to this is required so it can be closed once completed.
     */
    private Thread httpsClientToServer;


    public SOCKSHandler(Socket clientSocket, ChainHandler chainHandler){
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
        String message="";


        try {
            if(!this.initSocks5()){
                proxyToClientBw.close();
                this.clientSocket.close();

                return;
            }

            System.out.println(String.format("New SOCKS connection to %s:%d",this.getIpAddress(), this.getPort()));

            //Wait till ready
            for(int i=0; i<10 && !proxyToClientBr.ready(); i++){
                Thread.sleep(1);
            }

            Request req = new Request();
            req.getHeader().put(Constants.MAPPINGS_PROTOCOL, Constants.MAPPINGS_PROTO_TCP);
            req.getAuxiliar().put(SOCKSHANDLER, this);

            byte[] read = new byte[1];

            do{
                this.clientSocket.getInputStream().read(read);
                message+=new String(read);
            }while(this.clientSocket.getInputStream().available()>0 && (read=new byte[this.clientSocket.getInputStream().available()])!=null);

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
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean initSocks5() throws IOException {
        byte[] readBytes;
        byte[] sendBytes;

        byte addressType;
        byte[] destAddrBytes;
        String destAddr="";
        byte[] destPortBytes;
        int destPort;


        //CLIENT REQUEST: Read SOCKS Greeting from Client
        //0: SOCKS Version
        //1: Number of authentication methods
        //2..N: Authentication methods
        readBytes = new byte[2];
        clientSocket.getInputStream().read(readBytes);

        if(readBytes[0] != 0x05){
            return false;
        }

        //Read and discard authentication methods. Will use NO Authentication
        for(int i=0; i<Byte.toUnsignedInt(readBytes[1]); i++){
            clientSocket.getInputStream().read();
        }

        //SERVER RESPONSE: Send server's choice
        sendBytes = new byte[2];
        sendBytes[0] = 0x05; //Version 5
        sendBytes[1] = 0x00; //No authentication
        clientSocket.getOutputStream().write(sendBytes);


        //CLIENT REQUEST: Client Connection Request
        //0: Socks version
        //1: Command => Mockxy only allows 0x01 which is TCP/IP Stream
        //2: Reserved must be 0x00
        //3: Remote Address Type
        readBytes = new byte[4];
        clientSocket.getInputStream().read(readBytes);

        if(readBytes[1] != 0x01){
            return false;
        }

        addressType = readBytes[3];
        //Read address
        switch (addressType){
            case 0x01: //IPv4
                //4 bytes
                readBytes = new byte[4];
                clientSocket.getInputStream().read(readBytes);

                for (int i = 0; i < 4; ++i)
                {
                    int t = 0xFF & readBytes[i];
                    destAddr += "." + t;
                }
                destAddr = destAddr.substring(1);

                break;

            case 0x03: //DNS
                //1 byte for the length
                readBytes = new byte[1];
                clientSocket.getInputStream().read(readBytes);

                //N bytes
                readBytes = new byte[((int) readBytes[0])];
                clientSocket.getInputStream().read(readBytes);

                destAddr = new String(readBytes);
                break;

            case 0x04: //IPv6 NOT YET SUPPORTED
                return false;
        }

        destAddrBytes = readBytes;

        //Read Port in network byte order
        readBytes = new byte[2];
        clientSocket.getInputStream().read(readBytes);

        destPortBytes = readBytes;
        destPort = Byte.toUnsignedInt(readBytes[0]) << 8 | Byte.toUnsignedInt(readBytes[1]);


        //SERVER RESPONSE: Connection data
        sendBytes = new byte[4 + destAddrBytes.length +  destPortBytes.length];
        sendBytes[0] = 0x05; //Version 5
        sendBytes[1] = 0x00; //RQ Status. 0x00=Granted
        sendBytes[2] = 0x00; //Reserved. Must be 0x00
        sendBytes[3] = addressType; //Address type

        for(int i=0; i<destAddrBytes.length; i++){
            sendBytes[4+i] = destAddrBytes[i];
        }

        sendBytes[3+destAddrBytes.length+1]=destPortBytes[0];
        sendBytes[3+destAddrBytes.length+2]=destPortBytes[1];

        clientSocket.getOutputStream().write(sendBytes);

        this.ipAddress = destAddr;
        this.port = destPort;

        return true;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getPort() {
        return port;
    }

}



