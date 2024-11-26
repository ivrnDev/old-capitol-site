package com.econnect.barangaymanagementapp.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class StockLevel {

    public static Level getLevel(int currentStocks, int minStocks, int maxStocks) {
        if (maxStocks <= minStocks) {
            throw new IllegalArgumentException("Max stocks must be greater than min stocks");
        }

        int safetyStock = (int) Math.ceil(minStocks * 0.10);  // Safety stock is 10% of minStocks
        int effectiveMinStocks = minStocks + safetyStock;  // Effective minimum stock is minStocks + safetyStock

        if (currentStocks == 0) {
            return Level.OUT_OF_STOCK;
        }

        if (currentStocks <= effectiveMinStocks && currentStocks > minStocks) {
            return Level.SAFETY_STOCK;
        }

        if (currentStocks <= effectiveMinStocks) {
            return Level.CRITICAL;
        }

        // Calculate percentage-based levels for remaining stocks.
        int range = maxStocks - effectiveMinStocks;
        int adjustedStocks = currentStocks - effectiveMinStocks;
        int percentage = (adjustedStocks * 100) / range;

        if (percentage <= 25) {
            return Level.LOW;
        } else if (percentage <= 50) {
            return Level.MEDIUM;
        } else if (percentage <= 75) {
            return Level.MEDIUM_HIGH;
        } else {
            return Level.HIGH;
        }
    }

    @AllArgsConstructor
    @Getter
    public enum Level {
        OUT_OF_STOCK("Out of Stock"),
        CRITICAL("Critical"),
        SAFETY_STOCK("Safety Stock"),
        LOW("Low"),
        MEDIUM("Medium"),
        MEDIUM_HIGH("Medium High"),
        HIGH("High");

        private final String name;
    }
}
