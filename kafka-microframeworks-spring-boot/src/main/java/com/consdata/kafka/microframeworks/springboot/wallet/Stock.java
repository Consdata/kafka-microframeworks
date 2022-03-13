package com.consdata.kafka.microframeworks.springboot.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stock {

    private String symbol;

    private int minPrice;

    private int maxPrice;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static final List<Stock> STOCKS = List.of(
            Stock.builder().symbol("MSFT").minPrice(10).maxPrice(20).build(),
            Stock.builder().symbol("AAPL").minPrice(50).maxPrice(63).build(),
            Stock.builder().symbol("GOOGL").minPrice(10).maxPrice(20).build(),
            Stock.builder().symbol("AMZN").minPrice(10).maxPrice(20).build(),
            Stock.builder().symbol("TSLA").minPrice(10).maxPrice(20).build(),
            Stock.builder().symbol("NVDA").minPrice(10).maxPrice(20).build(),
            Stock.builder().symbol("TSM").minPrice(10).maxPrice(20).build(),
            Stock.builder().symbol("FB").minPrice(10).maxPrice(20).build(),
            Stock.builder().symbol("CSCO").minPrice(10).maxPrice(20).build(),
            Stock.builder().symbol("ORCL").minPrice(10).maxPrice(20).build(),
            Stock.builder().symbol("INTC").minPrice(10).maxPrice(20).build()
    );

    public static Stock getRandomStockOption() {
        return STOCKS.get(RANDOM.nextInt(STOCKS.size()));
    }
}
