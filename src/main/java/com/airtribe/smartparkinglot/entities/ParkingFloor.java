package com.airtribe.smartparkinglot.entities;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class ParkingFloor {
    private final String floorId;
    private final List<ParkingSpot> spots;

    public ParkingFloor(String floorId, List<ParkingSpot> spots) {
        if (floorId == null || floorId.isBlank()) {
            throw new IllegalArgumentException("floorId must be non-empty");
        }
        this.floorId = floorId;
        this.spots = List.copyOf(Objects.requireNonNull(spots, "spots"));
    }

    public String getFloorId() {
        return floorId;
    }

    /**
     * Exposed as read-only view; allocation logic should live in services.
     */
    public List<ParkingSpot> getSpots() {
        return Collections.unmodifiableList(spots);
    }
}


