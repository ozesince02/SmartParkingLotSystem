package com.airtribe.smartparkinglot.services;

import com.airtribe.smartparkinglot.entities.ParkingSpot;
import com.airtribe.smartparkinglot.entities.ParkingTicket;
import com.airtribe.smartparkinglot.entities.Vehicle;
import com.airtribe.smartparkinglot.enums.VehicleType;
import com.airtribe.smartparkinglot.strategies.FeeStrategy;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class FeeCalculatorTest {

    @Test
    void calculateFee_car_twoBillableHours_usesCarRate() {
        Instant entry = Instant.parse("2025-01-01T00:00:00Z");
        Instant exit = entry.plus(61, ChronoUnit.MINUTES); // billable=2

        ParkingTicket ticket = new ParkingTicket(
                "T1",
                new Vehicle("DL-01", VehicleType.CAR),
                new ParkingSpot("C1", VehicleType.CAR),
                entry
        );
        ticket.markExited(exit);

        double fee = new FeeCalculator().calculateFee(ticket);
        assertEquals(40.0, fee);
    }

    @Test
    void calculateFee_motorcycle_oneBillableHour_usesBikeRate() {
        Instant entry = Instant.parse("2025-01-01T00:00:00Z");
        Instant exit = entry.plus(1, ChronoUnit.MINUTES); // billable=1

        ParkingTicket ticket = new ParkingTicket(
                "T1",
                new Vehicle("B-01", VehicleType.MOTORCYCLE),
                new ParkingSpot("M1", VehicleType.MOTORCYCLE),
                entry
        );
        ticket.markExited(exit);

        double fee = new FeeCalculator().calculateFee(ticket);
        assertEquals(10.0, fee);
    }

    @Test
    void calculateFee_throwsIfExitTimeMissing() {
        ParkingTicket ticket = new ParkingTicket(
                "T1",
                new Vehicle("DL-01", VehicleType.CAR),
                new ParkingSpot("C1", VehicleType.CAR),
                Instant.parse("2025-01-01T00:00:00Z")
        );

        assertThrows(IllegalStateException.class, () -> new FeeCalculator().calculateFee(ticket));
    }

    @Test
    void calculateFee_throwsIfNoStrategyRegistered() {
        FeeCalculator calculator = new FeeCalculator(Map.of());

        ParkingTicket ticket = new ParkingTicket(
                "T1",
                new Vehicle("DL-01", VehicleType.CAR),
                new ParkingSpot("C1", VehicleType.CAR),
                Instant.parse("2025-01-01T00:00:00Z")
        );
        ticket.markExited(Instant.parse("2025-01-01T01:00:00Z"));

        assertThrows(IllegalArgumentException.class, () -> calculator.calculateFee(ticket));
    }

    @Test
    void calculateFee_usesProvidedStrategyImplementation() {
        FeeStrategy flatStrategy = hours -> 999.0;
        FeeCalculator calculator = new FeeCalculator(Map.of(VehicleType.CAR, flatStrategy));

        ParkingTicket ticket = new ParkingTicket(
                "T1",
                new Vehicle("DL-01", VehicleType.CAR),
                new ParkingSpot("C1", VehicleType.CAR),
                Instant.parse("2025-01-01T00:00:00Z")
        );
        ticket.markExited(Instant.parse("2025-01-01T00:10:00Z"));

        assertEquals(999.0, calculator.calculateFee(ticket));
    }
}

