import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

class TokenBucket {
    private int maxTokens;
    private double refillRatePerMs;
    private double tokens;
    private long lastRefillTime;

    TokenBucket(int maxTokens, int refillPerHour) {
        this.maxTokens = maxTokens;
        this.tokens = maxTokens;
        this.refillRatePerMs = (double) refillPerHour / 3600000.0;
        this.lastRefillTime = System.currentTimeMillis();
    }

    synchronized boolean tryConsume() {
        refill();
        if (tokens >= 1) {
            tokens -= 1;
            return true;
        }
        return false;
    }

    synchronized long getRetryAfterMs() {
        refill();
        if (tokens >= 1) return 0;
        return (long) Math.ceil((1 - tokens) / refillRatePerMs);
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double added = (now - lastRefillTime) * refillRatePerMs;
        tokens = Math.min(maxTokens, tokens + added);
        lastRefillTime = now;
    }

    synchronized int getUsed() {
        return (int) (maxTokens - tokens);
    }

    int getLimit() {
        return maxTokens;
    }
}

public class Problem_6 {

    private HashMap<String, TokenBucket> buckets = new HashMap<>();
    private int maxRequestsPerHour = 1000;

    public boolean checkRateLimit(String clientId) {
        buckets.putIfAbsent(clientId, new TokenBucket(maxRequestsPerHour, maxRequestsPerHour));
        TokenBucket bucket = buckets.get(clientId);
        boolean allowed = bucket.tryConsume();
        if (allowed) {
            System.out.println("Allowed (" + (bucket.getLimit() - bucket.getUsed()) + " requests remaining)");
        } else {
            long retry = bucket.getRetryAfterMs() / 1000;
            System.out.println("Denied (0 requests remaining, retry after " + retry + "s)");
        }
        return allowed;
    }

    public void getRateLimitStatus(String clientId) {
        buckets.putIfAbsent(clientId, new TokenBucket(maxRequestsPerHour, maxRequestsPerHour));
        TokenBucket bucket = buckets.get(clientId);
        long now = System.currentTimeMillis();
        long reset = now + bucket.getRetryAfterMs();
        Map<String, Object> status = new HashMap<>();
        status.put("used", bucket.getUsed());
        status.put("limit", bucket.getLimit());
        status.put("reset", reset / 1000);
        System.out.println(status);
    }

    public static void main(String[] args) throws Exception {
        Problem_6 limiter = new Problem_6();

        limiter.checkRateLimit("abc123");
        limiter.checkRateLimit("abc123");
        limiter.getRateLimitStatus("abc123");

        for (int i = 0; i < 1000; i++) limiter.checkRateLimit("abc123");

        limiter.checkRateLimit("abc123");
        limiter.getRateLimitStatus("abc123");
    }
}