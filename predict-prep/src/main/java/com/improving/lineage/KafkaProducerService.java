package com.improving.lineage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    private final static String TRACE_ID = "traceId";

    public void sendMessage(String topic, String message, UUID traceId) {
        Message<String> kafkaMessage = MessageBuilder
                .withPayload(message)
                .setHeader(KafkaHeaders.TOPIC, topic)
                .setHeader(TRACE_ID, traceId.toString())
                .build();
        kafkaTemplate.send(kafkaMessage);
        System.out.println("Produced message: " + message);
    }
}
