package kpi.com.index;

import java.io.File;
import java.util.*;

public class ParallelInvertedIndex {
    public static final int NUMBER_OF_THREADS = 10;
    private final Map<String, Set<String>> index;
    private final List<File> files = new ArrayList<>();

    public Map<String, Set<String>> getIndex() {
        return index;
    }

    public List<File> getFiles() {
        return files;
    }

    public ParallelInvertedIndex(List<File> files) {
        this.index = new HashMap<>();
        this.files.addAll(files);
    }

    public Set<String> search(String word) {
        return index.getOrDefault(word.toLowerCase(), new HashSet<>());
    }
}
