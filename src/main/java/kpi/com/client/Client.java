package kpi.com.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Objects;
import java.util.Scanner;

public class Client {
    private final String HOST;
    private final int PORT;

    public Client(String host, int port) {
        this.HOST = host;
        this.PORT = port;
    }

    public void connect() {
        try (Socket socket = new Socket(HOST, PORT)) {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            boolean continueConfirmation = true;
            Scanner sc = new Scanner(System.in);

            while(continueConfirmation) {
                System.out.println(dataInputStream.readUTF());
                String word = sc.nextLine();
                sendWord(dataOutputStream, word);
                System.out.println(dataInputStream.readUTF());
                word = sc.nextLine();
                sendWord(dataOutputStream, word);
                continueConfirmation = Objects.equals(word, "y");
            }
            sc.close();
            dataInputStream.close();
            dataOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void sendWord(DataOutputStream outputStream, String word) throws IOException {
        outputStream.writeUTF(word);
    }

    public static void main(String[] args) throws InterruptedException {
        Client client = new Client("localhost", 6666);
        client.connect();
    }
}

