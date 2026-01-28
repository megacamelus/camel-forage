package io.kaoto.forage.guardrails.output;

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
 * Tests for OutputLengthGuardrailConfigEntries.
 */
@DisplayName("OutputLengthGuardrailConfigEntries Tests")
class OutputLengthGuardrailConfigEntriesTest {

    @Nested
    @DisplayName("Static ConfigModule Field Tests")
    class StaticConfigModuleTests {

        @Test
        @DisplayName("Should have correct ConfigModule names")
        void shouldHaveCorrectConfigModuleNames() {
            assertThat(OutputLengthGuardrailConfigEntries.MAX_CHARS.name())
                    .isEqualTo("forage.guardrail.output.length.max.chars");
            assertThat(OutputLengthGuardrailConfigEntries.MIN_CHARS.name())
                    .isEqualTo("forage.guardrail.output.length.min.chars");
            assertThat(OutputLengthGuardrailConfigEntries.TRUNCATE_ON_OVERFLOW.name())
                    .isEqualTo("forage.guardrail.output.length.truncate.on.overflow");
        }

        @Test
        @DisplayName("Should have correct ConfigModule config class references")
        void shouldHaveCorrectConfigModuleConfigClassReferences() {
            assertThat(OutputLengthGuardrailConfigEntries.MAX_CHARS.config())
                    .isEqualTo(OutputLengthGuardrailConfig.class);
            assertThat(OutputLengthGuardrailConfigEntries.MIN_CHARS.config())
                    .isEqualTo(OutputLengthGuardrailConfig.class);
            assertThat(OutputLengthGuardrailConfigEntries.TRUNCATE_ON_OVERFLOW.config())
                    .isEqualTo(OutputLengthGuardrailConfig.class);
        }
    }

    @Nested
    @DisplayName("CONFIG_MODULES Map Tests")
    class ConfigModulesMapTests {

        @Test
        @DisplayName("Should return immutable map from entries()")
        void shouldReturnImmutableMapFromEntries() {
            Map<ConfigModule, ConfigEntry> entries = OutputLengthGuardrailConfigEntries.entries();

            assertThat(entries).isNotNull();
            assertThat(entries).isNotEmpty();

            assertThatThrownBy(entries::clear).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Should contain all ConfigModules in entries map")
        void shouldContainAllConfigModulesInEntriesMap() {
            Map<ConfigModule, ConfigEntry> entries = OutputLengthGuardrailConfigEntries.entries();

            assertThat(entries).containsKey(OutputLengthGuardrailConfigEntries.MAX_CHARS);
            assertThat(entries).containsKey(OutputLengthGuardrailConfigEntries.MIN_CHARS);
            assertThat(entries).containsKey(OutputLengthGuardrailConfigEntries.TRUNCATE_ON_OVERFLOW);
        }
    }

    @Nested
    @DisplayName("find() Method Tests")
    class FindMethodTests {

        @Test
        @DisplayName("Should find ConfigModule without prefix")
        void shouldFindConfigModuleWithoutPrefix() {
            Optional<ConfigModule> found =
                    OutputLengthGuardrailConfigEntries.find(null, "forage.guardrail.output.length.max.chars");

            assertThat(found).isPresent();
            assertThat(found.get()).isEqualTo(OutputLengthGuardrailConfigEntries.MAX_CHARS);
        }

        @Test
        @DisplayName("Should return Optional.empty for unknown configuration name")
        void shouldReturnEmptyForUnknownConfigurationName() {
            assertThat(OutputLengthGuardrailConfigEntries.find(null, "unknown.config"))
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("register() Method Tests")
    class RegisterMethodTests {

        @Test
        @DisplayName("Should register configurations without prefix")
        void shouldRegisterConfigurationsWithoutPrefix() {
            OutputLengthGuardrailConfigEntries.register(null);

            OutputLengthGuardrailConfig config = new OutputLengthGuardrailConfig();
            assertThat(config).isNotNull();
        }

        @Test
        @DisplayName("Should register configurations with prefix")
        void shouldRegisterConfigurationsWithPrefix() {
            String prefix = "testoutputlength";

            OutputLengthGuardrailConfigEntries.register(prefix);

            System.setProperty("forage." + prefix + ".guardrail.output.length.max.chars", "25000");

            try {
                OutputLengthGuardrailConfig config = new OutputLengthGuardrailConfig(prefix);
                assertThat(config.maxChars()).isEqualTo(25000);
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.output.length.max.chars");
            }
        }
    }
}
