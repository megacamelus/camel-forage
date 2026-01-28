package io.kaoto.forage.guardrails.output;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.camel.component.langchain4j.agent.api.guardrails.SensitiveDataOutputGuardrail.Action;
import org.apache.camel.component.langchain4j.agent.api.guardrails.SensitiveDataOutputGuardrail.SensitiveDataType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for SensitiveDataGuardrailConfig.
 */
@DisplayName("SensitiveDataGuardrailConfig Tests")
class SensitiveDataGuardrailConfigTest {

    @Nested
    @DisplayName("Named Configuration Tests")
    class NamedConfigurationTests {

        @Test
        @DisplayName("Should use prefixed system property for action")
        void shouldUsePrefixedSystemPropertyForAction() {
            String prefix = "sensitivetest";
            System.setProperty("forage." + prefix + ".guardrail.sensitive.data.action", "WARN");

            try {
                SensitiveDataGuardrailConfig config = new SensitiveDataGuardrailConfig(prefix);
                assertThat(config.action()).isEqualTo(Action.WARN);
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.sensitive.data.action");
            }
        }

        @Test
        @DisplayName("Should use prefixed system property for detectTypes")
        void shouldUsePrefixedSystemPropertyForDetectTypes() {
            String prefix = "sensitivetest2";
            System.setProperty("forage." + prefix + ".guardrail.sensitive.data.detect.types", "API_KEY,SSN");

            try {
                SensitiveDataGuardrailConfig config = new SensitiveDataGuardrailConfig(prefix);
                assertThat(config.detectTypes()).hasSize(2);
                assertThat(config.detectTypes()).contains(SensitiveDataType.API_KEY);
                assertThat(config.detectTypes()).contains(SensitiveDataType.SSN);
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.sensitive.data.detect.types");
            }
        }

        @Test
        @DisplayName("Should use prefixed system property for redactionText")
        void shouldUsePrefixedSystemPropertyForRedactionText() {
            String prefix = "sensitivetest3";
            System.setProperty("forage." + prefix + ".guardrail.sensitive.data.redaction.text", "[HIDDEN]");

            try {
                SensitiveDataGuardrailConfig config = new SensitiveDataGuardrailConfig(prefix);
                assertThat(config.redactionText()).isEqualTo("[HIDDEN]");
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.sensitive.data.redaction.text");
            }
        }
    }

    @Nested
    @DisplayName("Config Interface Implementation Tests")
    class ConfigInterfaceTests {

        @Test
        @DisplayName("Should return correct name")
        void shouldReturnCorrectName() {
            SensitiveDataGuardrailConfig config = new SensitiveDataGuardrailConfig();

            assertThat(config.name()).isEqualTo("forage-guardrail-sensitive-data");
        }

        @Test
        @DisplayName("Should create config without prefix")
        void shouldCreateConfigWithoutPrefix() {
            SensitiveDataGuardrailConfig config = new SensitiveDataGuardrailConfig();

            assertThat(config).isNotNull();
        }
    }

    @Nested
    @DisplayName("Invalid Value Handling Tests")
    class InvalidValueHandlingTests {

        @Test
        @DisplayName("Should fallback to BLOCK for invalid action")
        void shouldFallbackToBlockForInvalidAction() {
            String prefix = "sensitivetest4";
            System.setProperty("forage." + prefix + ".guardrail.sensitive.data.action", "INVALID_ACTION");

            try {
                SensitiveDataGuardrailConfig config = new SensitiveDataGuardrailConfig(prefix);
                assertThat(config.action()).isEqualTo(Action.BLOCK);
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.sensitive.data.action");
            }
        }

        @Test
        @DisplayName("Should ignore invalid detect types")
        void shouldIgnoreInvalidDetectTypes() {
            String prefix = "sensitivetest5";
            System.setProperty(
                    "forage." + prefix + ".guardrail.sensitive.data.detect.types", "API_KEY,INVALID_TYPE,SSN");

            try {
                SensitiveDataGuardrailConfig config = new SensitiveDataGuardrailConfig(prefix);
                assertThat(config.detectTypes()).hasSize(2);
                assertThat(config.detectTypes()).contains(SensitiveDataType.API_KEY);
                assertThat(config.detectTypes()).contains(SensitiveDataType.SSN);
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.sensitive.data.detect.types");
            }
        }
    }
}
