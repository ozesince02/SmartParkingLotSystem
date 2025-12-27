package com.airtribe.smartparkinglot.gates;

import com.airtribe.smartparkinglot.entities.ParkingTicket;
import com.airtribe.smartparkinglot.entities.Vehicle;
import com.airtribe.smartparkinglot.services.ParkingLotService;

import java.util.Objects;

public final class EntryGate {
    private final ParkingLotService parkingLotService;

    public EntryGate(ParkingLotService parkingLotService) {
        this.parkingLotService = Objects.requireNonNull(parkingLotService, "parkingLotService");
    }

    public ParkingTicket enter(Vehicle vehicle) {
        return parkingLotService.checkIn(vehicle);
    }
}


