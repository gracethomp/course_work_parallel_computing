package kpi.com.client;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Random;

public class Client {
    private final String HOST;
    private final int PORT;

    public Client(String host, int port){
        this.HOST = host;
        this.PORT = port;
    }

    public void sendMatrix(int n, int m) {
        int[][] matrix = generateMatrix(n, m);
        try(Socket socket = new Socket(HOST, PORT)) {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            System.out.println(dataInputStream.readUTF());
            writeMatrix(dataOutputStream, matrix);

            System.out.println(dataInputStream.readUTF());
            dataOutputStream.writeUTF("start");
            System.out.println(dataInputStream.readUTF());
            while (true) {
                dataOutputStream.writeUTF("get");
                String response = dataInputStream.readUTF();
                System.out.println(response);
                if (!response.equalsIgnoreCase("In progress"))
                    break;
                Thread.sleep(1000);
            }

            dataInputStream.close();
            dataOutputStream.close();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static int[][] generateMatrix(int n, int m){
        Random random = new Random();
        int[][] matrix = new int[n][m];
        for (int i = 0; i < matrix.length; i++) {
            for (int j = 0; j < matrix[i].length; j++){
                matrix[i][j] = random.nextInt(10001);
            }
        }
        return matrix;
    }

    private static void writeMatrix(DataOutputStream out, int[][] matrix) throws IOException {
        out.writeInt(matrix.length);
        out.writeInt(matrix[0].length);
        for (int[] ints : matrix) {
            for (int anInt : ints) {
                out.writeInt(anInt);
            }
        }
    }
}

