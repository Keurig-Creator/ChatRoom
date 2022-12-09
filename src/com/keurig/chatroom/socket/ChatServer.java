package com.keurig.chatroom.socket;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;

// The ChatServer class implements a simple chat room server that can accept
// connections from multiple clients and facilitate communication between them.
// The server uses the Java NIO library to manage multiple client connections
// concurrently, and uses a Selector object to monitor the state of the client
// sockets and a Buffer to store incoming messages. The ChatServer class
// provides the infrastructure for a simple chat room where multiple clients
// can connect and communicate with each other in real time.

public class ChatServer extends Thread implements Runnable {

    private ServerSocket serverSocket;

    public ChatServer(int port) throws IOException {
        // Create a new ServerSocket bound to the specified port
        serverSocket = new ServerSocket(port);
    }

    @Override
    public void run() {
        try {

            // Added by human for debug
            System.out.println("Server is connected");
            while (true) {
                // Accept incoming connections and create a new ChatHandler thread to handle communication with the client
                Socket clientSocket = serverSocket.accept();
                ChatHandler handler = new ChatHandler(clientSocket);
                handler.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
