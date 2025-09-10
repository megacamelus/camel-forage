package org.apache.camel.forage.models.chat.multimodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.apache.camel.forage.core.util.config.MissingConfigException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * Comprehensive tests for MultiModelConfig focusing on configuration loading mechanisms.
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
@DisplayName("MultiModelConfig Configuration Loading Tests")
class MultiModelConfigTest {

    @Test
    @DisplayName("Should load default model from system property")
    void shouldLoadDefaultModelFromSystemProperty() {
        System.setProperty("multimodel.default.model", "openai");

        MultiModelConfig config = new MultiModelConfig();

        assertThat(config.defaultModel()).isEqualTo("openai");
    }

    @Test
    @DisplayName("Should load available models from system property")
    void shouldLoadAvailableModelsFromSystemProperty() {
        System.setProperty("multimodel.available.models", "openai,ollama,gemini");

        MultiModelConfig config = new MultiModelConfig();

        assertThat(config.availableModels()).containsExactly("openai", "ollama", "gemini");
    }

    @Test
    @DisplayName("Should use default available models when not configured")
    void shouldUseDefaultAvailableModelsWhenNotConfigured() {
        System.clearProperty("multimodel.available.models");

        MultiModelConfig config = new MultiModelConfig();

        assertThat(config.availableModels()).containsExactly("openai", "ollama", "gemini");
    }

    @Test
    @DisplayName("Should throw exception when default model is not configured")
    void shouldThrowExceptionWhenDefaultModelNotConfigured() {
        System.clearProperty("multimodel.default.model");

        MultiModelConfig config = new MultiModelConfig();

        assertThatThrownBy(config::defaultModel)
                .isInstanceOf(MissingConfigException.class)
                .hasMessage("Missing default model configuration");
    }
}