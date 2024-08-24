package com.improving.lineage;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.openlineage.client.OpenLineage;
import io.openlineage.client.OpenLineageClient;
import io.openlineage.client.transports.HttpConfig;
import io.openlineage.client.transports.HttpTransport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.URI;

@Configuration
public class OpenLineageConfig {

    @Bean
    public OpenLineage openLineage(ApplicationConfig config) {
        return new OpenLineage(URI.create(config.getLineageProducer()));
    }

    @Bean
    public OpenLineageClient openLineageClient(ApplicationConfig config) {
        HttpConfig httpConfig = new HttpConfig();
        httpConfig.setUrl(URI.create(config.getLineageBackendUrl()));
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
