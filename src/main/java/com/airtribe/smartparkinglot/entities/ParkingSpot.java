package com.airtribe.smartparkinglot.entities;

import com.airtribe.smartparkinglot.enums.VehicleType;

import java.util.Objects;

public final class ParkingSpot {
    private final String spotId;
    private final VehicleType allowedVehicleType;
    private boolean occupied;

    public ParkingSpot(String spotId, VehicleType allowedVehicleType) {
        if (spotId == null || spotId.isBlank()) {
            throw new IllegalArgumentException("spotId must be non-empty");
        }
        this.spotId = spotId;
        this.allowedVehicleType = Objects.requireNonNull(allowedVehicleType, "allowedVehicleType");
        this.occupied = false;
    }

    public String getSpotId() {
        return spotId;
    }

    public VehicleType getAllowedVehicleType() {
        return allowedVehicleType;
    }

    public synchronized boolean isOccupied() {
        return occupied;
    }

    public synchronized boolean isAvailableFor(VehicleType vehicleType) {
        return !occupied && allowedVehicleType == vehicleType;
    }

    /**
     * MANDATORY: thread-safe spot occupation.
     */
    public synchronized void park(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "vehicle");
        if (occupied) {
            throw new IllegalStateException("Spot " + spotId + " is already occupied");
        }
        if (vehicle.getType() != allowedVehicleType) {
            throw new IllegalArgumentException(
                    "Spot " + spotId + " does not allow vehicle type " + vehicle.getType());
        }
        occupied = true;
    }

    /**
     * MANDATORY: thread-safe spot release.
     */
    public synchronized void remove() {
        if (!occupied) {
            throw new IllegalStateException("Spot " + spotId + " is already empty");
        }
        occupied = false;
    }
}


