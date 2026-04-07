import java.util.*;

class PageEvent {
    String url;
    String userId;
    String source;

    PageEvent(String url, String userId, String source) {
        this.url = url;
        this.userId = userId;
        this.source = source;
    }
}

public class Problem_5 {

    private HashMap<String, Integer> pageViews = new HashMap<>();
    private HashMap<String, Set<String>> uniqueVisitors = new HashMap<>();
    private HashMap<String, Integer> sourceCounts = new HashMap<>();
    private int totalVisits = 0;

    public void processEvent(PageEvent event) {

        pageViews.put(event.url, pageViews.getOrDefault(event.url, 0) + 1);

        uniqueVisitors.putIfAbsent(event.url, new HashSet<>());
        uniqueVisitors.get(event.url).add(event.userId);

        sourceCounts.put(event.source, sourceCounts.getOrDefault(event.source, 0) + 1);

        totalVisits++;
    }

    public void getDashboard() {

        PriorityQueue<Map.Entry<String, Integer>> pq =
                new PriorityQueue<>((a, b) -> b.getValue() - a.getValue());

        pq.addAll(pageViews.entrySet());

        System.out.println("Top Pages:");

        int rank = 1;
        while (!pq.isEmpty() && rank <= 10) {
            Map.Entry<String, Integer> entry = pq.poll();
            String url = entry.getKey();
            int views = entry.getValue();
            int unique = uniqueVisitors.get(url).size();

            System.out.println(rank + ". " + url + " - " + views + " views (" + unique + " unique)");
            rank++;
        }

        System.out.println("\nTraffic Sources:");

        for (Map.Entry<String, Integer> entry : sourceCounts.entrySet()) {
            double percent = (entry.getValue() * 100.0) / totalVisits;
            System.out.println(entry.getKey() + ": " + percent + "%");
        }
    }

    public static void main(String[] args) throws Exception {

        Problem_5 analytics = new Problem_5();

        analytics.processEvent(new PageEvent("/article/breaking-news", "user_123", "google"));
        analytics.processEvent(new PageEvent("/article/breaking-news", "user_456", "facebook"));
        analytics.processEvent(new PageEvent("/sports/championship", "user_789", "direct"));
        analytics.processEvent(new PageEvent("/article/breaking-news", "user_123", "google"));
        analytics.processEvent(new PageEvent("/sports/championship", "user_111", "google"));
        analytics.processEvent(new PageEvent("/sports/championship", "user_222", "facebook"));

        while (true) {
            System.out.println("\n--- Dashboard Update ---");
            analytics.getDashboard();
            Thread.sleep(5000);
        }
    }
}