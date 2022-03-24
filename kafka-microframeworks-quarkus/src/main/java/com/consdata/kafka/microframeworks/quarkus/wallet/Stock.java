package com.consdata.kafka.microframeworks.quarkus.wallet;

import java.security.SecureRandom;

public enum Stock {

    MICROSOFT("MSFT", 10, 100),

    APPLE("APPL", 10, 100),

    GOOGLE("GOOGL", 10, 100),

    AMAZON("AMZN", 10, 100),

    TESLA("TSLA", 10, 100),

    NVIDIA("NVDA", 10, 100),

    TSMC("TSM", 10, 100),

    FACEBOOK("FB", 10, 100),

    CISCO("CSCO", 10, 100),

    ORACLE("ORCL", 10, 100),

    INTEL("INTC", 10, 100);

    Stock(String symbol, int minPrice, int maxPrice) {
        this.symbol = symbol;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
    }

    private final String symbol;

    private final int minPrice;

    private final int maxPrice;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static Stock getRandomStockOption() {
        return values()[RANDOM.nextInt(values().length)];
    }

    public String getSymbol() {
        return symbol;
    }

    public int getMinPrice() {
        return minPrice;
    }

    public int getMaxPrice() {
        return maxPrice;
    }
}
