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

    @KafkaListener(topics = "com.weather.predict")
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
            var prepped = objectMapper.readValue(message, LocationPrepped.class);
            var rand = new Random();
            int predictLow = rand.nextInt(80);
            int predictHigh = predictLow + rand.nextInt(30);
            var prediction = new LocationWeatherPrediction(
                    prepped.city(),
                    prepped.state(),
                    celsiusToKelvin(fahrenheitToCelsius(prepped.currentTemp())),
                    celsiusToKelvin(fahrenheitToCelsius(predictLow)),
                    celsiusToKelvin(fahrenheitToCelsius((predictHigh)))
            );
            return objectMapper.writeValueAsString(prediction);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to convert Fahrenheit to Celsius (as int).
    public int fahrenheitToCelsius(int fahrenheit) {
        return (fahrenheit - 32) * 5 / 9;
    }

    // Method to convert Celsius to Kelvin (as int).
    public int celsiusToKelvin(int celsius) {
        return celsius + 273;
    }
    @Autowired
    private KafkaProducerService producerService;

    public void produce(String message) {
        producerService.sendMessage("com.weather.enriched", message);
    }
}

