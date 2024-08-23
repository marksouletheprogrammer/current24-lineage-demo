package com.improving.lineage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @Autowired
    LineageService lineageService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PredictionRepository repository;

    @KafkaListener(topics = "com.weather.enriched")
    public void consume(String message) {
        System.out.println("Consumed message: " + message);
        // Process the message
        processMessage(message);
        lineageService.reportLineage();

    }

    private void processMessage(String message) {
        try {
            var prediction = objectMapper.readValue(message, LocationWeatherPrediction.class);
            repository.save(prediction);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}

