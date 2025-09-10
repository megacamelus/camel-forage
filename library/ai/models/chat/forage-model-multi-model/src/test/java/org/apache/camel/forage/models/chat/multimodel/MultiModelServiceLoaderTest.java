package org.apache.camel.forage.models.chat.multimodel;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.model.chat.ChatModel;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import org.apache.camel.forage.core.ai.ModelProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for MultiModelProvider focusing on ServiceLoader discovery mechanisms.
 *
 * <p>This test class verifies:
 * <ul>
 *   <li>ServiceLoader discovery of MultiModelProvider</li>
 *   <li>META-INF/services file configuration</li>
 *   <li>Provider interface implementation</li>
 *   <li>Multiple provider scenarios</li>
 *   <li>Service loading edge cases</li>
 * </ul>
 */
@DisplayName("MultiModelProvider ServiceLoader Discovery Tests")
class MultiModelServiceLoaderTest {

    @Nested
    @DisplayName("ServiceLoader Discovery Tests")
    class ServiceLoaderDiscoveryTests {

        @Test
        @DisplayName("Should discover MultiModelProvider through ServiceLoader")
        void shouldDiscoverMultiModelProviderThroughServiceLoader() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            List<ModelProvider> providers =
                    StreamSupport.stream(serviceLoader.spliterator(), false).toList();

            assertThat(providers).isNotEmpty();

            // Find MultiModelProvider in the list
            boolean multiModelProviderFound = providers.stream().anyMatch(provider -> provider instanceof MultiModelProvider);

            assertThat(multiModelProviderFound)
                    .withFailMessage("MultiModelProvider should be discoverable through ServiceLoader")
                    .isTrue();
        }

        @Test
        @DisplayName("Should load MultiModelProvider as specific instance")
        void shouldLoadMultiModelProviderAsSpecificInstance() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            MultiModelProvider multiModelProvider = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .filter(provider -> provider instanceof MultiModelProvider)
                    .map(provider -> (MultiModelProvider) provider)
                    .findFirst()
                    .orElse(null);

            assertThat(multiModelProvider).isNotNull();
            assertThat(multiModelProvider).isInstanceOf(MultiModelProvider.class);
            assertThat(multiModelProvider).isInstanceOf(ModelProvider.class);
        }

        @Test
        @DisplayName("Should create ChatModel from discovered provider")
        void shouldCreateChatModelFromDiscoveredProvider() {
            System.setProperty("multimodel.default.model", "openai");
            System.setProperty("multimodel.available.models", "openai");

            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            ModelProvider multiModelProvider = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .filter(provider -> provider instanceof MultiModelProvider)
                    .findFirst()
                    .orElse(null);

            assertThat(multiModelProvider).isNotNull();

            ChatModel model = multiModelProvider.create();
            assertThat(model).isNotNull();
            assertThat(model).isInstanceOf(ChatModel.class);
        }

        @Test
        @DisplayName("Should create named instances from discovered provider")
        void shouldCreateNamedInstancesFromDiscoveredProvider() {
            System.setProperty("multimodel.default.model", "openai");
            System.setProperty("test-instance.multimodel.default.model", "ollama");

            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            ModelProvider multiModelProvider = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .filter(provider -> provider instanceof MultiModelProvider)
                    .findFirst()
                    .orElse(null);

            assertThat(multiModelProvider).isNotNull();

            ChatModel defaultModel = multiModelProvider.create();
            ChatModel namedModel = multiModelProvider.create("test-instance");

            assertThat(defaultModel).isNotNull();
            assertThat(namedModel).isNotNull();
            assertThat(defaultModel).isNotSameAs(namedModel);
        }
    }

    @Nested
    @DisplayName("ServiceLoader Provider Count Tests")
    class ServiceLoaderProviderCountTests {

        @Test
        @DisplayName("Should have at least one ModelProvider available")
        void shouldHaveAtLeastOneModelProviderAvailable() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            long providerCount =
                    StreamSupport.stream(serviceLoader.spliterator(), false).count();

            assertThat(providerCount).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Should have exactly one MultiModelProvider instance")
        void shouldHaveExactlyOneMultiModelProviderInstance() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            long multiModelProviderCount = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .filter(provider -> provider instanceof MultiModelProvider)
                    .count();

            assertThat(multiModelProviderCount)
                    .withFailMessage("Should have exactly one MultiModelProvider instance")
                    .isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("META-INF/services Integration Tests")
    class MetaInfServicesIntegrationTests {

        @Test
        @DisplayName("Should find META-INF/services/org.apache.camel.forage.core.ai.ModelProvider file")
        void shouldFindMetaInfServicesFile() {
            ClassLoader classLoader = MultiModelProvider.class.getClassLoader();

            java.net.URL resource =
                    classLoader.getResource("META-INF/services/org.apache.camel.forage.core.ai.ModelProvider");

            assertThat(resource)
                    .withFailMessage(
                            "META-INF/services/org.apache.camel.forage.core.ai.ModelProvider file should exist")
                    .isNotNull();
        }
    }
}