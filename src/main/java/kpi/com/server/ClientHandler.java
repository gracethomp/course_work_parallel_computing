package kpi.com.server;

import kpi.com.index.IndexBuilder;
import kpi.com.index.InvertedIndex;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.*;

public class ClientHandler extends Thread {
    public static final String[] folders = {"src/main/resources/test.neg",
            "src/main/resources/test.pos", "src/main/resources/train/neg", "src/main/resources/train.pos",
            "src/main/resources/train.unsup"};
    private final Socket socket;

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    public static File[] readFiles(String folderPath) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Invalid folder path: " + folderPath);
            return null;
        }

        return folder.listFiles();
    }

    public static List<File> readAllFiles() {
        List<File> files = new ArrayList<>();
        for (String folder : folders) {
            files.addAll(Arrays.asList(Objects.requireNonNull(readFiles(folder))));
        }
        return files;
    }

    private int setThreads(DataInputStream inputStream) throws IOException {
        return inputStream.readInt();
    }

    private void divideData(int numberOfThreads, String word, DataOutputStream outputStream) throws InterruptedException, IOException {
        InvertedIndex parallelInvertedIndex = new InvertedIndex(readAllFiles());
        IndexBuilder[] indexBuilder = new IndexBuilder[numberOfThreads];
        long currentTime = System.nanoTime();
        int numRowsPerThread = parallelInvertedIndex.getFiles().size() / numberOfThreads;
        for (int i = 0; i < numberOfThreads; i++) {
            indexBuilder[i] = new IndexBuilder(parallelInvertedIndex.getFiles().toArray(new File[0]), i * numRowsPerThread,
                    (i == numberOfThreads - 1) ? parallelInvertedIndex.getFiles().size() : (i + 1) * numRowsPerThread);
            indexBuilder[i].start();
        }
        for (int i = 0; i < numberOfThreads; i++) {
            indexBuilder[i].join();
            Map<String, Set<String>> threadIndex = indexBuilder[i].getIndex();
            for (Map.Entry<String, Set<String>> entry : threadIndex.entrySet()) {
                String word1 = entry.getKey();
                Set<String> documents = entry.getValue();
                parallelInvertedIndex.getIndex().merge(word1, documents, (existingDocs, newDocs) -> {
                    existingDocs.addAll(newDocs);
                    return existingDocs;
                });
            }
        }
        long time = System.nanoTime() - currentTime;
        Set<String> result = parallelInvertedIndex.search(word);

        outputStream.writeUTF(result.toString() + ".\n Time for indexing:" +
                time + " Did you want to continue? (y/n)");
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
                outputStream.writeUTF("Please send number of threads you want to use");
                int numberOfThreads = setThreads(inputStream);
                divideData(numberOfThreads, word, outputStream);
                continueConfirmation = Objects.equals(inputStream.readUTF(), "y");
            }
            outputStream.close();
            inputStream.close();
        } catch (IOException | InterruptedException e) {
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
