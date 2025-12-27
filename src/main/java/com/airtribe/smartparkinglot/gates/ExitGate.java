package com.airtribe.smartparkinglot.gates;

import com.airtribe.smartparkinglot.services.ParkingLotService;

import java.util.Objects;

public final class ExitGate {
    private final ParkingLotService parkingLotService;

    public ExitGate(ParkingLotService parkingLotService) {
        this.parkingLotService = Objects.requireNonNull(parkingLotService, "parkingLotService");
    }

    public double exit(String ticketId) {
        return parkingLotService.checkOut(ticketId);
    }
}


