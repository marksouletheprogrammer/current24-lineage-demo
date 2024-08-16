package com.improving.lineage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    LineageService lineageService;

    @KafkaListener(topics = "com.weather.enriched")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
        // Process the message
        String processedMessage = processMessage(message);
        // Produce the processed message to another topic
        produce(processedMessage);
        lineageService.reportLineage();

    }

    private String processMessage(String message) {
        return message;
    }

    @Autowired
    private KafkaProducerService producerService;

    public void produce(String message) {
        producerService.sendMessage("com.weather.prediction", message);
    }
}

