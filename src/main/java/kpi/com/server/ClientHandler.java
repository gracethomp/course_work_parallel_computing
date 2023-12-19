package kpi.com.server;

import kpi.com.index.InvertedIndex;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;

public class ClientHandler extends Thread {
    private final Socket socket;
    private final InvertedIndex invertedIndex = new InvertedIndex();

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream inputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream outputStream = new DataOutputStream(socket.getOutputStream());

            outputStream.writeUTF("Please send word you want to find");
            String word = inputStream.readUTF();
            System.out.println(word);
            invertedIndex.addDocument(1, "Java is a programming language");
            invertedIndex.addDocument(2, "Python is also a programming language");
            invertedIndex.addDocument(3, "Java and Python are popular languages");
            List<Integer> result = invertedIndex.search(word);
            outputStream.writeUTF(result.toString());
//            int[][] matrix = readMatrix(inputStream);
//
//            outputStream.writeUTF("Data is received. Write 'start'");
//            inputStream.readUTF();
//            CompletableFuture<Integer> future = CompletableFuture.supplyAsync(() -> {
//                try {
//                    return findSum(matrix);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                }
//            });
//
//            outputStream.writeUTF("Calculation is started, please wait...");
//            //int sum = findSum(matrix);
//            while (true) {
//                inputStream.readUTF();
//                if(future.isDone()){
//                    outputStream.writeUTF("Calculation was ended. The result is " + future.get());
//                    break;
//                } else {
//                    outputStream.writeUTF("In progress");
//                }
//            }

            outputStream.close();
            inputStream.close();
        } catch (IOException e) {
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
}
