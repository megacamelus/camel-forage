package io.kaoto.forage.guardrails.input;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.guardrail.InputGuardrail;
import io.kaoto.forage.core.guardrails.InputGuardrailProvider;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for InputGuardrailProvider ServiceLoader discovery.
 */
@DisplayName("InputGuardrailProvider ServiceLoader Discovery Tests")
class InputGuardrailServiceLoaderTest {

    @Nested
    @DisplayName("ServiceLoader Discovery Tests")
    class ServiceLoaderDiscoveryTests {

        @Test
        @DisplayName("Should discover all input guardrail providers through ServiceLoader")
        void shouldDiscoverAllInputGuardrailProvidersThroughServiceLoader() {
            ServiceLoader<InputGuardrailProvider> serviceLoader = ServiceLoader.load(InputGuardrailProvider.class);

            List<InputGuardrailProvider> providers =
                    StreamSupport.stream(serviceLoader.spliterator(), false).toList();

            assertThat(providers).isNotEmpty();
            assertThat(providers.size()).isGreaterThanOrEqualTo(5);
        }

        @Test
        @DisplayName("Should discover InputLengthGuardrailProvider")
        void shouldDiscoverInputLengthGuardrailProvider() {
            ServiceLoader<InputGuardrailProvider> serviceLoader = ServiceLoader.load(InputGuardrailProvider.class);

            boolean found = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .anyMatch(provider -> provider instanceof InputLengthGuardrailProvider);

            assertThat(found).isTrue();
        }

        @Test
        @DisplayName("Should discover PiiDetectorGuardrailProvider")
        void shouldDiscoverPiiDetectorGuardrailProvider() {
            ServiceLoader<InputGuardrailProvider> serviceLoader = ServiceLoader.load(InputGuardrailProvider.class);

            boolean found = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .anyMatch(provider -> provider instanceof PiiDetectorGuardrailProvider);

            assertThat(found).isTrue();
        }

        @Test
        @DisplayName("Should discover PromptInjectionGuardrailProvider")
        void shouldDiscoverPromptInjectionGuardrailProvider() {
            ServiceLoader<InputGuardrailProvider> serviceLoader = ServiceLoader.load(InputGuardrailProvider.class);

            boolean found = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .anyMatch(provider -> provider instanceof PromptInjectionGuardrailProvider);

            assertThat(found).isTrue();
        }

        @Test
        @DisplayName("Should discover CodeInjectionGuardrailProvider")
        void shouldDiscoverCodeInjectionGuardrailProvider() {
            ServiceLoader<InputGuardrailProvider> serviceLoader = ServiceLoader.load(InputGuardrailProvider.class);

            boolean found = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .anyMatch(provider -> provider instanceof CodeInjectionGuardrailProvider);

            assertThat(found).isTrue();
        }

        @Test
        @DisplayName("Should discover KeywordFilterGuardrailProvider")
        void shouldDiscoverKeywordFilterGuardrailProvider() {
            ServiceLoader<InputGuardrailProvider> serviceLoader = ServiceLoader.load(InputGuardrailProvider.class);

            boolean found = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .anyMatch(provider -> provider instanceof KeywordFilterGuardrailProvider);

            assertThat(found).isTrue();
        }
    }

    @Nested
    @DisplayName("Provider Creation Tests")
    class ProviderCreationTests {

        @Test
        @DisplayName("Should create InputGuardrail from InputLengthGuardrailProvider")
        void shouldCreateInputGuardrailFromInputLengthProvider() {
            InputLengthGuardrailProvider provider = new InputLengthGuardrailProvider();

            InputGuardrail guardrail = provider.create();

            assertThat(guardrail).isNotNull();
            assertThat(guardrail).isInstanceOf(InputGuardrail.class);
        }

        @Test
        @DisplayName("Should create InputGuardrail from PiiDetectorGuardrailProvider")
        void shouldCreateInputGuardrailFromPiiDetectorProvider() {
            PiiDetectorGuardrailProvider provider = new PiiDetectorGuardrailProvider();

            InputGuardrail guardrail = provider.create();

            assertThat(guardrail).isNotNull();
            assertThat(guardrail).isInstanceOf(InputGuardrail.class);
        }

        @Test
        @DisplayName("Should create InputGuardrail from PromptInjectionGuardrailProvider")
        void shouldCreateInputGuardrailFromPromptInjectionProvider() {
            PromptInjectionGuardrailProvider provider = new PromptInjectionGuardrailProvider();

            InputGuardrail guardrail = provider.create();

            assertThat(guardrail).isNotNull();
            assertThat(guardrail).isInstanceOf(InputGuardrail.class);
        }

        @Test
        @DisplayName("Should create InputGuardrail from CodeInjectionGuardrailProvider")
        void shouldCreateInputGuardrailFromCodeInjectionProvider() {
            CodeInjectionGuardrailProvider provider = new CodeInjectionGuardrailProvider();

            InputGuardrail guardrail = provider.create();

            assertThat(guardrail).isNotNull();
            assertThat(guardrail).isInstanceOf(InputGuardrail.class);
        }

        @Test
        @DisplayName("Should create InputGuardrail from KeywordFilterGuardrailProvider")
        void shouldCreateInputGuardrailFromKeywordFilterProvider() {
            KeywordFilterGuardrailProvider provider = new KeywordFilterGuardrailProvider();

            InputGuardrail guardrail = provider.create();

            assertThat(guardrail).isNotNull();
            assertThat(guardrail).isInstanceOf(InputGuardrail.class);
        }

        @Test
        @DisplayName("Should create named instances")
        void shouldCreateNamedInstances() {
            InputLengthGuardrailProvider provider = new InputLengthGuardrailProvider();

            InputGuardrail defaultGuardrail = provider.create();
            InputGuardrail namedGuardrail = provider.create("test-instance");

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
            ClassLoader classLoader = InputLengthGuardrailProvider.class.getClassLoader();

            java.net.URL resource =
                    classLoader.getResource("META-INF/services/io.kaoto.forage.core.guardrails.InputGuardrailProvider");

            assertThat(resource)
                    .withFailMessage(
                            "META-INF/services/io.kaoto.forage.core.guardrails.InputGuardrailProvider file should exist")
                    .isNotNull();
        }

        @Test
        @DisplayName("Should work with ServiceLoader.Provider API")
        void shouldWorkWithServiceLoaderProviderApi() {
            ServiceLoader<InputGuardrailProvider> serviceLoader = ServiceLoader.load(InputGuardrailProvider.class);

            List<ServiceLoader.Provider<InputGuardrailProvider>> providerWrappers =
                    serviceLoader.stream().toList();

            assertThat(providerWrappers).isNotEmpty();

            ServiceLoader.Provider<InputGuardrailProvider> inputLengthWrapper = providerWrappers.stream()
                    .filter(wrapper -> wrapper.type() == InputLengthGuardrailProvider.class)
                    .findFirst()
                    .orElse(null);

            assertThat(inputLengthWrapper).isNotNull();
            assertThat(inputLengthWrapper.type()).isEqualTo(InputLengthGuardrailProvider.class);

            InputGuardrailProvider provider = inputLengthWrapper.get();
            assertThat(provider).isNotNull();

            InputGuardrail guardrail = provider.create();
            assertThat(guardrail).isNotNull();
        }
    }
}
