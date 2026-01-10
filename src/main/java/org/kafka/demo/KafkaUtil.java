package org.kafka.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * Kafka Spring Boot Application
 * 
 * This is the main entry point for the Spring Boot application that demonstrates
 * Kafka producer and consumer patterns with Spring Boot integration.
 * 
 * The application automatically configures Kafka connections based on application.yml
 * and provides REST endpoints for sending and receiving messages.
 */
@Slf4j
@SpringBootApplication
@ComponentScan(basePackages = {
    "org.kafka.demo.producer",
    "org.kafka.demo.consumer",
    "org.kafka.demo.controller"
})
public class KafkaUtil {

    /**
     * Main method to start the Spring Boot application
     * 
     * @param args command line arguments
     */
    public static void main(String[] args) {
        log.info("Starting Kafka Spring Boot Application...");
        SpringApplication.run(KafkaUtil.class, args);
        log.info("Kafka Spring Boot Application started successfully");
    }
}
