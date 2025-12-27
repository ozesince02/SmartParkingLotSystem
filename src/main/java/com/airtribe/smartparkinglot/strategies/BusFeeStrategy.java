package com.airtribe.smartparkinglot.strategies;

public final class BusFeeStrategy implements FeeStrategy {
    private static final double RATE_PER_HOUR = 50.0;

    @Override
    public double calculateFee(long hours) {
        if (hours <= 0) {
            throw new IllegalArgumentException("hours must be >= 1");
        }
        return RATE_PER_HOUR * hours;
    }
}


