package com.airtribe.smartparkinglot.services;

import com.airtribe.smartparkinglot.entities.ParkingFloor;
import com.airtribe.smartparkinglot.entities.ParkingLot;
import com.airtribe.smartparkinglot.entities.ParkingSpot;
import com.airtribe.smartparkinglot.entities.Vehicle;
import com.airtribe.smartparkinglot.enums.VehicleType;
import com.airtribe.smartparkinglot.exceptions.ParkingFullException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class AllocationServiceConcurrencyTest {

    @Test
    void allocateSpot_concurrentRequests_sameSingleSpot_onlyOneSucceeds() throws Exception {
        ParkingSpot c1 = new ParkingSpot("C1", VehicleType.CAR);
        ParkingLot lot = new ParkingLot("LOT-1", List.of(new ParkingFloor("F1", List.of(c1))));

        AllocationService allocationService = new AllocationService();

        int threads = 20;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CyclicBarrier startBarrier = new CyclicBarrier(threads);

        List<Callable<Boolean>> tasks = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            final int idx = i;
            tasks.add(() -> {
                startBarrier.await(2, TimeUnit.SECONDS);
                try {
                    allocationService.allocateSpot(lot, new Vehicle("CAR-" + idx, VehicleType.CAR));
                    return true;
                } catch (ParkingFullException e) {
                    return false;
                }
            });
        }

        List<Future<Boolean>> results = pool.invokeAll(tasks);
        pool.shutdown();
        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));

        long successes = 0;
        for (Future<Boolean> f : results) {
            if (f.get(2, TimeUnit.SECONDS)) {
                successes++;
            }
        }

        assertEquals(1, successes, "Exactly one thread should successfully allocate the only spot");
        assertTrue(c1.isOccupied());
    }

    @Test
    void allocateSpot_concurrentRequests_multipleSpots_allocatesDistinctSpots() throws Exception {
        ParkingSpot c1 = new ParkingSpot("C1", VehicleType.CAR);
        ParkingSpot c2 = new ParkingSpot("C2", VehicleType.CAR);
        ParkingSpot c3 = new ParkingSpot("C3", VehicleType.CAR);

        ParkingLot lot = new ParkingLot("LOT-1", List.of(new ParkingFloor("F1", List.of(c1, c2, c3))));
        AllocationService allocationService = new AllocationService();

        int threads = 3;
        ExecutorService pool = Executors.newFixedThreadPool(threads);
        CyclicBarrier startBarrier = new CyclicBarrier(threads);

        List<Callable<String>> tasks = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            final int idx = i;
            tasks.add(() -> {
                startBarrier.await(2, TimeUnit.SECONDS);
                return allocationService
                        .allocateSpot(lot, new Vehicle("CAR-" + idx, VehicleType.CAR))
                        .getSpotId();
            });
        }

        List<Future<String>> results = pool.invokeAll(tasks);
        pool.shutdown();
        assertTrue(pool.awaitTermination(5, TimeUnit.SECONDS));

        Set<String> allocated = ConcurrentHashMap.newKeySet();
        for (Future<String> f : results) {
            allocated.add(f.get(2, TimeUnit.SECONDS));
        }

        assertEquals(3, allocated.size(), "Each vehicle should get a distinct spot when enough exist");
        assertTrue(c1.isOccupied());
        assertTrue(c2.isOccupied());
        assertTrue(c3.isOccupied());
    }
}
