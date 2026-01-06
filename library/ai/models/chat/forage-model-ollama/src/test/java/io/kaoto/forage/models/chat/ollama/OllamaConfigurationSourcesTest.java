package io.kaoto.forage.models.chat.ollama;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import io.kaoto.forage.core.util.config.ConfigStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for OllamaConfig focusing on configuration source precedence.
 *
 * <p>This test class verifies the precedence order:
 * <ol>
 *   <li>Environment variables (highest precedence)</li>
 *   <li>System properties</li>
 *   <li>Configuration files</li>
 *   <li>Default values (lowest precedence)</li>
 * </ol>
 */
@DisplayName("OllamaConfig Configuration Source Precedence Tests")
class OllamaConfigurationSourcesTest {

    @BeforeEach
    void setUp() {
        ConfigStore.getInstance().setClassLoader(getClass().getClassLoader());
    }

    @Test
    @DisplayName("Should load from configuration file only")
    void shouldLoadFromConfigurationFileOnly() throws IOException {
        OllamaConfig config = new OllamaConfig();

        assertThat(config.baseUrl()).isEqualTo("http://config-file-server:11434");
        assertThat(config.modelName()).isEqualTo("config-file-model");
        assertThat(config.temperature()).isEqualTo(0.75);
        assertThat(config.topK()).isEqualTo(30);
        assertThat(config.logRequests()).isTrue();
        assertThat(config.logResponses()).isFalse();
    }
}
