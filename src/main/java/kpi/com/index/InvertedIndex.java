package kpi.com.index;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InvertedIndex {
    private final Map<String, List<Integer>> index;

    public InvertedIndex() {
        this.index = new HashMap<>();
    }

    // Добавить документ в индекс
    public void addDocument(int documentId, String content) {
        String[] words = content.split("\\s+");

        for (String word : words) {
            word = word.toLowerCase();
            if (!index.containsKey(word)) {
                index.put(word, new ArrayList<>());
            }

            List<Integer> docList = index.get(word);
            if (!docList.contains(documentId)) {
                docList.add(documentId);
            }
        }
    }

    // Поиск документов по слову
    public List<Integer> search(String query) {
        query = query.toLowerCase();
        return index.getOrDefault(query, new ArrayList<>());
    }

    public static void main(String[] args) {
        InvertedIndex invertedIndex = new InvertedIndex();

        // Добавление документов в индекс
        invertedIndex.addDocument(1, "Java is a programming language");
        invertedIndex.addDocument(2, "Python is also a programming language");
        invertedIndex.addDocument(3, "Java and Python are popular languages");

        // Поиск документов по слову
        List<Integer> result = invertedIndex.search("is");

        // Вывод результатов
        System.out.println("Documents containing the word 'java': " + result);
    }
}
