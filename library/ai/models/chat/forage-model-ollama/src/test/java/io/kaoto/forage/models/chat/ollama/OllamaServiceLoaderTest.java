package io.kaoto.forage.models.chat.ollama;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.model.chat.ChatModel;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import io.kaoto.forage.core.ai.ModelProvider;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for OllamaProvider focusing on ServiceLoader discovery mechanisms.
 *
 * <p>This test class verifies:
 * <ul>
 *   <li>ServiceLoader discovery of OllamaProvider</li>
 *   <li>META-INF/services file configuration</li>
 *   <li>Provider interface implementation</li>
 *   <li>Multiple provider scenarios</li>
 *   <li>Service loading edge cases</li>
 * </ul>
 */
@DisplayName("OllamaProvider ServiceLoader Discovery Tests")
class OllamaServiceLoaderTest {

    @Nested
    @DisplayName("ServiceLoader Discovery Tests")
    class ServiceLoaderDiscoveryTests {

        @Test
        @DisplayName("Should discover OllamaProvider through ServiceLoader")
        void shouldDiscoverOllamaProviderThroughServiceLoader() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            List<ModelProvider> providers =
                    StreamSupport.stream(serviceLoader.spliterator(), false).toList();

            assertThat(providers).isNotEmpty();

            // Find OllamaProvider in the list
            boolean ollamaProviderFound = providers.stream().anyMatch(provider -> provider instanceof OllamaProvider);

            assertThat(ollamaProviderFound)
                    .withFailMessage("OllamaProvider should be discoverable through ServiceLoader")
                    .isTrue();
        }

        @Test
        @DisplayName("Should load OllamaProvider as specific instance")
        void shouldLoadOllamaProviderAsSpecificInstance() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            OllamaProvider ollamaProvider = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .filter(provider -> provider instanceof OllamaProvider)
                    .map(provider -> (OllamaProvider) provider)
                    .findFirst()
                    .orElse(null);

            assertThat(ollamaProvider).isNotNull();
            assertThat(ollamaProvider).isInstanceOf(OllamaProvider.class);
            assertThat(ollamaProvider).isInstanceOf(ModelProvider.class);
        }

        @Test
        @DisplayName("Should create ChatModel from discovered provider")
        void shouldCreateChatModelFromDiscoveredProvider() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            ModelProvider ollamaProvider = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .filter(provider -> provider instanceof OllamaProvider)
                    .findFirst()
                    .orElse(null);

            assertThat(ollamaProvider).isNotNull();

            ChatModel model = ollamaProvider.create();
            assertThat(model).isNotNull();
            assertThat(model).isInstanceOf(ChatModel.class);
        }

        @Test
        @DisplayName("Should create named instances from discovered provider")
        void shouldCreateNamedInstancesFromDiscoveredProvider() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            ModelProvider ollamaProvider = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .filter(provider -> provider instanceof OllamaProvider)
                    .findFirst()
                    .orElse(null);

            assertThat(ollamaProvider).isNotNull();

            ChatModel defaultModel = ollamaProvider.create();
            ChatModel namedModel = ollamaProvider.create("test-instance");

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
        @DisplayName("Should have exactly one OllamaProvider instance")
        void shouldHaveExactlyOneOllamaProviderInstance() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            long ollamaProviderCount = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .filter(provider -> provider instanceof OllamaProvider)
                    .count();

            assertThat(ollamaProviderCount)
                    .withFailMessage("Should have exactly one OllamaProvider instance")
                    .isEqualTo(1);
        }

        @Test
        @DisplayName("Should handle multiple ServiceLoader iterations")
        void shouldHandleMultipleServiceLoaderIterations() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            // First iteration
            List<ModelProvider> firstIteration =
                    StreamSupport.stream(serviceLoader.spliterator(), false).toList();

            // Second iteration (should reload)
            serviceLoader.reload();
            List<ModelProvider> secondIteration =
                    StreamSupport.stream(serviceLoader.spliterator(), false).toList();

            assertThat(firstIteration).isNotEmpty();
            assertThat(secondIteration).isNotEmpty();
            assertThat(firstIteration.size()).isEqualTo(secondIteration.size());

            // Verify OllamaProvider is in both iterations
            boolean firstHasOllama = firstIteration.stream().anyMatch(provider -> provider instanceof OllamaProvider);
            boolean secondHasOllama = secondIteration.stream().anyMatch(provider -> provider instanceof OllamaProvider);

            assertThat(firstHasOllama).isTrue();
            assertThat(secondHasOllama).isTrue();
        }
    }

    @Nested
    @DisplayName("ServiceLoader Configuration Tests")
    class ServiceLoaderConfigurationTests {

        @Test
        @DisplayName("Should load from correct ClassLoader")
        void shouldLoadFromCorrectClassLoader() {
            ClassLoader classLoader = OllamaProvider.class.getClassLoader();
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class, classLoader);

            List<ModelProvider> providers =
                    StreamSupport.stream(serviceLoader.spliterator(), false).toList();

            assertThat(providers).isNotEmpty();

            boolean ollamaProviderFound = providers.stream().anyMatch(provider -> provider instanceof OllamaProvider);

            assertThat(ollamaProviderFound).isTrue();
        }

        @Test
        @DisplayName("Should discover providers with thread context ClassLoader")
        void shouldDiscoverProvidersWithThreadContextClassLoader() {
            ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();

            try {
                Thread.currentThread().setContextClassLoader(OllamaProvider.class.getClassLoader());

                ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

                List<ModelProvider> providers =
                        StreamSupport.stream(serviceLoader.spliterator(), false).toList();

                assertThat(providers).isNotEmpty();

                boolean ollamaProviderFound =
                        providers.stream().anyMatch(provider -> provider instanceof OllamaProvider);

                assertThat(ollamaProviderFound).isTrue();

            } finally {
                Thread.currentThread().setContextClassLoader(originalClassLoader);
            }
        }
    }

    @Nested
    @DisplayName("Provider Interface Compliance Tests")
    class ProviderInterfaceComplianceTests {

        @Test
        @DisplayName("Should implement all required interface methods")
        void shouldImplementAllRequiredInterfaceMethods() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            OllamaProvider ollamaProvider = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .filter(provider -> provider instanceof OllamaProvider)
                    .map(provider -> (OllamaProvider) provider)
                    .findFirst()
                    .orElse(null);

            assertThat(ollamaProvider).isNotNull();

            // Test create() method
            ChatModel defaultModel = ollamaProvider.create();
            assertThat(defaultModel).isNotNull();

            // Test create(String id) method
            ChatModel namedModel = ollamaProvider.create("test");
            assertThat(namedModel).isNotNull();
        }

        @Test
        @DisplayName("Should have consistent behavior across ServiceLoader instances")
        void shouldHaveConsistentBehaviorAcrossServiceLoaderInstances() {
            // Create two separate ServiceLoader instances
            ServiceLoader<ModelProvider> serviceLoader1 = ServiceLoader.load(ModelProvider.class);
            ServiceLoader<ModelProvider> serviceLoader2 = ServiceLoader.load(ModelProvider.class);

            OllamaProvider provider1 = StreamSupport.stream(serviceLoader1.spliterator(), false)
                    .filter(provider -> provider instanceof OllamaProvider)
                    .map(provider -> (OllamaProvider) provider)
                    .findFirst()
                    .orElse(null);

            OllamaProvider provider2 = StreamSupport.stream(serviceLoader2.spliterator(), false)
                    .filter(provider -> provider instanceof OllamaProvider)
                    .map(provider -> (OllamaProvider) provider)
                    .findFirst()
                    .orElse(null);

            assertThat(provider1).isNotNull();
            assertThat(provider2).isNotNull();

            // Providers should be different instances
            assertThat(provider1).isNotSameAs(provider2);

            // But they should create functioning models
            ChatModel model1 = provider1.create();
            ChatModel model2 = provider2.create();

            assertThat(model1).isNotNull();
            assertThat(model2).isNotNull();
        }
    }

    @Nested
    @DisplayName("META-INF/services Integration Tests")
    class MetaInfServicesIntegrationTests {

        @Test
        @DisplayName("Should find META-INF/services/io.kaoto.forage.core.ai.ModelProvider file")
        void shouldFindMetaInfServicesFile() {
            // This test verifies that the META-INF/services file exists and is accessible
            ClassLoader classLoader = OllamaProvider.class.getClassLoader();

            java.net.URL resource =
                    classLoader.getResource("META-INF/services/io.kaoto.forage.core.ai.ModelProvider");

            assertThat(resource)
                    .withFailMessage(
                            "META-INF/services/io.kaoto.forage.core.ai.ModelProvider file should exist")
                    .isNotNull();
        }

        @Test
        @DisplayName("Should discover provider through standard ServiceLoader mechanism")
        void shouldDiscoverProviderThroughStandardServiceLoaderMechanism() {
            // This is a comprehensive test that verifies the entire ServiceLoader chain
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            // Convert to list to ensure all providers are loaded
            List<ModelProvider> providers =
                    StreamSupport.stream(serviceLoader.spliterator(), false).toList();

            assertThat(providers).isNotEmpty();

            // Verify OllamaProvider is discovered
            OllamaProvider ollamaProvider = providers.stream()
                    .filter(provider -> provider instanceof OllamaProvider)
                    .map(provider -> (OllamaProvider) provider)
                    .findFirst()
                    .orElse(null);

            assertThat(ollamaProvider).isNotNull();

            // Verify the provider works end-to-end
            ChatModel model = ollamaProvider.create();
            assertThat(model).isNotNull();

            // Verify named instance creation
            ChatModel namedModel = ollamaProvider.create("service-loader-test");
            assertThat(namedModel).isNotNull();
            assertThat(namedModel).isNotSameAs(model);
        }

        @Test
        @DisplayName("Should work with ServiceLoader.Provider API")
        void shouldWorkWithServiceLoaderProviderApi() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            // Use the Provider API (Java 9+)
            List<ServiceLoader.Provider<ModelProvider>> providerWrappers =
                    serviceLoader.stream().toList();

            assertThat(providerWrappers).isNotEmpty();

            // Find OllamaProvider wrapper
            ServiceLoader.Provider<ModelProvider> ollamaProviderWrapper = providerWrappers.stream()
                    .filter(wrapper -> wrapper.type() == OllamaProvider.class)
                    .findFirst()
                    .orElse(null);

            assertThat(ollamaProviderWrapper).isNotNull();
            assertThat(ollamaProviderWrapper.type()).isEqualTo(OllamaProvider.class);

            // Get the actual provider and test it
            ModelProvider ollamaProvider = ollamaProviderWrapper.get();
            assertThat(ollamaProvider).isNotNull();
            assertThat(ollamaProvider).isInstanceOf(OllamaProvider.class);

            ChatModel model = ollamaProvider.create();
            assertThat(model).isNotNull();
        }
    }
}
