package org.kafka.demo.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.kafka.demo.constant.KafkaConstants;
import org.kafka.demo.dto.MessageRequest;
import org.kafka.demo.producer.KafkaProducer;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for Kafka Operations
 * 
 * This controller provides REST API endpoints for sending messages to Kafka topics.
 * It serves as the interface between HTTP clients and the Kafka producer.
 */
@Slf4j
@RestController
@RequestMapping("/api/kafka")
@CrossOrigin(origins = "*")
public class MessageController {

    private final KafkaProducer kafkaProducer;

    /**
     * Constructor for dependency injection
     * 
     * @param kafkaProducer the Kafka producer component
     */
    @Autowired
    public MessageController(KafkaProducer kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    /**
     * Health check endpoint to verify the API is running
     * 
     * @return response with status
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        log.info("Health check endpoint called");
        Map<String, String> response = new HashMap<>();
        response.put("status", "UP");
        response.put("message", "Kafka API is running");
        return ResponseEntity.ok(response);
    }

    /**
     * Send a message to the default topic
     * 
     * @param request the message request containing the message content
     * @return response with success status
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, String>> sendMessage(@RequestBody MessageRequest request) {
        log.info("Send message request received: {}", request.getMessage());
        
        try {
            kafkaProducer.sendMessage(KafkaConstants.TOPIC_NAME, request.getMessage());
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Message sent successfully to topic: " + KafkaConstants.TOPIC_NAME);
            response.put("topic", KafkaConstants.TOPIC_NAME);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending message", e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "FAILED");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Send a message with a key to the default topic
     * Messages with the same key will be directed to the same partition
     * 
     * @param request the message request containing key and message
     * @return response with success status
     */
    @PostMapping("/send-with-key")
    public ResponseEntity<Map<String, String>> sendMessageWithKey(@RequestBody MessageRequest request) {
        log.info("Send message with key request - Key: {}, Message: {}", request.getKey(), request.getMessage());
        
        try {
            kafkaProducer.sendMessageWithKey(
                KafkaConstants.TOPIC_NAME,
                request.getKey(),
                request.getMessage()
            );
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Message with key sent successfully");
            response.put("key", request.getKey());
            response.put("topic", KafkaConstants.TOPIC_NAME);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending message with key", e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "FAILED");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Send a message to a custom topic
     * 
     * @param topicName the name of the topic
     * @param request the message request
     * @return response with success status
     */
    @PostMapping("/send/{topicName}")
    public ResponseEntity<Map<String, String>> sendMessageToTopic(
            @PathVariable String topicName,
            @RequestBody MessageRequest request) {
        
        log.info("Send message to topic: {}, Message: {}", topicName, request.getMessage());
        
        try {
            kafkaProducer.sendMessage(topicName, request.getMessage());
            
            Map<String, String> response = new HashMap<>();
            response.put("status", "SUCCESS");
            response.put("message", "Message sent successfully to topic: " + topicName);
            response.put("topic", topicName);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending message to topic: {}", topicName, e);
            Map<String, String> response = new HashMap<>();
            response.put("status", "FAILED");
            response.put("error", e.getMessage());
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
