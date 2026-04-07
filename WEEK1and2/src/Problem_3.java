import java.util.*;

class DNSEntry {
    String domain;
    String ipAddress;
    long expiryTime;

    DNSEntry(String domain, String ipAddress, long ttl) {
        this.domain = domain;
        this.ipAddress = ipAddress;
        this.expiryTime = System.currentTimeMillis() + ttl * 1000;
    }

    boolean isExpired() {
        return System.currentTimeMillis() > expiryTime;
    }
}

class DNSCache {

    private int capacity;
    private LinkedHashMap<String, DNSEntry> cache;
    private int hits = 0;
    private int misses = 0;
    private long totalLookupTime = 0;
    private int totalRequests = 0;

    DNSCache(int capacity) {
        this.capacity = capacity;
        cache = new LinkedHashMap<>(capacity, 0.75f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, DNSEntry> eldest) {
                return size() > DNSCache.this.capacity;
            }
        };
    }

    public synchronized String resolve(String domain) {

        long start = System.nanoTime();

        if (cache.containsKey(domain)) {
            DNSEntry entry = cache.get(domain);
            if (!entry.isExpired()) {
                hits++;
                totalRequests++;
                totalLookupTime += (System.nanoTime() - start);
                return "Cache HIT → " + entry.ipAddress;
            } else {
                cache.remove(domain);
            }
        }

        misses++;
        String ip = queryUpstreamDNS(domain);
        DNSEntry newEntry = new DNSEntry(domain, ip, 300);
        cache.put(domain, newEntry);

        totalRequests++;
        totalLookupTime += (System.nanoTime() - start);

        return "Cache MISS → Query upstream → " + ip + " (TTL: 300s)";
    }

    private String queryUpstreamDNS(String domain) {
        Random r = new Random();
        return "172.217.14." + (200 + r.nextInt(50));
    }

    public void getCacheStats() {
        double hitRate = (totalRequests == 0) ? 0 : ((double) hits / totalRequests) * 100;
        double avgTime = (totalRequests == 0) ? 0 : (totalLookupTime / totalRequests) / 1_000_000.0;

        System.out.println("Hit Rate: " + hitRate + "%");
        System.out.println("Avg Lookup Time: " + avgTime + " ms");
    }

    public static void main(String[] args) throws Exception {

        DNSCache cache = new DNSCache(5);

        System.out.println(cache.resolve("google.com"));
        System.out.println(cache.resolve("google.com"));

        Thread.sleep(1000);

        System.out.println(cache.resolve("openai.com"));
        System.out.println(cache.resolve("google.com"));

        cache.getCacheStats();
    }
}