package com.airtribe.smartparkinglot.services;

import com.airtribe.smartparkinglot.entities.ParkingFloor;
import com.airtribe.smartparkinglot.entities.ParkingLot;
import com.airtribe.smartparkinglot.entities.ParkingSpot;
import com.airtribe.smartparkinglot.entities.Vehicle;
import com.airtribe.smartparkinglot.exceptions.ParkingFullException;

import java.util.List;
import java.util.Objects;

/**
 * MANDATORY: synchronized allocation method to prevent race conditions.
 * Strategy: first-fit floor -> spot.
 */
public final class AllocationService {

    public synchronized ParkingSpot allocateSpot(ParkingLot parkingLot, Vehicle vehicle) {
        Objects.requireNonNull(parkingLot, "parkingLot");
        Objects.requireNonNull(vehicle, "vehicle");

        List<ParkingFloor> floors = parkingLot.getFloors();
        for (ParkingFloor floor : floors) {
            for (ParkingSpot spot : floor.getSpots()) {
                if (!spot.isAvailableFor(vehicle.getType())) {
                    continue;
                }

                // Try to occupy; park() is synchronized, and may fail if another thread won the race.
                try {
                    spot.park(vehicle);
                    return spot;
                } catch (IllegalStateException ignoredRace) {
                    // Someone else parked here; keep scanning.
                }
            }
        }
        throw new ParkingFullException("No available spot for vehicle type: " + vehicle.getType());
    }
}


