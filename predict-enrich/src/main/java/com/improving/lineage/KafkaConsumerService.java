package com.improving.lineage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Random;
import java.util.UUID;

@Service
public class KafkaConsumerService {

    @Autowired
    LineageService lineageService;

    @Autowired
    ObjectMapper objectMapper;

    private final static String TRACE_ID = "traceId";

    @KafkaListener(topics = "com.weather.predict")
    public void consume(ConsumerRecord<String, String> record) {
        System.out.println("Consumed message: " + record.value());
        // Process the message
        var traceId = Optional.ofNullable(record.headers().lastHeader(TRACE_ID))
                .map(Header::value)
                .map(String::new)
                .map(UUID::fromString)
                .orElseGet(UUID::randomUUID);
        String processedMessage = processMessage(record.value());
        // Produce the processed message to another topic
        produce(processedMessage, traceId);
        lineageService.reportLineage(traceId, record.partition(), record.offset());

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

    public void produce(String message, UUID traceId) {
        producerService.sendMessage("com.weather.enriched", message, traceId);
    }
}

