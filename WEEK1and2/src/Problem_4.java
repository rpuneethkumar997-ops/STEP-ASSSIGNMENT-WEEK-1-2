import java.util.*;

public class Problem_4 {

    private int n = 5;
    private HashMap<String, Set<String>> ngramIndex = new HashMap<>();
    private HashMap<String, List<String>> documentNgrams = new HashMap<>();

    public void addDocument(String docId, String text) {
        List<String> ngrams = generateNgrams(text);
        documentNgrams.put(docId, ngrams);

        for (String gram : ngrams) {
            ngramIndex.putIfAbsent(gram, new HashSet<>());
            ngramIndex.get(gram).add(docId);
        }
    }

    private List<String> generateNgrams(String text) {
        List<String> result = new ArrayList<>();
        String[] words = text.toLowerCase().split("\\s+");

        for (int i = 0; i <= words.length - n; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < n; j++) {
                sb.append(words[i + j]).append(" ");
            }
            result.add(sb.toString().trim());
        }

        return result;
    }

    public void analyzeDocument(String docId) {
        List<String> grams = documentNgrams.get(docId);
        HashMap<String, Integer> matchCount = new HashMap<>();

        for (String gram : grams) {
            Set<String> docs = ngramIndex.getOrDefault(gram, new HashSet<>());
            for (String otherDoc : docs) {
                if (!otherDoc.equals(docId)) {
                    matchCount.put(otherDoc, matchCount.getOrDefault(otherDoc, 0) + 1);
                }
            }
        }

        System.out.println("Extracted " + grams.size() + " n-grams");

        for (Map.Entry<String, Integer> entry : matchCount.entrySet()) {
            String otherDoc = entry.getKey();
            int matches = entry.getValue();
            double similarity = (matches * 100.0) / grams.size();

            System.out.println("Found " + matches + " matching n-grams with \"" + otherDoc + "\"");
            System.out.println("Similarity: " + similarity + "%");
        }
    }

    public static void main(String[] args) {

        Problem_4 detector = new Problem_4();

        String essay1 = "data structures and algorithms are important for computer science students";
        String essay2 = "data structures and algorithms are essential for computer science education";
        String essay3 = "machine learning and artificial intelligence are modern technologies";

        detector.addDocument("essay_089.txt", essay1);
        detector.addDocument("essay_092.txt", essay2);
        detector.addDocument("essay_123.txt", essay1 + " data structures and algorithms are important");

        detector.analyzeDocument("essay_123.txt");
    }
}