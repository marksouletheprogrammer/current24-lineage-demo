package com.improving.lineage;

import io.openlineage.client.OpenLineage;
import io.openlineage.client.OpenLineageClient;
import io.openlineage.client.transports.KafkaConfig;
import io.openlineage.client.transports.KafkaTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.ProducerFactory;

import java.net.URI;
import java.util.Properties;

@Configuration
public class OpenLineageConfig {

    @Bean
    public OpenLineage openLineage() {
        return new OpenLineage(URI.create("ferret_producer"));
    }

    @Bean
    public OpenLineageClient openLineageClient(ProducerFactory<String, String> producerFactory) {
        Properties producerProps = new Properties();
        producerProps.putAll(producerFactory.getConfigurationProperties());
        return OpenLineageClient.builder()
                .transport(
                        new KafkaTransport(
                                new KafkaConfig(
                                        "lineage",
                                        "lineage_waffles",
                                        producerProps
                                )
                        )
                ).build();
    }
}
