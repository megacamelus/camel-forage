package io.kaoto.forage.guardrails.output;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for OutputLengthGuardrailConfig.
 */
@DisplayName("OutputLengthGuardrailConfig Tests")
class OutputLengthGuardrailConfigTest {

    @Nested
    @DisplayName("Named Configuration Tests")
    class NamedConfigurationTests {

        @Test
        @DisplayName("Should use prefixed system property for maxChars")
        void shouldUsePrefixedSystemPropertyForMaxChars() {
            String prefix = "outputlentest";
            System.setProperty("forage." + prefix + ".guardrail.output.length.max.chars", "30000");

            try {
                OutputLengthGuardrailConfig config = new OutputLengthGuardrailConfig(prefix);
                assertThat(config.maxChars()).isEqualTo(30000);
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.output.length.max.chars");
            }
        }

        @Test
        @DisplayName("Should use prefixed system property for minChars")
        void shouldUsePrefixedSystemPropertyForMinChars() {
            String prefix = "outputlentest2";
            System.setProperty("forage." + prefix + ".guardrail.output.length.min.chars", "100");

            try {
                OutputLengthGuardrailConfig config = new OutputLengthGuardrailConfig(prefix);
                assertThat(config.minChars()).isEqualTo(100);
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.output.length.min.chars");
            }
        }

        @Test
        @DisplayName("Should use prefixed system property for truncateOnOverflow")
        void shouldUsePrefixedSystemPropertyForTruncateOnOverflow() {
            String prefix = "outputlentest3";
            System.setProperty("forage." + prefix + ".guardrail.output.length.truncate.on.overflow", "true");

            try {
                OutputLengthGuardrailConfig config = new OutputLengthGuardrailConfig(prefix);
                assertThat(config.truncateOnOverflow()).isTrue();
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.output.length.truncate.on.overflow");
            }
        }
    }

    @Nested
    @DisplayName("Config Interface Implementation Tests")
    class ConfigInterfaceTests {

        @Test
        @DisplayName("Should return correct name")
        void shouldReturnCorrectName() {
            OutputLengthGuardrailConfig config = new OutputLengthGuardrailConfig();

            assertThat(config.name()).isEqualTo("forage-guardrail-output-length");
        }

        @Test
        @DisplayName("Should create config without prefix")
        void shouldCreateConfigWithoutPrefix() {
            OutputLengthGuardrailConfig config = new OutputLengthGuardrailConfig();

            assertThat(config).isNotNull();
        }
    }
}
