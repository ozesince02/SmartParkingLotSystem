package com.airtribe.smartparkinglot.utils;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

public final class TimeUtil {
    private TimeUtil() {}

    /**
     * Calculates duration in billable hours.
     * - Minimum billing = 1 hour
     * - Rounds up partial hours (ceil)
     */
    public static long billableHours(Instant entryTime, Instant exitTime) {
        Objects.requireNonNull(entryTime, "entryTime");
        Objects.requireNonNull(exitTime, "exitTime");

        if (exitTime.isBefore(entryTime)) {
            throw new IllegalArgumentException("exitTime cannot be before entryTime");
        }

        Duration duration = Duration.between(entryTime, exitTime);
        long minutes = duration.toMinutes();
        if (minutes <= 0) {
            return 1;
        }

        long hours = minutes / 60;
        boolean hasPartialHour = (minutes % 60) != 0;
        long billed = hours + (hasPartialHour ? 1 : 0);
        return Math.max(1, billed);
    }
}


