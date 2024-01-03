package kpi.com.index;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class IndexBuilder extends Thread {
    private final Map<String, Set<String>> index;
    private final File[] files;
    private final int startIndex;
    private final int endIndex;

    public IndexBuilder(File[] files, int startIndex, int endIndex) {
        this.index = new HashMap<>();
        this.files = files;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
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


    public Map<String, Set<String>> getIndex() {
        return index;
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