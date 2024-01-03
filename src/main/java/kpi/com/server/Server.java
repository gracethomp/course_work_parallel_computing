package kpi.com.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private ServerSocket serverSocket;
    private ExecutorService threadPool;

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(6666);
            threadPool = Executors.newFixedThreadPool(5);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Server is running!");
        while (true) {
            try {
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept());
                System.out.println("A new client is connected...");
                threadPool.execute(clientHandler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

