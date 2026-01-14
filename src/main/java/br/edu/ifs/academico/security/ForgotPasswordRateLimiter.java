package br.edu.ifs.academico.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ForgotPasswordRateLimiter {

    private static final String UNKNOWN_EMAIL_KEY = "<unknown-email>";
    private static final String UNKNOWN_IP_KEY = "<unknown-ip>";

    private final FixedWindowLimiter ipLimiter;
    private final FixedWindowLimiter emailLimiter;

    public ForgotPasswordRateLimiter(
            @Value("${security.forgot-password.rate-limit.ip.limit:20}") int ipLimit,
            @Value("${security.forgot-password.rate-limit.email.limit:8}") int emailLimit,
            @Value("${security.forgot-password.rate-limit.window:PT10M}") Duration window) {
        this.ipLimiter = new FixedWindowLimiter(ipLimit, window);
        this.emailLimiter = new FixedWindowLimiter(emailLimit, window);
    }

    public boolean allow(String clientIp, String email) {
        String ipKey = normalizeKey(clientIp, UNKNOWN_IP_KEY);
        String emailKey = normalizeEmail(email);

        // If either bucket is exhausted, deny.
        return ipLimiter.allow(ipKey) && emailLimiter.allow(emailKey);
    }

    public long retryAfterSeconds(String clientIp, String email) {
        String ipKey = normalizeKey(clientIp, UNKNOWN_IP_KEY);
        String emailKey = normalizeEmail(email);
        return Math.max(ipLimiter.retryAfterSeconds(ipKey), emailLimiter.retryAfterSeconds(emailKey));
    }

    private static String normalizeEmail(String email) {
        if (email == null)
            return UNKNOWN_EMAIL_KEY;
        String trimmed = email.trim().toLowerCase();
        return trimmed.isEmpty() ? UNKNOWN_EMAIL_KEY : trimmed;
    }

    private static String normalizeKey(String value, String fallback) {
        if (value == null)
            return fallback;
        String trimmed = value.trim();
        return trimmed.isEmpty() ? fallback : trimmed;
    }

    private static final class FixedWindowLimiter {
        private final int limit;
        private final long windowMillis;

        private final Map<String, Window> windows = new ConcurrentHashMap<>();
        private final AtomicLong calls = new AtomicLong(0);

        private FixedWindowLimiter(int limit, Duration window) {
            if (limit < 1)
                throw new IllegalArgumentException("limit must be >= 1");
            this.limit = limit;
            this.windowMillis = window.toMillis();
            if (this.windowMillis < 1)
                throw new IllegalArgumentException("window must be >= 1ms");
        }

        boolean allow(String key) {
            long now = System.currentTimeMillis();
            long windowStart = (now / windowMillis) * windowMillis;

            Window win = windows.compute(key, (k, existing) -> {
                if (existing == null || existing.windowStartMillis != windowStart) {
                    return new Window(windowStart, 1);
                }
                if (existing.count >= limit) {
                    return existing;
                }
                existing.count++;
                return existing;
            });

            cleanupIfNeeded(windowStart);
            return win.windowStartMillis == windowStart && win.count <= limit;
        }

        long retryAfterSeconds(String key) {
            long now = System.currentTimeMillis();
            long windowStart = (now / windowMillis) * windowMillis;
            Window win = windows.get(key);
            if (win == null || win.windowStartMillis != windowStart)
                return 0;
            if (win.count < limit)
                return 0;
            long windowEnd = windowStart + windowMillis;
            long remainingMillis = Math.max(0, windowEnd - now);
            return (long) Math.ceil(remainingMillis / 1000.0);
        }

        private void cleanupIfNeeded(long currentWindowStart) {
            long n = calls.incrementAndGet();
            if (n % 200 != 0)
                return;
            long minStart = currentWindowStart - windowMillis;
            windows.entrySet().removeIf(e -> e.getValue().windowStartMillis < minStart);
        }

        private static final class Window {
            final long windowStartMillis;
            volatile int count;

            private Window(long windowStartMillis, int count) {
                this.windowStartMillis = windowStartMillis;
                this.count = count;
            }
        }
    }
}
