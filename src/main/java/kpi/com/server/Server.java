package kpi.com.server;

import kpi.com.threadPool.ThreadPool;

import java.io.IOException;
import java.net.ServerSocket;

public class Server {
    private ServerSocket serverSocket;
    private ThreadPool threadPool;

    public static void main(String[] args) {
        Server server = new Server();
        server.startServer();
    }

    public void startServer() {
        try {
            serverSocket = new ServerSocket(6666);
            threadPool = new ThreadPool(5);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println("Server is running!");
        acceptClients();
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void acceptClients()  {
        while (true) {
            try {
                ClientHandler clientHandler = new ClientHandler(serverSocket.accept());
                System.out.println("A new client is connected...");
                threadPool.submit(clientHandler);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}

