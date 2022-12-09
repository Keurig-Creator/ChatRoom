package com.keurig.chatroom.socket;

import java.io.*;
import java.net.Socket;
import java.util.Scanner;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

// The Client class is responsible for implementing the functionality for the
// chat room client. It provides methods for connecting to the ChatServer,
// sending messages to the ChatServer and other clients in the chat room,
// and receiving messages from the ChatServer and other clients.
//
// The Client class uses a SocketChannel object to communicate with the
// ChatServer, and a Buffer to store incoming messages. It also uses a
// Scanner object to read user input from the command line, allowing the
// user to enter their name and send chat messages to other clients.
//
// When the Client connects to the ChatServer, it sends a message to the
// server containing the user's name. The ChatServer then broadcasts a
// message to all other clients in the chat room to let them know that the
// user has joined. The Client can then use the Scanner object to read user
// input from the command line and send messages to the ChatServer, which
// will broadcast the messages to all other clients in the chat room.
//
// In this way, the Client class provides the functionality for a chat room
// client, allowing the user to connect to the ChatServer, send and receive
// messages, and participate in the chat room.

public class ChatClient extends Thread {

    private Socket clientSocket;
    private String hostname;
    private int port;
    private String clientName;
    private BufferedReader in;
    private PrintWriter out;
    private Selector selector;
    private SocketChannel socketChannel;

    public ChatClient(String hostname, int port) {
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public void run() {
        try {
            // Connect to the ChatServer
            clientSocket = new Socket(hostname, port);

            // Create input and output streams for the client socket
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            out = new PrintWriter(clientSocket.getOutputStream(), true);

            // Read the welcome message from the server and ask the user to enter their name
            System.out.println(in.readLine());
            System.out.print("Enter your name: ");

            // Read the user's name from the command line
            Scanner scanner = new Scanner(System.in);
            clientName = scanner.nextLine();

            // Send the user's name to the server
            out.println(clientName);

            // Create a new thread for reading messages from the server
            new Thread(() -> {
                try {
                    // Listen for incoming messages from the server
                    while (true) {
                        // Read the next message from the server
                        BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                        String message = reader.readLine();
                        if (message == null) {
                            // Exit the program
                            System.out.println("Connection to the server was closed.");
                            System.exit(0);
                        }

                        // Print the message to the console
                        System.out.println(message);
                    }
                } catch (EOFException e) {
                    // Exit the program
                    System.out.println("Connection to the server was closed.");
                    System.exit(0);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();

            // Listen for user input and send messages to the server
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            PrintWriter writer = new PrintWriter(clientSocket.getOutputStream(), true);
            String input;
            while ((input = reader.readLine()) != null) {
                // Send the message to the server
                writer.println(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}