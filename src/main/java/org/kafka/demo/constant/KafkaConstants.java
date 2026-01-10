package org.kafka.demo.constant;

/**
 * Kafka Configuration Constants
 * 
 * This class holds all Kafka-related configuration constants used throughout the application.
 * These values can be overridden via Spring Boot application.yml configuration file.
 */
public class KafkaConstants {

    // Kafka Broker Configuration
    public static final String KAFKA_BROKERS = "localhost:9092";
    
    // Topic Configuration
    public static final String TOPIC_NAME = "test-topic";
    
    // Producer Configuration
    public static final String CLIENT_ID = "client1";
    public static final int MESSAGE_COUNT = 1000;
    
    // Consumer Configuration
    public static final String GROUP_ID_CONFIG = "consumerGroup10";
    public static final String OFFSET_RESET_EARLIEST = "earliest";
    public static final String OFFSET_RESET_LATEST = "latest";
    public static final int MAX_POLL_RECORDS = 1;
    
    // Consumer Polling Configuration
    public static final int MAX_NO_MESSAGE_FOUND_COUNT = 100;
    public static final long POLL_TIMEOUT_MILLISECONDS = 1000;
    
    private KafkaConstants() {
        // Private constructor to prevent instantiation
        throw new AssertionError("Cannot instantiate constants class");
    }
}

