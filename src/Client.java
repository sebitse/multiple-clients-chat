import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * The Client class connects to a chat server, sends user input to the server, and displays messages from the server.
 * It runs the client-side logic of a simple chat application.
 */
public class Client {

    /** The socket used to connect to the server. */
    private static Socket socket;

    /** The address of the chat server. */
    private static final String SERVER_ADDRESS = "localhost";

    /** The port number on which the chat server is listening. */
    private static final int SERVER_PORT = 1234;

    /** The input stream to read messages from the server. */
    private static BufferedReader in;

    /** The output stream to send messages to the server. */
    private static PrintWriter out;

    /**
     * The main method that starts the client.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        (new Client()).start();
    }

    /**
     * Constructs a Client object and connects to the chat server.
     * Initializes input and output streams for communication.
     *
     * @throws RuntimeException if an I/O error occurs when creating the socket or streams.
     */
    private Client() {
        try {
            // Create a socket to connect to the server
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

            // Set up input and output streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            // If there's an error, wrap it in a RuntimeException
            throw new RuntimeException(e);
        }
    }

    /**
     * Starts the client, handling user input and displaying server messages.
     * The user's input is handled in a separate thread, while server messages are processed in the main thread.
     */
    public void start() {
        try {
            // Request user input in a separate thread
            Thread keyboardInputThread = new Thread(this::handleKeyboardInput);
            keyboardInputThread.start();

            // Handle server messages in the main thread
            String serverMessage;
            while ((serverMessage = in.readLine()) != null) {
                System.out.println(serverMessage);
            }
        } catch (UnknownHostException e) {
            System.err.println("Error: Unknown host: " + SERVER_ADDRESS);
        } catch (IOException e) {
            System.err.println("Error connecting to server: " + e.getMessage());
        } finally {
            // Ensure the socket is closed when done
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }

    /**
     * Handles user input from the keyboard and sends it to the server.
     * This method runs in a separate thread to allow simultaneous input and output processing.
     */
    private void handleKeyboardInput() {
        try {
            BufferedReader keyboardInput = new BufferedReader(new InputStreamReader(System.in));
            System.out.println("Connected to the chat server at " + SERVER_ADDRESS + ":" + SERVER_PORT);
            System.out.println("Enter your username:");
            String username = keyboardInput.readLine();
            out.println(username);

            String userInput;
            while ((userInput = keyboardInput.readLine()) != null) {
                out.println(userInput);
            }
        } catch (IOException e) {
            System.err.println("Error reading from keyboard: " + e.getMessage());
        }
    }
}
