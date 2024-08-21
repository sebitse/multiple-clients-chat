import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * ChatServer is a multi-threaded chat server that listens for client connections on port 1234.
 * It allows clients to connect, send messages, and broadcast messages to all other connected clients.
 */
public class ChatServer {

    /** The server socket that listens for connections. */
    private static ServerSocket server = null;

    /** The port number */
    private static final int PORT = 1234;

    /** A flag to control the running state. */
    private volatile boolean running = true;

    /** A set to keep track of all connected clients. */
    private Set<ClientHandler> clientHandlers = new HashSet<>();

    /**
     * The main method that starts the chat server.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        (new ChatServer()).start();
    }

    /**
     * Constructs a ChatServer and initializes the server socket.
     *
     * @throws RuntimeException if the server socket cannot be created.
     */
    private ChatServer() {
        try {
            server = new ServerSocket(PORT);
            System.out.println("Chat server started on port " + PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts the server and begins accepting client connections.
     * Each client connection is handled in a separate thread.
     */
    public void start() {
        try {
            while (running) {
                Socket socket = server.accept();
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            if (running) { // Only log if the server is running
                System.err.println("Error starting the server: " + e.getMessage());
            }
        } finally {
            stop();
        }
    }

    /**
     * Stops the server, closes the server socket, and disconnects all connected clients.
     */
    public void stop() {
        running = false;
        try {
            server.close();
            synchronized (clientHandlers) {
                for (ClientHandler clientHandler : clientHandlers) {
                    clientHandler.closeConnection();
                }
            }
        } catch (IOException e) {
            System.err.println("Error closing the server: " + e.getMessage());
        }
    }

    /**
     * Broadcasts a message to all connected clients except the sender.
     *
     * @param message The message to broadcast.
     * @param sender The client sending the message.
     */
    public synchronized void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    /**
     * Removes a client from the set of connected clients.
     *
     * @param clientHandler The client handler to remove.
     */
    public synchronized void removeClient(ClientHandler clientHandler) {
        if(!clientHandlers.isEmpty())
            clientHandlers.remove(clientHandler);
    }
}
