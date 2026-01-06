package io.kaoto.forage.models.chat.bedrock;

import static org.assertj.core.api.Assertions.assertThat;

import dev.langchain4j.model.chat.ChatModel;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;
import io.kaoto.forage.core.ai.ModelProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Tests for BedrockProvider focusing on ServiceLoader discovery mechanisms.
 */
@DisplayName("BedrockProvider ServiceLoader Discovery Tests")
class BedrockServiceLoaderTest {

    @AfterEach
    void cleanup() {
        System.clearProperty("forage.bedrock.model.id");
        System.clearProperty("forage.test-instance.bedrock.model.id");
        System.clearProperty("forage.service-loader-test.bedrock.model.id");
    }

    @Nested
    @DisplayName("ServiceLoader Discovery Tests")
    class ServiceLoaderDiscoveryTests {

        @Test
        @DisplayName("Should discover BedrockProvider through ServiceLoader")
        void shouldDiscoverBedrockProviderThroughServiceLoader() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            List<ModelProvider> providers =
                    StreamSupport.stream(serviceLoader.spliterator(), false).toList();

            assertThat(providers).isNotEmpty();

            boolean bedrockProviderFound = providers.stream().anyMatch(provider -> provider instanceof BedrockProvider);

            assertThat(bedrockProviderFound)
                    .withFailMessage("BedrockProvider should be discoverable through ServiceLoader")
                    .isTrue();
        }

        @Test
        @DisplayName("Should load BedrockProvider as specific instance")
        void shouldLoadBedrockProviderAsSpecificInstance() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            BedrockProvider bedrockProvider = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .filter(provider -> provider instanceof BedrockProvider)
                    .map(provider -> (BedrockProvider) provider)
                    .findFirst()
                    .orElse(null);

            assertThat(bedrockProvider).isNotNull();
            assertThat(bedrockProvider).isInstanceOf(BedrockProvider.class);
            assertThat(bedrockProvider).isInstanceOf(ModelProvider.class);
        }

        @Test
        @DisplayName("Should create ChatModel from discovered provider")
        void shouldCreateChatModelFromDiscoveredProvider() {
            System.setProperty("forage.bedrock.model.id", "anthropic.claude-3-haiku-20240307-v1:0");

            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            ModelProvider bedrockProvider = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .filter(provider -> provider instanceof BedrockProvider)
                    .findFirst()
                    .orElse(null);

            assertThat(bedrockProvider).isNotNull();

            ChatModel model = bedrockProvider.create();
            assertThat(model).isNotNull();
            assertThat(model).isInstanceOf(ChatModel.class);
        }

        @Test
        @DisplayName("Should create named instances from discovered provider")
        void shouldCreateNamedInstancesFromDiscoveredProvider() {
            System.setProperty("forage.bedrock.model.id", "anthropic.claude-3-haiku-20240307-v1:0");
            System.setProperty("forage.test-instance.bedrock.model.id", "meta.llama3-1-8b-instruct-v1:0");

            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            ModelProvider bedrockProvider = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .filter(provider -> provider instanceof BedrockProvider)
                    .findFirst()
                    .orElse(null);

            assertThat(bedrockProvider).isNotNull();

            ChatModel defaultModel = bedrockProvider.create();
            ChatModel namedModel = bedrockProvider.create("test-instance");

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
        @DisplayName("Should have exactly one BedrockProvider instance")
        void shouldHaveExactlyOneBedrockProviderInstance() {
            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            long bedrockProviderCount = StreamSupport.stream(serviceLoader.spliterator(), false)
                    .filter(provider -> provider instanceof BedrockProvider)
                    .count();

            assertThat(bedrockProviderCount)
                    .withFailMessage("Should have exactly one BedrockProvider instance")
                    .isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("META-INF/services Integration Tests")
    class MetaInfServicesIntegrationTests {

        @Test
        @DisplayName("Should find META-INF/services/io.kaoto.forage.core.ai.ModelProvider file")
        void shouldFindMetaInfServicesFile() {
            ClassLoader classLoader = BedrockProvider.class.getClassLoader();

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
            System.setProperty("forage.bedrock.model.id", "anthropic.claude-3-haiku-20240307-v1:0");
            System.setProperty("forage.service-loader-test.bedrock.model.id", "anthropic.claude-3-haiku-20240307-v1:0");

            ServiceLoader<ModelProvider> serviceLoader = ServiceLoader.load(ModelProvider.class);

            List<ModelProvider> providers =
                    StreamSupport.stream(serviceLoader.spliterator(), false).toList();

            assertThat(providers).isNotEmpty();

            BedrockProvider bedrockProvider = providers.stream()
                    .filter(provider -> provider instanceof BedrockProvider)
                    .map(provider -> (BedrockProvider) provider)
                    .findFirst()
                    .orElse(null);

            assertThat(bedrockProvider).isNotNull();

            ChatModel model = bedrockProvider.create();
            assertThat(model).isNotNull();

            ChatModel namedModel = bedrockProvider.create("service-loader-test");
            assertThat(namedModel).isNotNull();
            assertThat(namedModel).isNotSameAs(model);
        }
    }
}
