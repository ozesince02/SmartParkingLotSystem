package com.airtribe.smartparkinglot;

import com.airtribe.smartparkinglot.entities.ParkingFloor;
import com.airtribe.smartparkinglot.entities.ParkingLot;
import com.airtribe.smartparkinglot.entities.ParkingSpot;
import com.airtribe.smartparkinglot.entities.ParkingTicket;
import com.airtribe.smartparkinglot.entities.Vehicle;
import com.airtribe.smartparkinglot.enums.VehicleType;
import com.airtribe.smartparkinglot.exceptions.InvalidTicketException;
import com.airtribe.smartparkinglot.exceptions.ParkingFullException;
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

        System.out.println("=========================================");
        System.out.println(" Smart Parking Lot System - Demo Flow ");
        System.out.println(" LotId=" + parkingLot.getLotId());
        System.out.println("=========================================");

        // 1) Entry flow (first-fit allocation across floors)
        System.out.println("\n[1] Entry: park a CAR");
        ParkingTicket carTicket = entryGate.enter(new Vehicle("DL-01-1234", VehicleType.CAR));
        printTicket(carTicket);

        System.out.println("\n[2] Entry: park a MOTORCYCLE");
        ParkingTicket bikeTicket = entryGate.enter(new Vehicle("DL-02-7777", VehicleType.MOTORCYCLE));
        printTicket(bikeTicket);

        System.out.println("\n[3] Entry: park a BUS");
        ParkingTicket busTicket1 = entryGate.enter(new Vehicle("DL-03-9999", VehicleType.BUS));
        printTicket(busTicket1);

        // 2) Exit flow (ticket close + spot release + fee)
        System.out.println("\n[4] Exit: CAR leaves (fee calculated using strategy + TimeUtil)" );
        double carFee = exitGate.exit(carTicket.getTicketId());
        System.out.println("Exit successful. TicketId=" + carTicket.getTicketId() + ", Fee=₹" + carFee);

        // 3) Parking-full scenario for a specific vehicle type (BUS)
        System.out.println("\n[5] Entry: park another BUS" );
        ParkingTicket busTicket2 = entryGate.enter(new Vehicle("DL-04-1212", VehicleType.BUS));
        printTicket(busTicket2);

        System.out.println("\n[6] Entry: attempt to park a 3rd BUS (should fail - BUS spots are limited)" );
        try {
            entryGate.enter(new Vehicle("DL-05-3434", VehicleType.BUS));
            System.out.println("Unexpected: BUS was parked even though BUS spots should be full.");
        } catch (ParkingFullException e) {
            System.out.println("Expected failure: " + e.getMessage());
        }

        // 4) Invalid ticket scenario
        System.out.println("\n[7] Exit: invalid ticket" );
        try {
            exitGate.exit("INVALID-TICKET-ID");
            System.out.println("Unexpected: invalid ticket did not throw.");
        } catch (InvalidTicketException e) {
            System.out.println("Expected failure: " + e.getMessage());
        }

        // 5) Exit remaining tickets
        System.out.println("\n[8] Exit: MOTORCYCLE leaves" );
        double bikeFee = exitGate.exit(bikeTicket.getTicketId());
        System.out.println("Exit successful. TicketId=" + bikeTicket.getTicketId() + ", Fee=₹" + bikeFee);

        System.out.println("\n[9] Exit: BUS leaves" );
        double busFee = exitGate.exit(busTicket1.getTicketId());
        System.out.println("Exit successful. TicketId=" + busTicket1.getTicketId() + ", Fee=₹" + busFee);

        System.out.println("\n[10] Exit: BUS leaves (2nd)" );
        double busFee2 = exitGate.exit(busTicket2.getTicketId());
        System.out.println("Exit successful. TicketId=" + busTicket2.getTicketId() + ", Fee=₹" + busFee2);

        System.out.println("\nDemo finished. For exhaustive scenarios, see the JUnit tests under src/test/java.");
    }

    private static void printTicket(ParkingTicket ticket) {
        System.out.println(
                "Parked successfully. " +
                        "TicketId=" + ticket.getTicketId() +
                        ", Vehicle=" + ticket.getVehicle().getType() +
                        "(" + ticket.getVehicle().getVehicleNumber() + ")" +
                        ", Spot=" + ticket.getSpot().getSpotId() +
                        ", EntryTime=" + ticket.getEntryTime()
        );
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
