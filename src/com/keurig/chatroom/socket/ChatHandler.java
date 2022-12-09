package com.keurig.chatroom.socket;

import java.net.Socket;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

// The ChatHandler class is responsible for handling communication between the
// ChatServer and a single client. When a client connects to the ChatServer,
// the ChatServer creates a new ChatHandler instance to manage the connection
// and facilitate communication between the server and the client.
//
// The ChatHandler class uses a SocketChannel object to communicate with the
// client and a Buffer to store incoming messages. It also maintains a reference
// to the ChatServer and uses this reference to broadcast messages to all
// other clients in the chat room.
//
// When the ChatHandler receives a message from the client, it checks the
// message to see if it is the client's name. If so, it sets the client's
// name and sends a message to all other clients in the chat room to let
// them know that the user has joined the chat room. If the message is not
// the client's name, it is assumed to be a regular chat message and is
// broadcast to all other clients in the chat room.
//
// In this way, the ChatHandler class provides the functionality for
// managing communication between a single client and the ChatServer, and
// for facilitating communication between the client and other clients in
// the chat room.

public class ChatHandler extends Thread {

    private static List<ChatHandler> clients = new ArrayList<>();

    private Socket clientSocket;
    private String clientName;
    private PrintWriter out;

    public ChatHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    public void addClient() {
        clients.add(this);

        // Added by human
        broadcast(clientName + " has connected the server");
    }

    public void removeClient() {
        clients.remove(this);

        // Added by human
        broadcast(clientName + " has quit the server");
    }

    public void broadcast(String message) {
        for (ChatHandler client : clients) {
            if (client != this) { // Only send the message to the other clients
                client.out.println(message);
                client.out.flush(); // Flush the output buffer to send the message immediately
            }
        }
    }

    @Override
    public void run() {
        try {
            // Create input and output streams for the client socket
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Send a welcome message to the client and ask them to enter their name
            out.println("Welcome to the chat room! Please enter your name:");

            // Read the client's name from the input stream
            clientName = in.readLine();

            // Add the client to the list of connected clients
            addClient();

            // Read messages from the client and broadcast them to all connected clients
            String input;
            while ((input = in.readLine()) != null) {
                broadcast(clientName + ": " + input);
            }

            // When the client disconnects, remove them from the list of connected clients
            removeClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
