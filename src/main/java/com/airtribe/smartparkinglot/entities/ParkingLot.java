package com.airtribe.smartparkinglot.entities;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Root aggregate. Floors and spots are internal to the parking lot.
 * Entry/Exit are intended to happen via gates + services.
 */
public final class ParkingLot {
    private final String lotId;
    private final List<ParkingFloor> floors;

    public ParkingLot(String lotId, List<ParkingFloor> floors) {
        if (lotId == null || lotId.isBlank()) {
            throw new IllegalArgumentException("lotId must be non-empty");
        }
        this.lotId = lotId;
        this.floors = List.copyOf(Objects.requireNonNull(floors, "floors"));
    }

    public String getLotId() {
        return lotId;
    }

    /**
     * Internal read-only access (services/gates). Not meant for external mutation.
     */
    public List<ParkingFloor> getFloors() {
        return Collections.unmodifiableList(floors);
    }
}


