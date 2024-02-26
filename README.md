# Multi-Chat Application in Java
This project demonstrates a simple multi-chat application in Java, consisting of a ChatServer, a ClientHandler, and a Client class. This example allows multiple clients to connect to a server, send messages, and receive messages from other connected clients.

## Prerequisites
- Java Development Kit (JDK) 8 or later

## Building and Running the Project
Compile the source files:
```javac ChatServer.java ClientHandler.java Client.java```

Start the server in one terminal:
```java ChatServer```

Start multiple client instances in separate terminals:
```java Client```

Repeat step 3 for additional clients.

## Project Structure
- ChatServer.java: The server class that handles multiple client connections and broadcasts messages to all connected clients.
- ClientHandler.java: The client handler class that manages each client connection and communicates with the server.
- Client.java: The client class that connects to the server, sends user input as messages, and displays received messages.

## How it Works
- The ChatServer class starts a server that listens for incoming client connections on port 1234.
- When a client connects, a new ClientHandler instance is created to manage the client's connection and communication.
- The ClientHandler class communicates with the server and sends/receives messages from the connected client.
- The Client class connects to the server, sends user input as messages, and displays received messages.

## Notes
This example is for educational purposes and should not be used in production without proper error handling, security measures, and additional features.
Make sure to close the client and server applications properly to release any occupied resources.
