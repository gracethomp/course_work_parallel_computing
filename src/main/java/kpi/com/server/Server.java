package kpi.com.server;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private ServerSocket serverSocket;

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(6666);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Server is running!");
        while (true) {
            try {
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept());
                System.out.println("A new client is connected...");
                clientHandler.start();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

