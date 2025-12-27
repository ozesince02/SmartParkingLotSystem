package com.airtribe.smartparkinglot.utils;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

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
}


