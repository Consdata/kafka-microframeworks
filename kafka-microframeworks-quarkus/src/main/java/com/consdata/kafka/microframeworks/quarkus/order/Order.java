package com.consdata.kafka.microframeworks.quarkus.order;

import com.consdata.kafka.microframeworks.quarkus.wallet.Stock;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.security.SecureRandom;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    private int customerId;

    private String stockSymbol;

    private int amount;

    // Integer for simplicity's sake
    private int desiredPricePerStock;

    private OrderType orderType;

    private static final SecureRandom RANDOM = new SecureRandom();

    @JsonIgnore
    public String getKey() {
        return stockSymbol;
    }

    public static Order sell() {
        return Order.generate(OrderType.SELL);
    }

    public static Order testSell(int customerId, String stockSymbol) {
        return Order.generateTest(OrderType.SELL, customerId, stockSymbol);
    }

    public static Order buy() {
        return Order.generate(OrderType.BUY);
    }

    public static Order testBuy(int customerId, String stockSymbol) {
        return Order.generateTest(OrderType.BUY, customerId, stockSymbol);
    }

    private static Order generate(OrderType orderType) {
        Stock stock = Stock.getRandomStockOption();
        return Order
                .builder()
                .customerId(RANDOM.nextInt(0, 100))
                .stockSymbol(stock.getSymbol())
                .amount(RANDOM.nextInt(10, 100))
                .desiredPricePerStock(RANDOM.nextInt(stock.getMinPrice(), stock.getMaxPrice()))
                .orderType(orderType)
                .build();
    }

    private static Order generateTest(OrderType orderType, int customerId, String stockSymbol) {
        return Order
                .builder()
                .customerId(customerId)
                .stockSymbol(stockSymbol)
                .amount(10)
                .desiredPricePerStock(10)
                .orderType(orderType)
                .build();
    }
}
