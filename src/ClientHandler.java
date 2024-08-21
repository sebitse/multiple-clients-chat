import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private final Socket socket;
    private final ChatServer server;
    private PrintWriter out;
    private String username;

    public ClientHandler(Socket socket, ChatServer server) {
        this.socket = socket;
        this.server = server;
    }

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

    public void sendMessage(String message) {
        if(out != null) {
            out.println(message);
        }
    }

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
    public void closeConnection() {
        disconnect();
    }
}
