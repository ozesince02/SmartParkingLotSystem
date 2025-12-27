package com.airtribe.smartparkinglot.entities;

import java.time.Instant;
import java.util.Objects;

public final class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSpot spot;
    private final Instant entryTime;
    private volatile Instant exitTime;

    public ParkingTicket(String ticketId, Vehicle vehicle, ParkingSpot spot, Instant entryTime) {
        if (ticketId == null || ticketId.isBlank()) {
            throw new IllegalArgumentException("ticketId must be non-empty");
        }
        this.ticketId = ticketId;
        this.vehicle = Objects.requireNonNull(vehicle, "vehicle");
        this.spot = Objects.requireNonNull(spot, "spot");
        this.entryTime = Objects.requireNonNull(entryTime, "entryTime");
    }

    public String getTicketId() {
        return ticketId;
    }

    public Vehicle getVehicle() {
        return vehicle;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    public Instant getEntryTime() {
        return entryTime;
    }

    public Instant getExitTime() {
        return exitTime;
    }

    public void markExited(Instant exitTime) {
        this.exitTime = Objects.requireNonNull(exitTime, "exitTime");
    }
}


