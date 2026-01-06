package io.kaoto.forage.models.chat.mistralai;

import static java.time.Duration.ofSeconds;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.mistralai.MistralAiChatModel;
import io.kaoto.forage.core.ai.ModelProvider;
import io.kaoto.forage.core.annotations.ForageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating MistralAI chat models with configurable parameters.
 *
 * <p>This provider creates instances of {@link MistralAiChatModel} using configuration
 * values managed by {@link MistralAiConfig}. The configuration supports environment
 * variables, system properties, and configuration files for flexible deployment.
 *
 * <p><strong>Configuration:</strong>
 * <ul>
 *   <li>API Key: Configured via MISTRALAI_API_KEY environment variable (required)</li>
 *   <li>Model Name: Configured via MISTRALAI_MODEL_NAME environment variable or defaults to "mistral-large-latest"</li>
 *   <li>Temperature: Optionally configured via MISTRALAI_TEMPERATURE environment variable (0.0-1.0)</li>
 *   <li>Max Tokens: Optionally configured via MISTRALAI_MAX_TOKENS environment variable</li>
 *   <li>Top-P: Optionally configured via MISTRALAI_TOP_P environment variable (0.0-1.0)</li>
 *   <li>Random Seed: Optionally configured via MISTRALAI_RANDOM_SEED environment variable</li>
 *   <li>Timeout: Optionally configured via MISTRALAI_TIMEOUT environment variable</li>
 *   <li>Max Retries: Optionally configured via MISTRALAI_MAX_RETRIES environment variable</li>
 *   <li>Request/Response Logging: Optionally configured via MISTRALAI_LOG_REQUESTS_AND_RESPONSES environment variable</li>
 * </ul>
 *
 * <p><strong>Usage:</strong>
 * <pre>{@code
 * // Configuration is automatic through environment variables or defaults
 * MistralAiProvider provider = new MistralAiProvider();
 * ChatModel model = provider.create("my-model");
 * }</pre>
 *
 * @see MistralAiConfig
 * @see ModelProvider
 * @since 1.0
 */
@ForageBean(
        value = "mistral-ai",
        components = {"camel-langchain4j-agent"},
        feature = "Chat Model",
        description = "Mistral AI models")
public class MistralAiProvider implements ModelProvider {
    private static final Logger LOG = LoggerFactory.getLogger(MistralAiProvider.class);

    /**
     * Creates a new MistralAI chat model instance with the configured parameters.
     *
     * <p>This method creates a {@link MistralAiChatModel} using the API key, model name,
     * and other parameters from the configuration. The model is ready to use for chat
     * operations once created.
     *
     * @param id the configuration prefix to use (optional)
     * @return a new configured MistralAI chat model instance
     */
    @Override
    public ChatModel create(String id) {
        final MistralAiConfig config = new MistralAiConfig(id);
        LOG.trace("Creating MistralAI chat model");

        MistralAiChatModel.MistralAiChatModelBuilder builder =
                MistralAiChatModel.builder().apiKey(config.apiKey()).modelName(config.modelName());

        // Configure model behavior parameters
        if (config.temperature() != null) {
            builder.temperature(config.temperature());
        }

        if (config.maxTokens() != null) {
            builder.maxTokens(config.maxTokens());
        }

        if (config.topP() != null) {
            builder.topP(config.topP());
        }

        if (config.randomSeed() != null) {
            builder.randomSeed(config.randomSeed());
        }

        // Configure connection and reliability settings
        if (config.timeoutSeconds() != null) {
            builder.timeout(ofSeconds(config.timeoutSeconds()));
        }

        if (config.maxRetries() != null) {
            builder.maxRetries(config.maxRetries());
        }

        // Configure logging settings
        if (config.logRequestsAndResponses() != null) {
            builder.logRequests(config.logRequestsAndResponses());
            builder.logResponses(config.logRequestsAndResponses());
        }

        return builder.build();
    }
}
