package com.consdata.kafka.microframeworks.springboot.wallet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import static com.consdata.kafka.microframeworks.springboot.wallet.Stock.STOCKS;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Wallet {

    private Map<String, Integer> stockAmount;

    private AtomicInteger balance;

    private static final SecureRandom RANDOM = new SecureRandom();

    public static Wallet generateRandomWallet() {
        Map<String, Integer> stocks = new HashMap<>();
        STOCKS.forEach(s -> stocks.put(s.getSymbol(), RANDOM.nextInt(1000, 2000)));
        return Wallet.builder()
                .balance(new AtomicInteger(RANDOM.nextInt(1_000_000, 2_000_000)))
                .stockAmount(stocks)
                .build();
    }

    public boolean hasEnoughStocks(String stockSymbol, int desiredAmount) {
        return stockAmount.get(stockSymbol) >= desiredAmount;
    }

    public boolean hasEnoughCash(int desiredAmount) {
        return balance.get() >= desiredAmount;
    }

    public void changeBalance(int amount) {
        balance.addAndGet(amount);
    }

    public void changeStockAmount(String stockSymbol, int amount) {
        Integer currentAmount = stockAmount.get(stockSymbol);
        stockAmount.put(stockSymbol, currentAmount + amount);
    }

}
