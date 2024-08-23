package com.improving.lineage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class KafkaConsumerService {

    @Autowired
    LineageService lineageService;

    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics = "com.weather.source")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
        // Process the message
        String processedMessage = processMessage(message);
        // Produce the processed message to another topic
        produce(processedMessage);
        lineageService.reportLineage();

    }

    private String processMessage(String message) {
        try {
            var loc = objectMapper.readValue(message, Location.class);
            var rand = new Random();
            int temp = rand.nextInt(100);
            var prepped = new LocationPrepped(loc.city(), loc.state(), temp);
            return objectMapper.writeValueAsString(prepped);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private KafkaProducerService producerService;

    public void produce(String message) {
        producerService.sendMessage("com.weather.predict", message);
    }
}

