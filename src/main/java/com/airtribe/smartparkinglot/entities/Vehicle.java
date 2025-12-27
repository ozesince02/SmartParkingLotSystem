package com.airtribe.smartparkinglot.entities;

import com.airtribe.smartparkinglot.enums.VehicleType;

import java.util.Objects;

public final class Vehicle {
    private final String vehicleNumber;
    private final VehicleType type;

    public Vehicle(String vehicleNumber, VehicleType type) {
        if (vehicleNumber == null || vehicleNumber.isBlank()) {
            throw new IllegalArgumentException("vehicleNumber must be non-empty");
        }
        this.vehicleNumber = vehicleNumber;
        this.type = Objects.requireNonNull(type, "type");
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public VehicleType getType() {
        return type;
    }
}


