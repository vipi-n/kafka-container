package org.kafka.demo.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.kafka.demo.constant.KafkaConstants;

/**
 * Kafka Consumer Component
 * 
 * Listens to messages from Kafka topics using Spring Boot's @KafkaListener annotation.
 * This provides a declarative and clean way to consume messages with automatic
 * serialization/deserialization and error handling.
 */
@Slf4j
@Component
public class KafkaConsumer {

    /**
     * Listen to messages from the test-topic
     * 
     * This method is automatically invoked whenever a message arrives on the topic.
     * The message is automatically deserialized from JSON to String.
     * 
     * @param message the message content
     * @param partition the partition from which the message was received
     * @param offset the offset of the message
     */
    @KafkaListener(
        topics = KafkaConstants.TOPIC_NAME,
        groupId = KafkaConstants.GROUP_ID_CONFIG
    )
    public void consume(
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received message from topic: {}", KafkaConstants.TOPIC_NAME);
        log.info("Message: {}", message);
        log.info("Partition: {}", partition);
        log.info("Offset: {}", offset);
    }

    /**
     * Alternative consumer method that receives the raw message with key
     * 
     * This method demonstrates consuming messages with keys, useful when messages
     * need to maintain ordering per key.
     * 
     * @param key the message key
     * @param message the message content
     * @param partition the partition from which the message was received
     * @param offset the offset of the message
     */
    @KafkaListener(
        topics = KafkaConstants.TOPIC_NAME,
        groupId = "consumerGroup-with-key"
    )
    public void consumeWithKey(
            @Header(KafkaHeaders.RECEIVED_MESSAGE_KEY) String key,
            @Payload String message,
            @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        log.info("Received message with key: {} from topic: {}", key, KafkaConstants.TOPIC_NAME);
        log.info("Message: {}", message);
        log.info("Partition: {}, Offset: {}", partition, offset);
    }

    /**
     * Error handler for this consumer
     * 
     * This method handles any exceptions that occur during message consumption
     * 
     * @param message the message that caused the error
     * @param exception the exception that was thrown
     */
    public void handleError(String message, Exception exception) {
        log.error("Error consuming message: {}, Exception: {}", message, exception.getMessage());
    }
}
