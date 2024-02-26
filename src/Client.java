import java.io.BufferedReader;
import java.io.*;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {

    private static Socket socket;
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;
    private static BufferedReader in;
    private static PrintWriter out;

    public static void main(String[] args) {
        (new Client()).start();
    }

    private Client() {
        try {
            socket = new Socket(SERVER_ADDRESS, SERVER_PORT);

            // Set up input and output streams
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void start() {
        try {
            // Request user input in a separate thread
            Thread keyboardInputThread = new Thread(this::handleKeyboardInput); // method reference
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
            try {
                if (socket != null) {
                    socket.close();
                }
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
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