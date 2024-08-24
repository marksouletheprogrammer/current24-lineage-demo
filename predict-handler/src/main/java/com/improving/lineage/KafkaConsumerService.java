package com.improving.lineage;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.header.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class KafkaConsumerService {

    @Autowired
    LineageService lineageService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    PredictionRepository repository;

    @Autowired
    ApplicationConfig config;

    private final static String TRACE_ID = "traceId";

    @KafkaListener(topics = "${app.input-topic}")
    public void consume(ConsumerRecord<String, String> record) {
        System.out.println("Consumed message: " + record.value());
        // Process the message
        var traceId = Optional.ofNullable(record.headers().lastHeader(TRACE_ID))
                .map(Header::value)
                .map(String::new)
                .map(UUID::fromString)
                .orElseGet(UUID::randomUUID);
        processMessage(record.value());
        lineageService.reportLineage(traceId, record.partition(), record.offset());

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

