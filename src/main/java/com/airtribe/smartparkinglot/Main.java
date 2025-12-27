package com.airtribe.smartparkinglot;

import com.airtribe.smartparkinglot.entities.ParkingFloor;
import com.airtribe.smartparkinglot.entities.ParkingLot;
import com.airtribe.smartparkinglot.entities.ParkingSpot;
import com.airtribe.smartparkinglot.entities.ParkingTicket;
import com.airtribe.smartparkinglot.entities.Vehicle;
import com.airtribe.smartparkinglot.enums.VehicleType;
import com.airtribe.smartparkinglot.gates.EntryGate;
import com.airtribe.smartparkinglot.gates.ExitGate;
import com.airtribe.smartparkinglot.repository.ParkingTicketRepository;
import com.airtribe.smartparkinglot.services.AllocationService;
import com.airtribe.smartparkinglot.services.FeeCalculator;
import com.airtribe.smartparkinglot.services.ParkingLotService;

import java.util.List;

public final class Main {
    public static void main(String[] args) {
        ParkingLot parkingLot = buildSampleParkingLot();

        ParkingLotService service = new ParkingLotService(
                parkingLot,
                new AllocationService(),
                new FeeCalculator(),
                new ParkingTicketRepository()
        );

        EntryGate entryGate = new EntryGate(service);
        ExitGate exitGate = new ExitGate(service);

        Vehicle car = new Vehicle("DL-01-1234", VehicleType.CAR);
        ParkingTicket ticket = entryGate.enter(car);
        System.out.println("Car parked. TicketId=" + ticket.getTicketId() + ", Spot=" + ticket.getSpot().getSpotId());

        double fee = exitGate.exit(ticket.getTicketId());
        System.out.println("Car exited. Fee=₹" + fee);
    }

    private static ParkingLot buildSampleParkingLot() {
        ParkingFloor floor1 = new ParkingFloor("F1", List.of(
                new ParkingSpot("F1-M1", VehicleType.MOTORCYCLE),
                new ParkingSpot("F1-C1", VehicleType.CAR),
                new ParkingSpot("F1-C2", VehicleType.CAR),
                new ParkingSpot("F1-B1", VehicleType.BUS)
        ));

        ParkingFloor floor2 = new ParkingFloor("F2", List.of(
                new ParkingSpot("F2-M1", VehicleType.MOTORCYCLE),
                new ParkingSpot("F2-C1", VehicleType.CAR),
                new ParkingSpot("F2-B1", VehicleType.BUS)
        ));

        return new ParkingLot("LOT-1", List.of(floor1, floor2));
    }
}


