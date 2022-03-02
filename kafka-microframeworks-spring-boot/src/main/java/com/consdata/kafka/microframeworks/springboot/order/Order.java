package com.consdata.kafka.microframeworks.springboot.order;

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

    private String orderId;

    private int productId;

    private OrderType orderType;

    // Integer for simplicity’s sake
    private int desiredPrice;

    private static final SecureRandom random = new SecureRandom();

    public static Order sell() {
        return Order.generate(OrderType.SELL);
    }

    public static Order buy() {
        return Order.generate(OrderType.BUY);
    }

    private static Order generate(OrderType orderType) {
        return Order
                .builder()
                .orderId(RandomStringUtils.randomAlphanumeric(4))
                .productId(random.nextInt(0, 10))
                .orderType(orderType)
                .desiredPrice(random.nextInt(1, 100))
                .build();
    }
}
