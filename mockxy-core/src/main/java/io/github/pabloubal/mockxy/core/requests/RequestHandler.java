package io.github.pabloubal.mockxy.core.requests;

import io.github.pabloubal.mockxy.core.ChainHandler;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class RequestHandler implements Runnable{
    private int proxyType;
    private Boolean running;
    private ServerSocket serverSocket;
    private Integer port;
    private ChainHandler chainHandler;

    private static ArrayList<Thread> servicingThreads;

    public RequestHandler(int proxyType, Integer socksPort, ChainHandler chainHandler){
        this.proxyType = proxyType;
        this.port =socksPort;
        this.chainHandler = chainHandler;

        servicingThreads = new ArrayList<>();
    }

    @Override
    public void run() {
        try {
            // Create the Server Socket for the SOCKS 5 Proxy
            serverSocket = new ServerSocket(port);

            System.out.println("Proxy Started on Port " + serverSocket.getLocalPort());

            running = true;
        }

        // Catch exceptions associated with opening socket
        catch (SocketException se) {
            System.out.println("Socket Exception when connecting to client");
            se.printStackTrace();
        }
        catch (SocketTimeoutException ste) {
            System.out.println("Timeout occured while connecting to client");
        }
        catch (IOException io) {
            System.out.println("IO exception when connecting to client");
        }

        while(running){
            try {
                // serverSocket.accept() Blocks until a connection is made
                Socket socket = serverSocket.accept();
                Thread thread;

                switch (proxyType){
                    case Proxy.TYPE_HTTP:
                        // Create new Thread for HTTPHandler
                        thread = new Thread(new HTTPHandler(socket, chainHandler));
                        break;

                    case Proxy.TYPE_SOCKS:
                    default:
                        // Create new Thread for SOCKSHandler
                        thread = new Thread(new SOCKSHandler(socket, chainHandler));
                        break;
                }

                servicingThreads.add(thread);
                thread.start();

            } catch (SocketException e) {
                System.out.println("Server closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void stop(){
        this.running = false;
    }


    public Boolean getRunning() {
        return running;
    }

    public void setRunning(Boolean running) {
        this.running = running;
    }
}
