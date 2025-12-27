package com.airtribe.smartparkinglot.services;

import com.airtribe.smartparkinglot.entities.ParkingTicket;
import com.airtribe.smartparkinglot.enums.VehicleType;
import com.airtribe.smartparkinglot.strategies.BikeFeeStrategy;
import com.airtribe.smartparkinglot.strategies.BusFeeStrategy;
import com.airtribe.smartparkinglot.strategies.CarFeeStrategy;
import com.airtribe.smartparkinglot.strategies.FeeStrategy;
import com.airtribe.smartparkinglot.utils.TimeUtil;

import java.time.Instant;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public final class FeeCalculator {
    private final Map<VehicleType, FeeStrategy> strategies;

    public FeeCalculator() {
        EnumMap<VehicleType, FeeStrategy> map = new EnumMap<>(VehicleType.class);
        map.put(VehicleType.MOTORCYCLE, new BikeFeeStrategy());
        map.put(VehicleType.CAR, new CarFeeStrategy());
        map.put(VehicleType.BUS, new BusFeeStrategy());
        this.strategies = Map.copyOf(map);
    }

    public FeeCalculator(Map<VehicleType, FeeStrategy> strategies) {
        this.strategies = Map.copyOf(Objects.requireNonNull(strategies, "strategies"));
    }

    public double calculateFee(ParkingTicket ticket) {
        Objects.requireNonNull(ticket, "ticket");
        Instant exitTime = ticket.getExitTime();
        if (exitTime == null) {
            throw new IllegalStateException("Ticket " + ticket.getTicketId() + " has no exitTime");
        }

        long hours = TimeUtil.billableHours(ticket.getEntryTime(), exitTime);
        VehicleType vehicleType = ticket.getVehicle().getType();
        FeeStrategy strategy = strategies.get(vehicleType);
        if (strategy == null) {
            throw new IllegalArgumentException("No FeeStrategy registered for " + vehicleType);
        }
        return strategy.calculateFee(hours);
    }
}


