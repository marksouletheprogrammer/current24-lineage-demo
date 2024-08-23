package com.improving.lineage;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.openlineage.client.OpenLineage;
import io.openlineage.client.OpenLineageClient;
import io.openlineage.client.transports.HttpConfig;
import io.openlineage.client.transports.HttpTransport;
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
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.setUrl(URI.create("http://marquez-api:5000"));
        return OpenLineageClient.builder()
                .transport(
                        new HttpTransport(httpConfig)
                ).build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }
}
