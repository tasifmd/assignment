// Server.java
import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Server {
    public static void main(String[] args) {
        int port = 12345; // Default port for the server

        // Parse command-line arguments to allow specifying the port
        if (args.length > 0) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.err.println("Invalid port number. Using default port 12345.");
            }
        }

        // Create a thread pool to manage multiple client connections concurrently
        ExecutorService threadPool = Executors.newCachedThreadPool();

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port: " + port);

            // Infinite loop to accept client connections
            while (true) {
                try {
                    // Accept a new client connection
                    Socket clientSocket = serverSocket.accept();
                    System.out.println("New client connected: " + clientSocket.getInetAddress());

                    // Assign the client connection to a handler and run it in a separate thread
                    threadPool.execute(new ClientHandler(clientSocket));
                } catch (IOException e) {
                    System.err.println("Error accepting client connection: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            // Handle errors that occur while creating or using the ServerSocket
            System.err.println("Server error: " + e.getMessage());
        } finally {
            // Shutdown the thread pool when the server stops
            threadPool.shutdown();
        }
    }
}

// This class handles communication with a single client
class ClientHandler implements Runnable {
    private final Socket clientSocket;

    // Constructor to initialize the client socket
    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
            // Input stream to read data from the client
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            // Output stream to send data to the client
            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String message;

            // Continuously read messages from the client
            while ((message = in.readLine()) != null) {
                // Respond to "ping" messages with "pong"
                if ("ping".equalsIgnoreCase(message.trim())) {
                    System.out.println("Received 'ping' from client. Sending 'pong'.");
                    out.println("pong");
                } else {
                    // Log unknown messages for debugging purposes
                    System.out.println("Unknown message: " + message);
                }
            }
        } catch (IOException e) {
            // Handle communication errors with the client
            System.err.println("Communication error with client: " + e.getMessage());
        } finally {
            try {
                // Close the client socket when done
                clientSocket.close();
            } catch (IOException e) {
                System.err.println("Error closing client socket: " + e.getMessage());
            }
            System.out.println("Client disconnected.");
        }
    }
}