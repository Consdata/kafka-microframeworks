package com.consdata.kafka.microframeworks.springboot.order;

import com.consdata.kafka.microframeworks.springboot.wallet.Stock;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

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

    public String getKey() {
        return stockSymbol;
    }

    public static Order sell() {
        return Order.generate(OrderType.SELL);
    }

    public static Order buy() {
        return Order.generate(OrderType.BUY);
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
}
