import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * The ClientHandler class handles communication between the server and a connected client.
 * It runs in its own thread, listening for messages from the client and broadcasting them to other clients.
 */
public class ClientHandler implements Runnable {

    /** The socket that connects to the client. */
    private final Socket socket;

    /** Reference to the chat server that manages this client. */
    private final ChatServer server;

    /** The output stream to send messages to the client. */
    private PrintWriter out;

    /** The username of the connected client. */
    private String username;

    /**
     * Constructs a new ClientHandler to manage a client connection.
     *
     * @param socket The socket connected to the client.
     * @param server The chat server that manages this client.
     */
    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

    /**
     * The main method that runs when the client's thread is started.
     * It listens for messages from the client and broadcasts them to other clients.
     */
    @Override
    public void run() {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            username = in.readLine();

            if (username == null) {
                out.println("Invalid username. Connection will be closed.");
                disconnect();
                return;
            }

            server.broadcastMessage(username + " has joined the chat.", this);

            String message;
            while ((message = in.readLine()) != null) {
                server.broadcastMessage(username + ": " + message, this);
            }
        } catch (IOException e) {
            System.err.println("Error handling client: " + e.getMessage());
        } finally {
            disconnect();
        }
    }

    /**
     * Sends a message to the client.
     *
     * @param message The message to send
     */
    public void sendMessage(String message) {
        if(out != null) {
            out.println(message);
        }
    }

    /**
     * Disconnects the client, closes the socket, and removes the client from the server.
     * Also broadcasts a message to notify other clients that this client has left.
     */
    private void disconnect() {
        try {
            if (out != null) {
                out.close();
            }
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            System.err.println("Error closing client socket: " + e.getMessage());
        } finally {
            server.removeClient(this);
            if (username != null) {
                server.broadcastMessage(username + " has left the chat.", this);
            }
        }
    }

    /**
     * Closes the client connection.
     * This method is called by the server when it needs to disconnect a client.
     */
    public void closeConnection() {
        disconnect();
    }
}
