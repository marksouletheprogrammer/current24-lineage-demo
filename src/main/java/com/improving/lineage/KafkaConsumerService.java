package com.improving.lineage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "input_topic", groupId = "your-consumer-group")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
        // Process the message
        String processedMessage = processMessage(message);
        // Produce the processed message to another topic
        produce(processedMessage);
    }

    private String processMessage(String message) {
        return message;
    }

    @Autowired
    private KafkaProducerService producerService;

    public void produce(String message) {
        producerService.sendMessage("output_topic", message);
    }
}

