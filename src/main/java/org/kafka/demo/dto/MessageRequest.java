package org.kafka.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Message Request DTO
 * 
 * This class represents the structure of a message request received via REST API.
 * It uses Lombok annotations to reduce boilerplate code for getters, setters, and constructors.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageRequest {
    
    /**
     * The message content to be sent to Kafka
     */
    private String message;
    
    /**
     * Optional key for the message
     * Messages with the same key will be sent to the same partition
     */
    private String key;
}
