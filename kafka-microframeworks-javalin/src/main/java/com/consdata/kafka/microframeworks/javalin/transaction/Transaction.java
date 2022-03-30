package com.consdata.kafka.microframeworks.javalin.transaction;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

import static java.lang.String.format;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    private String stockSymbol;

    private int amount;

    private int sellingCustomerId;

    private int buyingCustomerId;

    private int price;

    private Date executionTimestamp;

    public String getKey() {
        return format("%s_%s_%s", stockSymbol, sellingCustomerId, buyingCustomerId);
    }

    // thread safety feature - we always need to lock wallets in the same order
    public int smallerCustomerId() {
        return Math.min(sellingCustomerId, buyingCustomerId);
    }

    // thread safety feature - we always need to lock wallets in the same order
    public int biggerCustomerId() {
        return Math.max(sellingCustomerId, buyingCustomerId);
    }

}
