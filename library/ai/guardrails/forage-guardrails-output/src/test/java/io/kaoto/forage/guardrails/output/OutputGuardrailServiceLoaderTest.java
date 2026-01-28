package io.kaoto.forage.guardrails.output;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.guardrail.OutputGuardrail;
import io.kaoto.forage.core.guardrails.OutputGuardrailProvider;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for OutputGuardrailProvider ServiceLoader discovery.
 */
@DisplayName("OutputGuardrailProvider ServiceLoader Discovery Tests")
class OutputGuardrailServiceLoaderTest {

    @Nested
    @DisplayName("ServiceLoader Discovery Tests")
    class ServiceLoaderDiscoveryTests {

        @Test
        @DisplayName("Should discover all output guardrail providers through ServiceLoader")
        void shouldDiscoverAllOutputGuardrailProvidersThroughServiceLoader() {
            ServiceLoader<OutputGuardrailProvider> serviceLoader = ServiceLoader.load(OutputGuardrailProvider.class);

            List<OutputGuardrailProvider> providers =
                    StreamSupport.stream(serviceLoader.spliterator(), false).toList();

            assertThat(providers).isNotEmpty();
            assertThat(providers.size()).isGreaterThanOrEqualTo(3);
        }

        @Test
        @DisplayName("Should discover OutputLengthGuardrailProvider")
        void shouldDiscoverOutputLengthGuardrailProvider() {
            ServiceLoader<OutputGuardrailProvider> serviceLoader = ServiceLoader.load(OutputGuardrailProvider.class);

            boolean found = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .anyMatch(provider -> provider instanceof OutputLengthGuardrailProvider);

            assertThat(found).isTrue();
        }

        @Test
        @DisplayName("Should discover SensitiveDataGuardrailProvider")
        void shouldDiscoverSensitiveDataGuardrailProvider() {
            ServiceLoader<OutputGuardrailProvider> serviceLoader = ServiceLoader.load(OutputGuardrailProvider.class);

            boolean found = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .anyMatch(provider -> provider instanceof SensitiveDataGuardrailProvider);

            assertThat(found).isTrue();
        }

        @Test
        @DisplayName("Should discover JsonFormatGuardrailProvider")
        void shouldDiscoverJsonFormatGuardrailProvider() {
            ServiceLoader<OutputGuardrailProvider> serviceLoader = ServiceLoader.load(OutputGuardrailProvider.class);

            boolean found = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .anyMatch(provider -> provider instanceof JsonFormatGuardrailProvider);

            assertThat(found).isTrue();
        }
    }

    @Nested
    @DisplayName("Provider Creation Tests")
    class ProviderCreationTests {

        @Test
        @DisplayName("Should create OutputGuardrail from OutputLengthGuardrailProvider")
        void shouldCreateOutputGuardrailFromOutputLengthProvider() {
            OutputLengthGuardrailProvider provider = new OutputLengthGuardrailProvider();

            OutputGuardrail guardrail = provider.create();

            assertThat(guardrail).isNotNull();
            assertThat(guardrail).isInstanceOf(OutputGuardrail.class);
        }

        @Test
        @DisplayName("Should create OutputGuardrail from SensitiveDataGuardrailProvider")
        void shouldCreateOutputGuardrailFromSensitiveDataProvider() {
            SensitiveDataGuardrailProvider provider = new SensitiveDataGuardrailProvider();

            OutputGuardrail guardrail = provider.create();

            assertThat(guardrail).isNotNull();
            assertThat(guardrail).isInstanceOf(OutputGuardrail.class);
        }

        @Test
        @DisplayName("Should create OutputGuardrail from JsonFormatGuardrailProvider")
        void shouldCreateOutputGuardrailFromJsonFormatProvider() {
            JsonFormatGuardrailProvider provider = new JsonFormatGuardrailProvider();

            OutputGuardrail guardrail = provider.create();

            assertThat(guardrail).isNotNull();
            assertThat(guardrail).isInstanceOf(OutputGuardrail.class);
        }

        @Test
        @DisplayName("Should create named instances")
        void shouldCreateNamedInstances() {
            OutputLengthGuardrailProvider provider = new OutputLengthGuardrailProvider();

            OutputGuardrail defaultGuardrail = provider.create();
            OutputGuardrail namedGuardrail = provider.create("test-instance");

            assertThat(defaultGuardrail).isNotNull();
            assertThat(namedGuardrail).isNotNull();
            assertThat(defaultGuardrail).isNotSameAs(namedGuardrail);
        }
    }

    @Nested
    @DisplayName("META-INF/services Integration Tests")
    class MetaInfServicesIntegrationTests {

        @Test
        @DisplayName("Should find META-INF/services file")
        void shouldFindMetaInfServicesFile() {
            ClassLoader classLoader = OutputLengthGuardrailProvider.class.getClassLoader();

            java.net.URL resource = classLoader.getResource(
                    "META-INF/services/io.kaoto.forage.core.guardrails.OutputGuardrailProvider");

            assertThat(resource)
                    .withFailMessage(
                            "META-INF/services/io.kaoto.forage.core.guardrails.OutputGuardrailProvider file should exist")
                    .isNotNull();
        }

        @Test
        @DisplayName("Should work with ServiceLoader.Provider API")
        void shouldWorkWithServiceLoaderProviderApi() {
            ServiceLoader<OutputGuardrailProvider> serviceLoader = ServiceLoader.load(OutputGuardrailProvider.class);

            List<ServiceLoader.Provider<OutputGuardrailProvider>> providerWrappers =
                    serviceLoader.stream().toList();

            assertThat(providerWrappers).isNotEmpty();

            ServiceLoader.Provider<OutputGuardrailProvider> outputLengthWrapper = providerWrappers.stream()
                    .filter(wrapper -> wrapper.type() == OutputLengthGuardrailProvider.class)
                    .findFirst()
                    .orElse(null);

            assertThat(outputLengthWrapper).isNotNull();
            assertThat(outputLengthWrapper.type()).isEqualTo(OutputLengthGuardrailProvider.class);

            OutputGuardrailProvider provider = outputLengthWrapper.get();
            assertThat(provider).isNotNull();

            OutputGuardrail guardrail = provider.create();
            assertThat(guardrail).isNotNull();
        }
    }
}
