package com.airtribe.smartparkinglot.gates;

import com.airtribe.smartparkinglot.entities.ParkingFloor;
import com.airtribe.smartparkinglot.entities.ParkingLot;
import com.airtribe.smartparkinglot.entities.ParkingSpot;
import com.airtribe.smartparkinglot.entities.ParkingTicket;
import com.airtribe.smartparkinglot.entities.Vehicle;
import com.airtribe.smartparkinglot.enums.VehicleType;
import com.airtribe.smartparkinglot.exceptions.InvalidTicketException;
import com.airtribe.smartparkinglot.repository.ParkingTicketRepository;
import com.airtribe.smartparkinglot.services.AllocationService;
import com.airtribe.smartparkinglot.services.FeeCalculator;
import com.airtribe.smartparkinglot.services.ParkingLotService;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GateIntegrationTest {

    @Test
    void entryGate_thenExitGate_happyPath_ticketFlowAndSpotReleased() {
        ParkingSpot c1 = new ParkingSpot("C1", VehicleType.CAR);
        ParkingLot lot = new ParkingLot("LOT-1", List.of(new ParkingFloor("F1", List.of(c1))));
        ParkingTicketRepository repo = new ParkingTicketRepository();

        FeeCalculator calculator = new FeeCalculator(Map.of(VehicleType.CAR, hours -> 77.0));
        ParkingLotService service = new ParkingLotService(lot, new AllocationService(), calculator, repo);

        EntryGate entryGate = new EntryGate(service);
        ExitGate exitGate = new ExitGate(service);

        ParkingTicket ticket = entryGate.enter(new Vehicle("DL-01", VehicleType.CAR));

        assertTrue(c1.isOccupied());
        assertNotNull(repo.find(ticket.getTicketId()));

        double fee = exitGate.exit(ticket.getTicketId());

        assertEquals(77.0, fee);
        assertFalse(c1.isOccupied(), "Spot should be freed on exit");
        assertNull(repo.find(ticket.getTicketId()), "Ticket should be removed from active repository on exit");
    }

    @Test
    void exitGate_invalidTicket_throwsInvalidTicketException() {
        ParkingLotService service = new ParkingLotService(
                new ParkingLot("LOT-1", List.of()),
                new AllocationService(),
                new FeeCalculator(),
                new ParkingTicketRepository()
        );

        ExitGate exitGate = new ExitGate(service);
        assertThrows(InvalidTicketException.class, () -> exitGate.exit("missing"));
    }
}

