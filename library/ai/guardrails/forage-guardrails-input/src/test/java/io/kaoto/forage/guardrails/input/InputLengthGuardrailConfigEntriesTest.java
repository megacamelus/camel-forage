package io.kaoto.forage.guardrails.input;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import io.kaoto.forage.core.util.config.ConfigEntry;
import io.kaoto.forage.core.util.config.ConfigModule;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for InputLengthGuardrailConfigEntries.
 */
@DisplayName("InputLengthGuardrailConfigEntries Tests")
class InputLengthGuardrailConfigEntriesTest {

    @Nested
    @DisplayName("Static ConfigModule Field Tests")
    class StaticConfigModuleTests {

        @Test
        @DisplayName("Should have correct ConfigModule names")
        void shouldHaveCorrectConfigModuleNames() {
            assertThat(InputLengthGuardrailConfigEntries.MAX_CHARS.name())
                    .isEqualTo("forage.guardrail.input.length.max.chars");
            assertThat(InputLengthGuardrailConfigEntries.MIN_CHARS.name())
                    .isEqualTo("forage.guardrail.input.length.min.chars");
        }

        @Test
        @DisplayName("Should have correct ConfigModule config class references")
        void shouldHaveCorrectConfigModuleConfigClassReferences() {
            assertThat(InputLengthGuardrailConfigEntries.MAX_CHARS.config())
                    .isEqualTo(InputLengthGuardrailConfig.class);
            assertThat(InputLengthGuardrailConfigEntries.MIN_CHARS.config())
                    .isEqualTo(InputLengthGuardrailConfig.class);
        }
    }

    @Nested
    @DisplayName("CONFIG_MODULES Map Tests")
    class ConfigModulesMapTests {

        @Test
        @DisplayName("Should return immutable map from entries()")
        void shouldReturnImmutableMapFromEntries() {
            Map<ConfigModule, ConfigEntry> entries = InputLengthGuardrailConfigEntries.entries();

            assertThat(entries).isNotNull();
            assertThat(entries).isNotEmpty();

            assertThatThrownBy(entries::clear).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Should contain all ConfigModules in entries map")
        void shouldContainAllConfigModulesInEntriesMap() {
            Map<ConfigModule, ConfigEntry> entries = InputLengthGuardrailConfigEntries.entries();

            assertThat(entries).containsKey(InputLengthGuardrailConfigEntries.MAX_CHARS);
            assertThat(entries).containsKey(InputLengthGuardrailConfigEntries.MIN_CHARS);
        }
    }

    @Nested
    @DisplayName("find() Method Tests")
    class FindMethodTests {

        @Test
        @DisplayName("Should find ConfigModule without prefix")
        void shouldFindConfigModuleWithoutPrefix() {
            Optional<ConfigModule> found =
                    InputLengthGuardrailConfigEntries.find(null, "forage.guardrail.input.length.max.chars");

            assertThat(found).isPresent();
            assertThat(found.get()).isEqualTo(InputLengthGuardrailConfigEntries.MAX_CHARS);
        }

        @Test
        @DisplayName("Should return Optional.empty for unknown configuration name")
        void shouldReturnEmptyForUnknownConfigurationName() {
            assertThat(InputLengthGuardrailConfigEntries.find(null, "unknown.config"))
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("register() Method Tests")
    class RegisterMethodTests {

        @Test
        @DisplayName("Should register configurations without prefix")
        void shouldRegisterConfigurationsWithoutPrefix() {
            InputLengthGuardrailConfigEntries.register(null);

            InputLengthGuardrailConfig config = new InputLengthGuardrailConfig();
            assertThat(config).isNotNull();
        }

        @Test
        @DisplayName("Should register configurations with prefix")
        void shouldRegisterConfigurationsWithPrefix() {
            String prefix = "testinputlength";

            InputLengthGuardrailConfigEntries.register(prefix);

            System.setProperty("forage." + prefix + ".guardrail.input.length.max.chars", "5000");

            try {
                InputLengthGuardrailConfig config = new InputLengthGuardrailConfig(prefix);
                assertThat(config.maxChars()).isEqualTo(5000);
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.input.length.max.chars");
            }
        }
    }
}
