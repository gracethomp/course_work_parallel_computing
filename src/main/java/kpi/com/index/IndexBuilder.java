package kpi.com.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class IndexBuilder extends Thread {
    private static final String[] folders = {"src/main/resources/test.neg",
            "src/main/resources/test.pos", "src/main/resources/train/neg", "src/main/resources/train.pos",
            "src/main/resources/train.unsup"};
    private final Map<String, Set<String>> index;
    private final File[] files;
    private int startIndex;
    private int endIndex;

    public IndexBuilder(File[] files) {
        this.index = new HashMap<>();
        this.files = files;
    }

    public IndexBuilder(File[] files, int startIndex, int endIndex) {
        this.index = new HashMap<>();
        this.files = files;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
    }

    public static File[] readFiles(String folderPath) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Invalid folder path: " + folderPath);
            return null;
        }
        return folder.listFiles();
    }

    public void buildIndex() throws IOException {
        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    indexFile(file);
                }
            }
        }
    }

    public void buildIndex(int startIndex, int endIndex) throws IOException {
        if (files != null) {
            for (int i = startIndex; i < endIndex; i++){
                if (files[i].isFile()) {
                    indexFile(files[i]);
                }
            }
        }
    }

    private void indexFile(File file) throws IOException {
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("[^a-zA-Z0-9']+");
                for (String word : words) {
                    word = word.toLowerCase();
                    index.computeIfAbsent(word, k -> new HashSet<>()).add(file.getName());
                }
            }
        }
    }

    public Set<String> search(String word) {
        return index.getOrDefault(word.toLowerCase(), new HashSet<>());
    }

    public Map<String, Set<String>> getIndex() {
        return index;
    }

    public static void main(String[] args) {
        IndexBuilder invertedIndex = new IndexBuilder(IndexBuilder.readFiles(folders[0]));
        long currentTime = System.nanoTime();
        try {
            invertedIndex.buildIndex();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        System.out.println(System.nanoTime() - currentTime);

        String searchWord = "text";
        Set<String> searchResult = invertedIndex.search(searchWord);

        if (searchResult.isEmpty()) {
            System.out.println("Word '" + searchWord + "' not found in any files.");
        } else {
            System.out.println("Word '" + searchWord + "' found in files: " + searchResult);
        }
    }

    @Override
    public void run() {
        try {
            this.buildIndex(startIndex, endIndex);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}