package org.kafka.demo.producer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.kafka.demo.constant.KafkaConstants;

/**
 * Kafka Producer Component
 * 
 * Handles sending messages to Kafka topics using Spring Boot's KafkaTemplate.
 * This component provides a clean abstraction for producing messages to Kafka
 * with proper error handling and logging.
 */
@Slf4j
@Component
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    /**
     * Constructor for dependency injection of KafkaTemplate
     * 
     * @param kafkaTemplate Spring Boot provided KafkaTemplate
     */
    public KafkaProducer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /**
     * Send a message to the specified Kafka topic
     * 
     * @param topicName the topic to send the message to
     * @param message the message content
     */
    public void sendMessage(String topicName, String message) {
        log.info("Sending message to topic: {}, message: {}", topicName, message);
        
        try {
            Message<String> kafkaMessage = MessageBuilder
                    .withPayload(message)
                    .setHeader(KafkaHeaders.TOPIC, topicName)
                    .build();
            
            kafkaTemplate.send(kafkaMessage);
            log.info("Message sent successfully to topic: {}", topicName);
        } catch (Exception e) {
            log.error("Error sending message to topic: {}, error: {}", topicName, e.getMessage());
            throw new RuntimeException("Failed to send message to Kafka", e);
        }
    }

    /**
     * Send a message with a key to the specified Kafka topic
     * Messages with the same key will be sent to the same partition
     * 
     * @param topicName the topic to send the message to
     * @param key the message key (for partitioning)
     * @param message the message content
     */
    public void sendMessageWithKey(String topicName, String key, String message) {
        log.info("Sending message with key: {} to topic: {}, message: {}", key, topicName, message);
        
        try {
            Message<String> kafkaMessage = MessageBuilder
                    .withPayload(message)
                    .setHeader(KafkaHeaders.TOPIC, topicName)
                    .setHeader(KafkaHeaders.MESSAGE_KEY, key)
                    .build();
            
            kafkaTemplate.send(kafkaMessage);
            log.info("Message with key: {} sent successfully to topic: {}", key, topicName);
        } catch (Exception e) {
            log.error("Error sending message with key: {} to topic: {}, error: {}", key, topicName, e.getMessage());
            throw new RuntimeException("Failed to send message with key to Kafka", e);
        }
    }

    /**
     * Send a batch of messages to a topic
     * 
     * @param topicName the topic to send messages to
     * @param messages the list of messages to send
     */
    public void sendBatchMessages(String topicName, java.util.List<String> messages) {
        log.info("Sending {} messages to topic: {}", messages.size(), topicName);
        
        messages.forEach(message -> {
            try {
                sendMessage(topicName, message);
            } catch (Exception e) {
                log.error("Error in batch send for message: {}", message, e);
            }
        });
        
        log.info("Batch message sending completed for topic: {}", topicName);
    }
}

