package com.airtribe.smartparkinglot.utils;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

class TimeUtilTest {

    @Test
    void billableHours_minimumOneHour() {
        Instant t0 = Instant.now();
        Instant t1 = t0.plus(1, ChronoUnit.MINUTES);
        assertEquals(1, TimeUtil.billableHours(t0, t1));
    }

    @Test
    void billableHours_roundsUpPartialHour() {
        Instant t0 = Instant.now();
        Instant t1 = t0.plus(61, ChronoUnit.MINUTES);
        assertEquals(2, TimeUtil.billableHours(t0, t1));
    }

    @Test
    void billableHours_exactHours() {
        Instant t0 = Instant.now();
        Instant t1 = t0.plus(120, ChronoUnit.MINUTES);
        assertEquals(2, TimeUtil.billableHours(t0, t1));
    }

    @Test
    void billableHours_zeroDuration_billsOneHour() {
        Instant t0 = Instant.parse("2025-01-01T00:00:00Z");
        assertEquals(1, TimeUtil.billableHours(t0, t0));
    }

    @Test
    void billableHours_exitBeforeEntry_throws() {
        Instant t0 = Instant.parse("2025-01-01T00:00:00Z");
        Instant t1 = Instant.parse("2024-12-31T23:59:59Z");
        assertThrows(IllegalArgumentException.class, () -> TimeUtil.billableHours(t0, t1));
    }

    @Test
    void billableHours_nullArguments_throw() {
        Instant t0 = Instant.parse("2025-01-01T00:00:00Z");
        assertThrows(NullPointerException.class, () -> TimeUtil.billableHours(null, t0));
        assertThrows(NullPointerException.class, () -> TimeUtil.billableHours(t0, null));
    }
}
