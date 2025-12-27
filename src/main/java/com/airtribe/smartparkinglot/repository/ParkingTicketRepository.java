package com.airtribe.smartparkinglot.repository;

import com.airtribe.smartparkinglot.entities.ParkingTicket;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory persistence (MANDATORY: ConcurrentHashMap).
 */
public final class ParkingTicketRepository {
    private final ConcurrentHashMap<String, ParkingTicket> activeTickets = new ConcurrentHashMap<>();

    public void save(ParkingTicket ticket) {
        Objects.requireNonNull(ticket, "ticket");
        activeTickets.put(ticket.getTicketId(), ticket);
    }

    public ParkingTicket find(String ticketId) {
        Objects.requireNonNull(ticketId, "ticketId");
        return activeTickets.get(ticketId);
    }

    public ParkingTicket remove(String ticketId) {
        Objects.requireNonNull(ticketId, "ticketId");
        return activeTickets.remove(ticketId);
    }
}


