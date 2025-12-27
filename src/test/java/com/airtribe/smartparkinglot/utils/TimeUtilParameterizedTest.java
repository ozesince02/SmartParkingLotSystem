package com.airtribe.smartparkinglot.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TimeUtilParameterizedTest {

    @ParameterizedTest
    @CsvSource({
            "0,1",
            "1,1",
            "59,1",
            "60,1",
            "61,2",
            "119,2",
            "120,2",
            "121,3"
    })
    void billableHours_variousDurations_minutesToHoursCeil(long minutes, long expectedHours) {
        Instant entry = Instant.parse("2025-01-01T00:00:00Z");
        Instant exit = entry.plus(minutes, ChronoUnit.MINUTES);

        assertEquals(expectedHours, TimeUtil.billableHours(entry, exit));
    }
}

