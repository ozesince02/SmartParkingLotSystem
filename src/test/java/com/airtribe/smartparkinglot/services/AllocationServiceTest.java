package com.airtribe.smartparkinglot.services;

import com.airtribe.smartparkinglot.entities.ParkingFloor;
import com.airtribe.smartparkinglot.entities.ParkingLot;
import com.airtribe.smartparkinglot.entities.ParkingSpot;
import com.airtribe.smartparkinglot.entities.Vehicle;
import com.airtribe.smartparkinglot.enums.VehicleType;
import com.airtribe.smartparkinglot.exceptions.ParkingFullException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AllocationServiceTest {

    @Test
    void allocateSpot_firstFitAcrossFloors_picksFirstAvailableSpot() {
        ParkingSpot f1c1 = new ParkingSpot("F1-C1", VehicleType.CAR);
        ParkingSpot f1c2 = new ParkingSpot("F1-C2", VehicleType.CAR);
        ParkingSpot f2c1 = new ParkingSpot("F2-C1", VehicleType.CAR);

        ParkingLot lot = new ParkingLot("LOT-1", List.of(
                new ParkingFloor("F1", List.of(f1c1, f1c2)),
                new ParkingFloor("F2", List.of(f2c1))
        ));

        AllocationService allocationService = new AllocationService();
        Vehicle car = new Vehicle("DL-01-1234", VehicleType.CAR);

        ParkingSpot allocated = allocationService.allocateSpot(lot, car);

        assertSame(f1c1, allocated);
        assertTrue(f1c1.isOccupied());
        assertFalse(f1c2.isOccupied());
        assertFalse(f2c1.isOccupied());
    }

    @Test
    void allocateSpot_skipsOccupiedSpot_andAllocatesNext() {
        ParkingSpot f1c1 = new ParkingSpot("F1-C1", VehicleType.CAR);
        ParkingSpot f1c2 = new ParkingSpot("F1-C2", VehicleType.CAR);
        f1c1.park(new Vehicle("X", VehicleType.CAR));

        ParkingLot lot = new ParkingLot("LOT-1", List.of(
                new ParkingFloor("F1", List.of(f1c1, f1c2))
        ));

        AllocationService allocationService = new AllocationService();
        Vehicle car = new Vehicle("DL-01-1234", VehicleType.CAR);

        ParkingSpot allocated = allocationService.allocateSpot(lot, car);

        assertSame(f1c2, allocated);
        assertTrue(f1c2.isOccupied());
    }

    @Test
    void allocateSpot_throwsParkingFullWhenNoSpotForType() {
        ParkingLot lot = new ParkingLot("LOT-1", List.of(
                new ParkingFloor("F1", List.of(
                        new ParkingSpot("F1-M1", VehicleType.MOTORCYCLE)
                ))
        ));

        AllocationService allocationService = new AllocationService();

        assertThrows(ParkingFullException.class,
                () -> allocationService.allocateSpot(lot, new Vehicle("DL-01", VehicleType.CAR)));
    }
}
