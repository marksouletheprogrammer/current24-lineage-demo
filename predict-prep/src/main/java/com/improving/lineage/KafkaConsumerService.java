package com.improving.lineage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class KafkaConsumerService {

    @Autowired
    LineageService lineageService;

    @Autowired
    ObjectMapper objectMapper;

    @KafkaListener(topics = "com.weather.source")
    public void consume(ConsumerRecord<String, String> record) {
        System.out.println("Consumed message: " + record.value());
        // Process the message
        var traceId = UUID.randomUUID();
        String processedMessage = processMessage(record.value());
        // Produce the processed message to another topic
        produce(processedMessage, traceId);
        lineageService.reportLineage(traceId, record.partition(), record.offset());

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

    public void produce(String message, UUID traceId) {
        producerService.sendMessage("com.weather.predict", message, traceId);
    }
}

