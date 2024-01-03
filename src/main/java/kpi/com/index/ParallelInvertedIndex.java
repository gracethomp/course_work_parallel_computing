package kpi.com.index;

import java.io.File;
import java.util.*;

public class ParallelInvertedIndex {
    private static final String[] folders = {"src/main/resources/test.neg",
            "src/main/resources/test.pos", "src/main/resources/train/neg", "src/main/resources/train.pos",
            "src/main/resources/train.unsup"};
    public static final int NUMBER_OF_THREADS = 10;
    private final Map<String, Set<String>> index;
    private static final List<File> files = new ArrayList<>();

    public Map<String, Set<String>> getIndex() {
        return index;
    }

    public ParallelInvertedIndex() {
        this.index = new HashMap<>();
    }

    public static File[] readFiles(String folderPath) {
        File folder = new File(folderPath);

        if (!folder.exists() || !folder.isDirectory()) {
            System.err.println("Invalid folder path: " + folderPath);
            return null;
        }

        return folder.listFiles();
    }

    public Set<String> search(String word) {
        return index.getOrDefault(word.toLowerCase(), new HashSet<>());
    }

    public static void main(String[] args) throws InterruptedException {
        ParallelInvertedIndex parallelInvertedIndex = new ParallelInvertedIndex();
        for (String folder : folders) {
            files.addAll(Arrays.asList(Objects.requireNonNull(readFiles(folder))));
        }
        IndexBuilder[] indexBuilder = new IndexBuilder[ParallelInvertedIndex.NUMBER_OF_THREADS];
        int numRowsPerThread = files.size() / ParallelInvertedIndex.NUMBER_OF_THREADS;
        for (int i = 0; i < ParallelInvertedIndex.NUMBER_OF_THREADS; i++) {
            indexBuilder[i] = new IndexBuilder(files.toArray(new File[0]), i * numRowsPerThread,
                    (i == ParallelInvertedIndex.NUMBER_OF_THREADS - 1) ? files.size() : (i + 1) * numRowsPerThread);
            indexBuilder[i].start();
        }
        for (int i = 0; i < ParallelInvertedIndex.NUMBER_OF_THREADS; i++) {
            indexBuilder[i].join();

            Map<String, Set<String>> threadIndex = indexBuilder[i].getIndex();
            for (Map.Entry<String, Set<String>> entry : threadIndex.entrySet()) {
                String word = entry.getKey();
                Set<String> documents = entry.getValue();
                parallelInvertedIndex.index.merge(word, documents, (existingDocs, newDocs) -> {
                    existingDocs.addAll(newDocs);
                    return existingDocs;
                });
            }
        }
        System.out.println(parallelInvertedIndex.getIndex());
    }
}
