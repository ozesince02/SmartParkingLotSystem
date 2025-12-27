package com.airtribe.smartparkinglot.entities;

import com.airtribe.smartparkinglot.enums.VehicleType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ParkingSpotTest {

    @Test
    void isAvailableFor_onlyWhenNotOccupiedAndVehicleTypeMatches() {
        ParkingSpot spot = new ParkingSpot("C1", VehicleType.CAR);

        assertTrue(spot.isAvailableFor(VehicleType.CAR));
        assertFalse(spot.isAvailableFor(VehicleType.MOTORCYCLE));

        spot.park(new Vehicle("DL-01", VehicleType.CAR));
        assertFalse(spot.isAvailableFor(VehicleType.CAR));
        assertTrue(spot.isOccupied());
    }

    @Test
    void park_throwsIfWrongVehicleType() {
        ParkingSpot spot = new ParkingSpot("C1", VehicleType.CAR);
        assertThrows(IllegalArgumentException.class, () -> spot.park(new Vehicle("M-1", VehicleType.MOTORCYCLE)));
    }

    @Test
    void remove_throwsIfSpotAlreadyEmpty() {
        ParkingSpot spot = new ParkingSpot("C1", VehicleType.CAR);
        assertThrows(IllegalStateException.class, spot::remove);
    }

    @Test
    void park_thenRemove_makesSpotAvailableAgain() {
        ParkingSpot spot = new ParkingSpot("C1", VehicleType.CAR);
        spot.park(new Vehicle("DL-01", VehicleType.CAR));
        spot.remove();
        assertFalse(spot.isOccupied());
        assertTrue(spot.isAvailableFor(VehicleType.CAR));
    }
}

