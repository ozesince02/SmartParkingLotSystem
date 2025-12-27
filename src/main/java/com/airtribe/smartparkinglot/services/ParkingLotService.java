package com.airtribe.smartparkinglot.services;

import com.airtribe.smartparkinglot.entities.ParkingLot;
import com.airtribe.smartparkinglot.entities.ParkingSpot;
import com.airtribe.smartparkinglot.entities.ParkingTicket;
import com.airtribe.smartparkinglot.entities.Vehicle;
import com.airtribe.smartparkinglot.exceptions.InvalidTicketException;
import com.airtribe.smartparkinglot.repository.ParkingTicketRepository;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entry point for gates. Delegates allocation and fee calculation.
 */
public final class ParkingLotService {
    private final ParkingLot parkingLot;
    private final AllocationService allocationService;
    private final FeeCalculator feeCalculator;
    private final ParkingTicketRepository ticketRepository;

    public ParkingLotService(
            ParkingLot parkingLot,
            AllocationService allocationService,
            FeeCalculator feeCalculator,
            ParkingTicketRepository ticketRepository
    ) {
        this.parkingLot = Objects.requireNonNull(parkingLot, "parkingLot");
        this.allocationService = Objects.requireNonNull(allocationService, "allocationService");
        this.feeCalculator = Objects.requireNonNull(feeCalculator, "feeCalculator");
        this.ticketRepository = Objects.requireNonNull(ticketRepository, "ticketRepository");
    }

    public ParkingTicket checkIn(Vehicle vehicle) {
        Objects.requireNonNull(vehicle, "vehicle");

        ParkingSpot spot = allocationService.allocateSpot(parkingLot, vehicle);
        String ticketId = UUID.randomUUID().toString();
        ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, spot, Instant.now());
        ticketRepository.save(ticket);
        return ticket;
    }

    public double checkOut(String ticketId) {
        if (ticketId == null || ticketId.isBlank()) {
            throw new IllegalArgumentException("ticketId must be non-empty");
        }

        // Atomic remove ensures concurrent exits are safe (one wins, others see invalid ticket).
        ParkingTicket ticket = ticketRepository.remove(ticketId);
        if (ticket == null) {
            throw new InvalidTicketException("Ticket not found: " + ticketId);
        }

        ticket.markExited(Instant.now());

        // Free spot (thread-safe)
        ticket.getSpot().remove();

        return feeCalculator.calculateFee(ticket);
    }
}


