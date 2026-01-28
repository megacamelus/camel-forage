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
 * Tests for SensitiveDataGuardrailConfigEntries.
 */
@DisplayName("SensitiveDataGuardrailConfigEntries Tests")
class SensitiveDataGuardrailConfigEntriesTest {

    @Nested
    @DisplayName("Static ConfigModule Field Tests")
    class StaticConfigModuleTests {

        @Test
        @DisplayName("Should have correct ConfigModule names")
        void shouldHaveCorrectConfigModuleNames() {
            assertThat(SensitiveDataGuardrailConfigEntries.DETECT_TYPES.name())
                    .isEqualTo("forage.guardrail.sensitive.data.detect.types");
            assertThat(SensitiveDataGuardrailConfigEntries.ACTION.name())
                    .isEqualTo("forage.guardrail.sensitive.data.action");
            assertThat(SensitiveDataGuardrailConfigEntries.REDACTION_TEXT.name())
                    .isEqualTo("forage.guardrail.sensitive.data.redaction.text");
        }

        @Test
        @DisplayName("Should have correct ConfigModule config class references")
        void shouldHaveCorrectConfigModuleConfigClassReferences() {
            assertThat(SensitiveDataGuardrailConfigEntries.DETECT_TYPES.config())
                    .isEqualTo(SensitiveDataGuardrailConfig.class);
            assertThat(SensitiveDataGuardrailConfigEntries.ACTION.config())
                    .isEqualTo(SensitiveDataGuardrailConfig.class);
            assertThat(SensitiveDataGuardrailConfigEntries.REDACTION_TEXT.config())
                    .isEqualTo(SensitiveDataGuardrailConfig.class);
        }
    }

    @Nested
    @DisplayName("CONFIG_MODULES Map Tests")
    class ConfigModulesMapTests {

        @Test
        @DisplayName("Should return immutable map from entries()")
        void shouldReturnImmutableMapFromEntries() {
            Map<ConfigModule, ConfigEntry> entries = SensitiveDataGuardrailConfigEntries.entries();

            assertThat(entries).isNotNull();
            assertThat(entries).isNotEmpty();

            assertThatThrownBy(entries::clear).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Should contain all ConfigModules in entries map")
        void shouldContainAllConfigModulesInEntriesMap() {
            Map<ConfigModule, ConfigEntry> entries = SensitiveDataGuardrailConfigEntries.entries();

            assertThat(entries).containsKey(SensitiveDataGuardrailConfigEntries.DETECT_TYPES);
            assertThat(entries).containsKey(SensitiveDataGuardrailConfigEntries.ACTION);
            assertThat(entries).containsKey(SensitiveDataGuardrailConfigEntries.REDACTION_TEXT);
        }
    }

    @Nested
    @DisplayName("find() Method Tests")
    class FindMethodTests {

        @Test
        @DisplayName("Should find ConfigModule without prefix")
        void shouldFindConfigModuleWithoutPrefix() {
            Optional<ConfigModule> found =
                    SensitiveDataGuardrailConfigEntries.find(null, "forage.guardrail.sensitive.data.action");

            assertThat(found).isPresent();
            assertThat(found.get()).isEqualTo(SensitiveDataGuardrailConfigEntries.ACTION);
        }

        @Test
        @DisplayName("Should return Optional.empty for unknown configuration name")
        void shouldReturnEmptyForUnknownConfigurationName() {
            assertThat(SensitiveDataGuardrailConfigEntries.find(null, "unknown.config"))
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("register() Method Tests")
    class RegisterMethodTests {

        @Test
        @DisplayName("Should register configurations without prefix")
        void shouldRegisterConfigurationsWithoutPrefix() {
            SensitiveDataGuardrailConfigEntries.register(null);

            SensitiveDataGuardrailConfig config = new SensitiveDataGuardrailConfig();
            assertThat(config).isNotNull();
        }

        @Test
        @DisplayName("Should register configurations with prefix")
        void shouldRegisterConfigurationsWithPrefix() {
            String prefix = "testsensitive";

            SensitiveDataGuardrailConfigEntries.register(prefix);

            System.setProperty("forage." + prefix + ".guardrail.sensitive.data.action", "REDACT");

            try {
                SensitiveDataGuardrailConfig config = new SensitiveDataGuardrailConfig(prefix);
                assertThat(config.action().name()).isEqualTo("REDACT");
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.sensitive.data.action");
            }
        }
    }
}
