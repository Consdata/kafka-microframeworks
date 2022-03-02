package com.consdata.kafka.microframeworks.micronaut;

import io.micronaut.runtime.Micronaut;

public class KafkaMicronautApplication {

    public static void main(String[] args) {
        Micronaut.run(KafkaMicronautApplication.class, args);
    }
}
