import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

public class ChatServer {

    private static ServerSocket server = null;
    private static final int PORT = 1234;
    private Set<ClientHandler> clientHandlers = new HashSet<>();

    public static void main(String[] args) {
        (new ChatServer()).start();
    }

    private ChatServer() {
        try {
            server = new ServerSocket(PORT);
            System.out.println("Chat server started on port " + PORT);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void start() {
        try {
            while (true) {
                Socket socket = server.accept();
                ClientHandler clientHandler = new ClientHandler(socket, this);
                clientHandlers.add(clientHandler);
                new Thread(clientHandler).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }

    public synchronized void broadcastMessage(String message, ClientHandler sender) {
        for (ClientHandler client : clientHandlers) {
            if (client != sender) {
                client.sendMessage(message);
            }
        }
    }

    public synchronized void removeClient(ClientHandler clientHandler) {
        if(!clientHandlers.isEmpty())
            clientHandlers.remove(clientHandler);
    }
}