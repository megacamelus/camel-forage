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
 * Tests for PiiDetectorGuardrailConfigEntries.
 */
@DisplayName("PiiDetectorGuardrailConfigEntries Tests")
class PiiDetectorGuardrailConfigEntriesTest {

    @Nested
    @DisplayName("Static ConfigModule Field Tests")
    class StaticConfigModuleTests {

        @Test
        @DisplayName("Should have correct ConfigModule names")
        void shouldHaveCorrectConfigModuleNames() {
            assertThat(PiiDetectorGuardrailConfigEntries.DETECT_TYPES.name())
                    .isEqualTo("forage.guardrail.pii.detect.types");
            assertThat(PiiDetectorGuardrailConfigEntries.BLOCK_ON_DETECTION.name())
                    .isEqualTo("forage.guardrail.pii.block.on.detection");
        }

        @Test
        @DisplayName("Should have correct ConfigModule config class references")
        void shouldHaveCorrectConfigModuleConfigClassReferences() {
            assertThat(PiiDetectorGuardrailConfigEntries.DETECT_TYPES.config())
                    .isEqualTo(PiiDetectorGuardrailConfig.class);
            assertThat(PiiDetectorGuardrailConfigEntries.BLOCK_ON_DETECTION.config())
                    .isEqualTo(PiiDetectorGuardrailConfig.class);
        }
    }

    @Nested
    @DisplayName("CONFIG_MODULES Map Tests")
    class ConfigModulesMapTests {

        @Test
        @DisplayName("Should return immutable map from entries()")
        void shouldReturnImmutableMapFromEntries() {
            Map<ConfigModule, ConfigEntry> entries = PiiDetectorGuardrailConfigEntries.entries();

            assertThat(entries).isNotNull();
            assertThat(entries).isNotEmpty();

            assertThatThrownBy(entries::clear).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("Should contain all ConfigModules in entries map")
        void shouldContainAllConfigModulesInEntriesMap() {
            Map<ConfigModule, ConfigEntry> entries = PiiDetectorGuardrailConfigEntries.entries();

            assertThat(entries).containsKey(PiiDetectorGuardrailConfigEntries.DETECT_TYPES);
            assertThat(entries).containsKey(PiiDetectorGuardrailConfigEntries.BLOCK_ON_DETECTION);
        }
    }

    @Nested
    @DisplayName("find() Method Tests")
    class FindMethodTests {

        @Test
        @DisplayName("Should find ConfigModule without prefix")
        void shouldFindConfigModuleWithoutPrefix() {
            Optional<ConfigModule> found =
                    PiiDetectorGuardrailConfigEntries.find(null, "forage.guardrail.pii.detect.types");

            assertThat(found).isPresent();
            assertThat(found.get()).isEqualTo(PiiDetectorGuardrailConfigEntries.DETECT_TYPES);
        }

        @Test
        @DisplayName("Should return Optional.empty for unknown configuration name")
        void shouldReturnEmptyForUnknownConfigurationName() {
            assertThat(PiiDetectorGuardrailConfigEntries.find(null, "unknown.config"))
                    .isEmpty();
        }
    }

    @Nested
    @DisplayName("register() Method Tests")
    class RegisterMethodTests {

        @Test
        @DisplayName("Should register configurations without prefix")
        void shouldRegisterConfigurationsWithoutPrefix() {
            PiiDetectorGuardrailConfigEntries.register(null);

            PiiDetectorGuardrailConfig config = new PiiDetectorGuardrailConfig();
            assertThat(config).isNotNull();
        }

        @Test
        @DisplayName("Should register configurations with prefix")
        void shouldRegisterConfigurationsWithPrefix() {
            String prefix = "testpii";

            PiiDetectorGuardrailConfigEntries.register(prefix);

            System.setProperty("forage." + prefix + ".guardrail.pii.detect.types", "EMAIL,PHONE");

            try {
                PiiDetectorGuardrailConfig config = new PiiDetectorGuardrailConfig(prefix);
                assertThat(config.detectTypes()).hasSize(2);
            } finally {
                System.clearProperty("forage." + prefix + ".guardrail.pii.detect.types");
            }
        }
    }
}
