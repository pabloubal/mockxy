package io.github.pabloubal.mockxy.core.requests;

import io.github.pabloubal.mockxy.core.ChainHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

@ConfigurationProperties
public class Proxy{
    @Autowired
    ChainHandler chainHandler;

    @Value("${proxy.port}")
    private Integer port;


    @PostConstruct
    public void init(){
        this.listen();
    }


    private ServerSocket serverSocket;

    /**
     * Semaphore for Proxy and Consolee Management System.
     */
    private volatile boolean running = true;


    /**
     * ArrayList of threads that are currently running and servicing requests.
     * This list is required in order to join all threads on closing of server
     */
    static ArrayList<Thread> servicingThreads;



    /**
     * Create the Proxy Server
     * @param port Port number to run proxy server from.
     */
    public Proxy() {
        // Create array list to hold servicing threads
        servicingThreads = new ArrayList<>();
    }


    /**
     * Listens to port and accepts new socket connections.
     * Creates a new thread to handle the request and passes it the socket connection and continues listening.
     */
    public void listen(){
        try {
            // Create the Server Socket for the Proxy
            serverSocket = new ServerSocket(port);

            // Set the timeout
            //serverSocket.setSoTimeout(100000);	// debug
            System.out.println("Waiting for clients on port " + serverSocket.getLocalPort() + "src/test");
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
                // serverSocket.accpet() Blocks until a connection is made
                Socket socket = serverSocket.accept();

                // Create new Thread and pass it Runnable RequestHandler
                Thread thread = new Thread(new RequestHandler(socket, chainHandler));

                // Key a reference to each thread so they can be joined later if necessary
                servicingThreads.add(thread);

                thread.start();
            } catch (SocketException e) {
                // Socket exception is triggered by management system to shut down the proxy
                System.out.println("Server closed");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }
}