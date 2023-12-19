package kpi.com.server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ClientHandler extends Thread{
    private final Socket socket;
    private final ExecutorService executorService;
    public ClientHandler(Socket socket){
        this.socket = socket;
        this.executorService = Executors.newFixedThreadPool(3);
    }

    @Override
    public void run() {
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.writeUTF("Please send your matrix to calculate sum od elements");
            int[][] matrix = readMatrix(inputStream);

            outputStream.writeUTF("Data is received. Write 'start'");
            inputStream.readUTF();
            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
                try {
                    return findSum(matrix);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });

            outputStream.writeUTF("Calculation is started, please wait...");
            //int sum = findSum(matrix);
            while (true) {
                inputStream.readUTF();
                if(future.isDone()){
                    outputStream.writeUTF("Calculation was ended. The result is " + future.get());
                    break;
                } else {
                    outputStream.writeUTF("In progress");
                }
            }

            outputStream.close();
            inputStream.close();
        } catch (IOException | InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        } finally {
            try {
                socket.close();
                System.out.println("Connection closed.");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private int[][] readMatrix(DataInputStream in) throws IOException {
        int n = in.readInt();
        int m = in.readInt();
        int[][] matrix = new int[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                matrix[i][j] = in.readInt();
            }
        }
        return matrix;
    }

    private int findSum(int[][] matrix) throws InterruptedException {
        int sum = 0;
        return sum;
    }
}
