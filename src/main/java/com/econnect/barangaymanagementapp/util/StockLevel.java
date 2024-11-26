package com.econnect.barangaymanagementapp.util;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StockLevel {
    OUT_OF_STOCK("Out of Stock"),
    CRITICAL("Critical"),
    SAFETY_STOCK("Safety Stock"),
    LOW("Low"),
    MEDIUM("Medium"),
    MEDIUM_HIGH("Medium High"),
    HIGH("High");
    private final String name;


    public static StockLevel getLevel(int currentStocks, int minStocks, int maxStocks) {
        if (maxStocks <= minStocks) {
            throw new IllegalArgumentException("Max stocks must be greater than min stocks");
        }

        int safetyStock = (int) Math.ceil(minStocks * 0.10);  // Safety stock is 10% of minStocks
        int effectiveMinStocks = minStocks + safetyStock;  // Effective minimum stock is minStocks + safetyStock

        if (currentStocks == 0) {
            return StockLevel.OUT_OF_STOCK;
        }

        if (currentStocks <= effectiveMinStocks && currentStocks > minStocks) {
            return StockLevel.SAFETY_STOCK;
        }

        if (currentStocks <= effectiveMinStocks) {
            return StockLevel.CRITICAL;
        }

        // Calculate percentage-based levels for remaining stocks.
        int range = maxStocks - effectiveMinStocks;
        int adjustedStocks = currentStocks - effectiveMinStocks;
        int percentage = (adjustedStocks * 100) / range;

        if (percentage <= 25) {
            return StockLevel.LOW;
        } else if (percentage <= 50) {
            return StockLevel.MEDIUM;
        } else if (percentage <= 75) {
            return StockLevel.MEDIUM_HIGH;
        } else {
            return StockLevel.HIGH;
        }
    }

    public static StockLevel fromName(String name) {
        for (StockLevel level : StockLevel.values()) {
            if (level.getName().equals(name)) {
                return level;
            }
        }
        return null;
    }
}
