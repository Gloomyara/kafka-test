package ru.antonovmikhail.shipment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class ShipmentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShipmentServiceApplication.class, args);
	}

}
