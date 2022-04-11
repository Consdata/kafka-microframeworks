# Apache Kafka in the era of Java Microframeworks

This repository is intended as a showcase of Apache Kafka integration using four different Java frameworks. It can also 
serve as a guideline for choosing the framework for your next Kafka project.

The four frameworks being compared are:

* Spring Boot
* Micronaut
* Quarkus
* Javalin

## Architecture

The same concept - a simple stock exchange platform - was implemented in each of these frameworks.
Each of the projects follows the same architecture, and they are functionally identical.

We're comparing three main features of Kafka integration:
* Producers
* Stream joins
* Batch consumers

Codebases are purposely separated from each other, and reside in folders corresponding the framework's names: 
`kafka-microframeworks-spring-boot`, `kafka-microframeworks-micronaut`, `kafka-microframeworks-quarkus`,
and `kafka-microframeworks-javalin`.

Diagram below illustrates the architecture of our simple stock exchange platform:

![arch](assets/arch.png)

Order Producer simulates the behaviour of a group of customers that want to buy or sell stocks - it generates random 
Buy and Sell Orders, and puts them into corresponding topics. 
Orders are simple objects, stating that a client with a given ID wants to buy or sell some stocks at a certain price.

This is what an Order may look like:

    {
        "customerId": 45,
        "stockSymbol": "GOOGL",
        "amount": 94,
        "desiredPricePerStock": 27,
        "orderType": "SELL"
    }

In this case, a client with id `45` wants to sell 94 Google stocks, at $27 per stock.

Buy and Sell Order streams are then joined, using stream joins. If you're not familiar with the concept of stream 
joins, I highly recommend [this article](https://www.confluent.io/blog/crossing-streams-joins-apache-kafka/). 
The Stock symbol is used as a key, which makes joining Buy and Sell Orders easy. If business 
requirements are met (i.e. selling side indeed has the stocks it wants to sell, and the buying side has the money) 
then transaction may occur, and Transaction event is created and put into Transaction topic.

Transactions are consumed in batches by Transaction Consumer.

At the start of each application, customers are initialized with a faux wallet, containing random amounts of money and 
stocks.

## Launching & testing

You can easily test each of these projects, there are only two requirements: having _docker_ and _docker-compose_
installed. Everything is orchestrated using docker-compose through [docker-compose.yml](docker-compose.yml) file. 
Inside you'll find 8 services:

| Service    | Exposed Port                    | Description                                                                 |
|------------|---------------------------------|-----------------------------------------------------------------------------|
| zookeeper  | 2181                            | Zookeeper, used to orchestrate Kafka cluster                                |
| kafka      | 9002                            | Kafka itself                                                                |
| kouncil    | [8080](http://localhost:8080)   | Web based Kafka dashboard, allowing you to see what's going on inside Kafka |
| netdata    | [19999](http://localhost:19999) | System monitoring tool                                                      |
| springboot | 8081                            | Spring Boot application, running on JVM                                     |
| micronaut  | 8082                            | Micronaut application, running on JVM                                       |
| quarkus    | 8083                            | Quarkus application, running as native image                                |
| javalin    | 8084                            | Javalin application, running on JVM                                         |

Make sure that all of these ports are available on your host. 

### Building docker images

To build docker images simply run

    docker-compose build

Java source codes will be compiled inside Docker containers, using a multistage build process. The resulting images 
contain only runtime components, without sources, build tools etc. This step may take a couple of minutes, depending 
on your CPU and connection speed. 

### Launching

After everything has been successfully built, you need to provide the IP of your local machine 
in `docker-compose.yml` file, under `services.kafka.environment.KAFKA_ADVERTISED_HOST_NAME` key, for example:

    kafka:
      image: wurstmeister/kafka
      ports:
        - "9092:9092"
      environment:
        KAFKA_ADVERTISED_HOST_NAME: 192.168.0.64
        KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181

Then, you can start Kafka and all the applications by typing:

    docker-compose up -d

This will spawn docker containers - you should see 8 services marked as `done`. 

    Creating kafka-microframeworks_zookeeper_1 ... done
    Creating kafka-microframeworks_netdata_1   ... done
    Creating kafka-microframeworks_kafka_1     ... done
    Creating kafka-microframeworks_kouncil_1   ... done
    Creating quarkus                           ... done
    Creating micronaut                         ... done
    Creating spring-boot                       ... done
    Creating javalin                           ... done

At any point in time you can make sure that everything is ok by checking the output of `ps` command, like so:

    docker-compose ps

You should see 8 services, each one of them in `Up` state.

Head to [http://localhost:8080](http://localhost:8080), you'll find Kouncil there, which is a web dashboard for 
Apache Kafka. You should see topics starting with `spring-boot`, `micronaut`, `quarkus`, and `javalin` prefixes. 
Feel free to inspect them (they will be empty at this point).

### Testing

Testing is fairly easy, as it only requires an HTTP client, like curl or postman. Each of the applications 
exposes HTTP POST endpoint `/order/{count}`, which generates `{count}` random Buy and Sell Orders.

Here are example curls to generate 1000 random Buy and Sell Orders in each of the applications:

| Application | curl                                          |
|-------------|-----------------------------------------------|
| Spring Boot | curl -X POST http://localhost:8081/order/1000 |
| Micronaut   | curl -X POST http://localhost:8082/order/1000 |
| Quarkus     | curl -X POST http://localhost:8083/order/1000 |
| Javalin     | curl -X POST http://localhost:8084/order/1000 |

You can actually generate hundreds of millions of Orders, and everything should be fine, as long as you have sufficient 
disk space.

After generating some Orders head to Kouncil ([http://localhost:8080](http://localhost:8080)) and inspect input 
topics of one of the applications, let's say `quarkus-sell-orders` and `quarkus-buy-orders`. You should see some random 
Orders. From there, head to corresponding transactions topic (`quarkus-transactions` in this case), and you should 
expect some Transaction objects to be there, as a result of stream join.

While the applications are running, you can monitor resource consumption using Netdata, which should be running at 
[http://localhost:19999](http://localhost:19999).

### Shutting down

To shut down and clean everything up, simply type

    docker-compose down
