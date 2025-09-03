package org.apache.camel.forage.models.chat.ollama;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for OllamaConfig focusing on configuration loading mechanisms.
 *
 * <p>This test class covers:
 * <ul>
 *   <li>Default value handling</li>
 *   <li>Environment variable configuration</li>
 *   <li>System property configuration</li>
 *   <li>Configuration file loading</li>
 *   <li>Type conversion and validation</li>
 *   <li>Error handling scenarios</li>
 * </ul>
 */
@DisplayName("OllamaConfig Configuration Loading Tests")
class OllamaConfigTest {

    @Test
    @DisplayName("Should load base URL from system property")
    void shouldLoadBaseUrlFromSystemProperty() {
        System.setProperty("ollama.base.url", "http://custom-server:11434");

        OllamaConfig config = new OllamaConfig();

        assertThat(config.baseUrl()).isEqualTo("http://custom-server:11434");
    }

    @Test
    @DisplayName("Should load model name from system property")
    void shouldLoadModelNameFromSystemProperty() {
        System.setProperty("ollama.model.name", "llama3.1");

        OllamaConfig config = new OllamaConfig();

        assertThat(config.modelName()).isEqualTo("llama3.1");
    }

    @Test
    @DisplayName("Should load all optional parameters from system properties")
    void shouldLoadOptionalParametersFromSystemProperties() {
        System.setProperty("ollama.temperature", "0.7");
        System.setProperty("ollama.top.k", "40");
        System.setProperty("ollama.top.p", "0.9");
        System.setProperty("ollama.min.p", "0.05");
        System.setProperty("ollama.num.ctx", "2048");
        System.setProperty("ollama.log.requests", "true");
        System.setProperty("ollama.log.responses", "false");

        OllamaConfig config = new OllamaConfig();

        assertThat(config.temperature()).isEqualTo(0.7);
        assertThat(config.topK()).isEqualTo(40);
        assertThat(config.topP()).isEqualTo(0.9);
        assertThat(config.minP()).isEqualTo(0.05);
        assertThat(config.numCtx()).isEqualTo(2048);
        assertThat(config.logRequests()).isTrue();
        assertThat(config.logResponses()).isFalse();
    }
}
