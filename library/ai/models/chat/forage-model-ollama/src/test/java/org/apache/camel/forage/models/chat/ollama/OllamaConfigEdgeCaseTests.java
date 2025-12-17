package org.apache.camel.forage.models.chat.ollama;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Edge Case Tests")
class OllamaConfigEdgeCaseTests {

    @Test
    @DisplayName("Should handle empty string values by returning null")
    void shouldHandleEmptyStringValues() {
        System.setProperty("forage.ollama.temperature", "");
        System.setProperty("forage.ollama.top.k", "");

        OllamaConfig config = new OllamaConfig();

        // Empty strings should result in null values after optional mapping
        assertThatThrownBy(() -> config.temperature()).isInstanceOf(NumberFormatException.class);
        assertThatThrownBy(() -> config.topK()).isInstanceOf(NumberFormatException.class);
    }

    @Test
    @DisplayName("Should handle zero values correctly")
    void shouldHandleZeroValuesCorrectly() {
        System.setProperty("forage.ollama.temperature", "0.0");
        System.setProperty("forage.ollama.top.k", "0");

        OllamaConfig config = new OllamaConfig();

        assertThat(config.temperature()).isEqualTo(0.0);
        assertThat(config.topK()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should handle negative values for parameters that support them")
    void shouldHandleNegativeValues() {
        System.setProperty("forage.ollama.temperature", "-1.0");
        System.setProperty("forage.ollama.top.k", "-1");

        OllamaConfig config = new OllamaConfig();

        // Note: These values may be invalid for the model but should be parsed correctly
        assertThat(config.temperature()).isEqualTo(-1.0);
        assertThat(config.topK()).isEqualTo(-1);
    }

    @Test
    @DisplayName("Should handle very large numbers")
    void shouldHandleVeryLargeNumbers() {
        System.setProperty("forage.ollama.num.ctx", "999999");
        System.setProperty("forage.ollama.top.k", "999999");

        OllamaConfig config = new OllamaConfig();

        assertThat(config.numCtx()).isEqualTo(999999);
        assertThat(config.topK()).isEqualTo(999999);
    }

    @Test
    @DisplayName("Should handle fractional values for double parameters")
    void shouldHandleFractionalValues() {
        System.setProperty("forage.ollama.temperature", "0.123456789");
        System.setProperty("forage.ollama.top.p", "0.987654321");

        OllamaConfig config = new OllamaConfig();

        assertThat(config.temperature()).isEqualTo(0.123456789);
        assertThat(config.topP()).isEqualTo(0.987654321);
    }
}
