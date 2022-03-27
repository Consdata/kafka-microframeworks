package com.consdata.kafka.microframeworks.quarkus.transaction;

import io.quarkus.kafka.client.serialization.JsonbDeserializer;

public class TransactionDeserializer extends JsonbDeserializer<Transaction> {

    public TransactionDeserializer() {
        super(Transaction.class);
    }
}
