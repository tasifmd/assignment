// Client.java
import java.io.*;
import java.net.*;

public class Client {
    public static void main(String[] args) {
        // Ensure the client receives the server address and port as arguments
        if (args.length < 2) {
            System.err.println("Usage: java Client <server_address> <port>");
            return;
        }

        String serverAddress = args[0]; // Server address (IP or hostname)
        int port;

        // Parse the port number from the arguments
        try {
            port = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.err.println("Invalid port number.");
            return;
        }

        try (
            // Create a socket to connect to the server
            Socket socket = new Socket(serverAddress, port);
            // Input stream to receive data from the server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Output stream to send data to the server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

            System.out.println("Connected to server: " + serverAddress + " on port " + port);

            // Thread to send "ping" messages every second
            Thread senderThread = new Thread(() -> {
                try {
                    while (true) {
                        out.println("ping"); // Send a "ping" message
                        System.out.println("Sent: ping");
                        Thread.sleep(1000); // Wait for 1 second before sending the next message
                    }
                } catch (InterruptedException e) {
                    // Handle interruptions in the sender thread
                    System.err.println("Sender thread interrupted: " + e.getMessage());
                }
            });

            senderThread.start(); // Start the sender thread

            String response;
            // Continuously read and print responses from the server
            while ((response = in.readLine()) != null) {
                System.out.println("Received: " + response);
            }

        } catch (IOException e) {
            // Handle errors during connection or communication with the server
            System.err.println("Client error: " + e.getMessage());
        }
    }
}
