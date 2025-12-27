package com.airtribe.smartparkinglot.repository;

import com.airtribe.smartparkinglot.entities.ParkingSpot;
import com.airtribe.smartparkinglot.entities.ParkingTicket;
import com.airtribe.smartparkinglot.entities.Vehicle;
import com.airtribe.smartparkinglot.enums.VehicleType;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class ParkingTicketRepositoryTest {

    @Test
    void save_find_remove_roundTrip() {
        ParkingTicketRepository repo = new ParkingTicketRepository();
        ParkingTicket ticket = new ParkingTicket(
                "T1",
                new Vehicle("DL-01", VehicleType.CAR),
                new ParkingSpot("C1", VehicleType.CAR),
                Instant.parse("2025-01-01T00:00:00Z")
        );

        repo.save(ticket);
        assertSame(ticket, repo.find("T1"));

        ParkingTicket removed = repo.remove("T1");
        assertSame(ticket, removed);
        assertNull(repo.find("T1"));
    }

    @Test
    void remove_nonExisting_returnsNull() {
        ParkingTicketRepository repo = new ParkingTicketRepository();
        assertNull(repo.remove("missing"));
    }
}

