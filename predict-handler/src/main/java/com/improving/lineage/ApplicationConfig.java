package com.improving.lineage;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "app")
public class ApplicationConfig {

    private String inputTopic;

    private String lineageProducer;

    private String lineageBackendUrl;

    public String getInputTopic() {
        return inputTopic;
    }

    public void setInputTopic(String inputTopic) {
        this.inputTopic = inputTopic;
    }

    public String getLineageProducer() {
        return lineageProducer;
    }

    public void setLineageProducer(String lineageProducer) {
        this.lineageProducer = lineageProducer;
    }

    public String getLineageBackendUrl() {
        return lineageBackendUrl;
    }

    public void setLineageBackendUrl(String lineageBackendUrl) {
        this.lineageBackendUrl = lineageBackendUrl;
    }
}
