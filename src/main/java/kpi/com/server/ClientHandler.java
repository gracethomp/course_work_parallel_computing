package kpi.com.server;

import kpi.com.index.InvertedIndex;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.List;
import java.util.Objects;

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

            boolean continueConfirmation = true;

            while (continueConfirmation) {
                outputStream.writeUTF("Please send word you want to find");
                String word = inputStream.readUTF();
                System.out.println(word);
                invertedIndex.addDocument(1, "Java is a programming language");
                invertedIndex.addDocument(2, "Python is also a programming language");
                invertedIndex.addDocument(3, "Java and Python are popular languages");
                List<Integer> result = invertedIndex.search(word);
                outputStream.writeUTF(result.toString() + ". Did you want to continue? (y/n)");
                continueConfirmation = Objects.equals(inputStream.readUTF(), "y");
            }

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
