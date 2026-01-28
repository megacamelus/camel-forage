package io.kaoto.forage.guardrails.input;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for InputLengthGuardrailConfig.
 */
@DisplayName("InputLengthGuardrailConfig Tests")
class InputLengthGuardrailConfigTest {

    @Nested
    @DisplayName("Named Configuration Tests")
    class NamedConfigurationTests {

        @Test
        @DisplayName("Should use prefixed system property for named config")
        void shouldUsePrefixedSystemPropertyForNamedConfig() {
            String prefix = "inputlentest";
            System.setProperty("forage." + prefix + ".guardrail.input.length.max.chars", "8000");

            try {
                InputLengthGuardrailConfig config = new InputLengthGuardrailConfig(prefix);
                assertThat(config.maxChars()).isEqualTo(8000);
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.input.length.max.chars");
            }
        }

        @Test
        @DisplayName("Should use prefixed system property for minChars")
        void shouldUsePrefixedSystemPropertyForMinChars() {
            String prefix = "inputlentest2";
            System.setProperty("forage." + prefix + ".guardrail.input.length.min.chars", "50");

            try {
                InputLengthGuardrailConfig config = new InputLengthGuardrailConfig(prefix);
                assertThat(config.minChars()).isEqualTo(50);
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.input.length.min.chars");
            }
        }
    }

    @Nested
    @DisplayName("Config Interface Implementation Tests")
    class ConfigInterfaceTests {

        @Test
        @DisplayName("Should return correct name")
        void shouldReturnCorrectName() {
            InputLengthGuardrailConfig config = new InputLengthGuardrailConfig();

            assertThat(config.name()).isEqualTo("forage-guardrail-input-length");
        }

        @Test
        @DisplayName("Should create config without prefix")
        void shouldCreateConfigWithoutPrefix() {
            InputLengthGuardrailConfig config = new InputLengthGuardrailConfig();

            assertThat(config).isNotNull();
        }
    }
}
