package com.airtribe.smartparkinglot.services;

import com.airtribe.smartparkinglot.entities.ParkingSpot;
import com.airtribe.smartparkinglot.entities.ParkingTicket;
import com.airtribe.smartparkinglot.entities.Vehicle;
import com.airtribe.smartparkinglot.enums.VehicleType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FeeCalculatorParameterizedTest {

    record FeeCase(VehicleType type, long minutesParked, double expectedFee) {}

    static Stream<FeeCase> feeCases() {
        // Rates: MOTORCYCLE=10/h, CAR=20/h, BUS=50/h.
        // Billing: min 1 hour, ceil to next hour for partial.
        return Stream.of(
                new FeeCase(VehicleType.MOTORCYCLE, 0, 10.0),
                new FeeCase(VehicleType.MOTORCYCLE, 1, 10.0),
                new FeeCase(VehicleType.MOTORCYCLE, 60, 10.0),
                new FeeCase(VehicleType.MOTORCYCLE, 61, 20.0),

                new FeeCase(VehicleType.CAR, 1, 20.0),
                new FeeCase(VehicleType.CAR, 60, 20.0),
                new FeeCase(VehicleType.CAR, 119, 40.0),
                new FeeCase(VehicleType.CAR, 120, 40.0),

                new FeeCase(VehicleType.BUS, 1, 50.0),
                new FeeCase(VehicleType.BUS, 61, 100.0)
        );
    }

    @ParameterizedTest
    @MethodSource("feeCases")
    void calculateFee_variousVehicleTypesAndDurations(FeeCase c) {
        Instant entry = Instant.parse("2025-01-01T00:00:00Z");
        Instant exit = entry.plus(c.minutesParked(), ChronoUnit.MINUTES);

        ParkingSpot spot = new ParkingSpot("S1", c.type());
        ParkingTicket ticket = new ParkingTicket(
                "T1",
                new Vehicle("V1", c.type()),
                spot,
                entry
        );
        ticket.markExited(exit);

        double fee = new FeeCalculator().calculateFee(ticket);
        assertEquals(c.expectedFee(), fee);
    }
}

