package com.airtribe.smartparkinglot.services;

import com.airtribe.smartparkinglot.entities.ParkingFloor;
import com.airtribe.smartparkinglot.entities.ParkingLot;
import com.airtribe.smartparkinglot.entities.ParkingSpot;
import com.airtribe.smartparkinglot.entities.ParkingTicket;
import com.airtribe.smartparkinglot.entities.Vehicle;
import com.airtribe.smartparkinglot.enums.VehicleType;
import com.airtribe.smartparkinglot.exceptions.InvalidTicketException;
import com.airtribe.smartparkinglot.repository.ParkingTicketRepository;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ParkingLotServiceTest {

    @Test
    void checkIn_createsTicket_savesIt_andOccupiesSpot() {
        ParkingSpot c1 = new ParkingSpot("C1", VehicleType.CAR);
        ParkingLot lot = new ParkingLot("LOT-1", List.of(new ParkingFloor("F1", List.of(c1))));
        ParkingTicketRepository repo = new ParkingTicketRepository();

        ParkingLotService service = new ParkingLotService(lot, new AllocationService(), new FeeCalculator(), repo);

        ParkingTicket ticket = service.checkIn(new Vehicle("DL-01", VehicleType.CAR));

        assertNotNull(ticket.getTicketId());
        assertFalse(ticket.getTicketId().isBlank());
        assertSame(c1, ticket.getSpot());
        assertTrue(c1.isOccupied());
        assertSame(ticket, repo.find(ticket.getTicketId()));
    }

    @Test
    void checkOut_invalidTicket_throwsInvalidTicketException() {
        ParkingLot lot = new ParkingLot("LOT-1", List.of());
        ParkingLotService service = new ParkingLotService(lot, new AllocationService(), new FeeCalculator(), new ParkingTicketRepository());

        assertThrows(InvalidTicketException.class, () -> service.checkOut("missing"));
    }

    @Test
    void checkOut_nullOrBlankTicketId_throwsIllegalArgumentException() {
        ParkingLot lot = new ParkingLot("LOT-1", List.of());
        ParkingLotService service = new ParkingLotService(lot, new AllocationService(), new FeeCalculator(), new ParkingTicketRepository());

        assertThrows(IllegalArgumentException.class, () -> service.checkOut(null));
        assertThrows(IllegalArgumentException.class, () -> service.checkOut(""));
        assertThrows(IllegalArgumentException.class, () -> service.checkOut("   "));
    }

    @Test
    void checkOut_happyPath_removesTicket_releasesSpot_andReturnsFee() {
        ParkingSpot c1 = new ParkingSpot("C1", VehicleType.CAR);
        c1.park(new Vehicle("TEMP", VehicleType.CAR));

        ParkingTicketRepository repo = new ParkingTicketRepository();
        ParkingTicket ticket = new ParkingTicket(
                "T-1",
                new Vehicle("DL-01", VehicleType.CAR),
                c1,
                Instant.now().minus(2, ChronoUnit.HOURS)
        );
        repo.save(ticket);

        FeeCalculator calculator = new FeeCalculator(Map.of(VehicleType.CAR, hours -> 123.0));
        ParkingLotService service = new ParkingLotService(new ParkingLot("LOT-1", List.of()), new AllocationService(), calculator, repo);

        double fee = service.checkOut("T-1");

        assertEquals(123.0, fee);
        assertNull(repo.find("T-1"));
        assertFalse(c1.isOccupied());
    }

    @Test
    void checkOut_calledTwice_secondTimeThrowsInvalidTicket() {
        ParkingSpot c1 = new ParkingSpot("C1", VehicleType.CAR);
        c1.park(new Vehicle("TEMP", VehicleType.CAR));

        ParkingTicketRepository repo = new ParkingTicketRepository();
        ParkingTicket ticket = new ParkingTicket(
                "T-1",
                new Vehicle("DL-01", VehicleType.CAR),
                c1,
                Instant.now().minus(10, ChronoUnit.MINUTES)
        );
        repo.save(ticket);

        FeeCalculator calculator = new FeeCalculator(Map.of(VehicleType.CAR, hours -> 1.0));
        ParkingLotService service = new ParkingLotService(new ParkingLot("LOT-1", List.of()), new AllocationService(), calculator, repo);

        assertEquals(1.0, service.checkOut("T-1"));
        assertThrows(InvalidTicketException.class, () -> service.checkOut("T-1"));
    }
}

