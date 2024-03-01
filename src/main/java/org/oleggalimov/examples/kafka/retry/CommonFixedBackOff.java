package org.oleggalimov.examples.kafka.retry;

import org.springframework.stereotype.Component;
import org.springframework.util.backoff.FixedBackOff;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Component
public class CommonFixedBackOff extends FixedBackOff {
    public static final long DEFAULT_RETRY_INTERVAL_MS = Duration.of(15, ChronoUnit.SECONDS).toMillis();
    public static final long MAX_RETRY_ATTEMPTS = 3L;

    public CommonFixedBackOff() {
        super(DEFAULT_RETRY_INTERVAL_MS, MAX_RETRY_ATTEMPTS);
    }
}
