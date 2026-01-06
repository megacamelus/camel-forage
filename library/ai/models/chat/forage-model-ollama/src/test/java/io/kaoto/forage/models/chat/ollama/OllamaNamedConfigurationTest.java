package io.kaoto.forage.models.chat.ollama;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import io.kaoto.forage.core.util.config.ConfigStore;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Tests for OllamaConfig focusing on named/prefixed configuration functionality.
 *
 * <p>This test class verifies:
 * <ul>
 *   <li>Default vs named configuration construction</li>
 *   <li>Prefix resolution for different configuration sources</li>
 *   <li>Isolation between multiple named instances</li>
 *   <li>Mixed configuration scenarios</li>
 *   <li>Edge cases with prefixes</li>
 * </ul>
 */
@DisplayName("OllamaConfig Named/Prefixed Configuration Tests")
class OllamaNamedConfigurationTest {

    @BeforeEach
    void setUp() {
        ConfigStore.getInstance().setClassLoader(getClass().getClassLoader());
    }

    @Test
    @DisplayName("Should handle unnamed configurations only")
    void handledUnnamedOnly() throws IOException {
        OllamaConfig config = new OllamaConfig();

        assertThat(config.baseUrl()).isEqualTo("http://config-file-server:11434");
        assertThat(config.modelName()).isEqualTo("config-file-model");
        assertThat(config.temperature()).isEqualTo(0.75);
        assertThat(config.topK()).isEqualTo(30);
        assertThat(config.minP()).isEqualTo(0.05);
        assertThat(config.numCtx()).isEqualTo(1024);
        assertThat(config.logRequests()).isTrue();
        assertThat(config.logResponses()).isFalse();
    }

    @Test
    @DisplayName("Should handle named configurations only")
    void handledNamedOnly() throws IOException {
        OllamaConfig config = new OllamaConfig("instance1");

        assertThat(config.baseUrl()).isEqualTo("http://instance1-server:11434");
        assertThat(config.modelName()).isEqualTo("instance1-model");
        assertThat(config.temperature()).isEqualTo(0.6);
        assertThat(config.topK()).isNull();
        assertThat(config.minP()).isNull();
        assertThat(config.numCtx()).isNull();
        assertThat(config.topK()).isNull();
    }
}
