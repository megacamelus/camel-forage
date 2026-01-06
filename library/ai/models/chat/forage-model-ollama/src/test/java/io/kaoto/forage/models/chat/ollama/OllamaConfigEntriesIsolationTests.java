package io.kaoto.forage.models.chat.ollama;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Integration Tests")
class OllamaConfigEntriesIsolationTests {

    @Test
    @DisplayName("Should work end-to-end with configuration loading")
    void shouldWorkEndToEndWithConfigurationLoading() {
        // Register with a specific prefix
        String prefix = "integration";
        OllamaConfigEntries.register(prefix);

        // Set up configuration values
        System.setProperty("forage." + prefix + ".ollama.base.url", "http://integration-test:11434");
        System.setProperty("forage." + prefix + ".ollama.model.name", "integration-model");
        System.setProperty("forage." + prefix + ".ollama.temperature", "0.5");
        System.setProperty("forage." + prefix + ".ollama.top.k", "25");

        // Create configuration and verify all values
        OllamaConfig config = new OllamaConfig(prefix);

        assertThat(config.baseUrl()).isEqualTo("http://integration-test:11434");
        assertThat(config.modelName()).isEqualTo("integration-model");
        assertThat(config.temperature()).isEqualTo(0.5);
        assertThat(config.topK()).isEqualTo(25);
    }

    @Test
    @DisplayName("Should maintain isolation between different configuration instances")
    void shouldMaintainIsolationBetweenDifferentConfigurationInstances() {
        // Set up different configurations
        System.setProperty("forage.instance1.ollama.base.url", "http://instance1:11434");
        System.setProperty("forage.instance2.ollama.base.url", "http://instance2:11434");
        System.setProperty("forage.ollama.base.url", "http://default:11434");

        OllamaConfig config1 = new OllamaConfig("instance1");
        OllamaConfig config2 = new OllamaConfig("instance2");
        OllamaConfig defaultConfig = new OllamaConfig();

        assertThat(config1.baseUrl()).isEqualTo("http://instance1:11434");
        assertThat(config2.baseUrl()).isEqualTo("http://instance2:11434");
        assertThat(defaultConfig.baseUrl()).isEqualTo("http://default:11434");
    }
}
