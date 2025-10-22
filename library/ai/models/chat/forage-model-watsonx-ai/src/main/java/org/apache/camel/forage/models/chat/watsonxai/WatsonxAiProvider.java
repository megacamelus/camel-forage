package org.apache.camel.forage.models.chat.watsonxai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.watsonx.WatsonxChatModel;
import org.apache.camel.forage.core.ai.ModelProvider;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating IBM Watsonx.ai chat models with configurable parameters.
 *
 * <p>This provider creates instances of {@link WatsonxChatModel} using configuration
 * values managed by {@link WatsonxAiConfig}. The configuration supports environment
 * variables, system properties, and configuration files for flexible deployment.
 *
 * <p><strong>Configuration:</strong>
 * <ul>
 *   <li>API Key: Configured via WATSONXAI_API_KEY environment variable (required)</li>
 *   <li>URL: Configured via WATSONXAI_URL environment variable (required)</li>
 *   <li>Project ID: Configured via WATSONXAI_PROJECT_ID environment variable (required)</li>
 *   <li>Model Name: Configured via WATSONXAI_MODEL_NAME environment variable or defaults to "llama-3-405b-instruct"</li>
 *   <li>Temperature: Optionally configured via WATSONXAI_TEMPERATURE environment variable (0.0-2.0)</li>
 *   <li>Max New Tokens: Optionally configured via WATSONXAI_MAX_NEW_TOKENS environment variable</li>
 *   <li>Top-P: Optionally configured via WATSONXAI_TOP_P environment variable (0.0-1.0)</li>
 *   <li>Top-K: Optionally configured via WATSONXAI_TOP_K environment variable</li>
 *   <li>Random Seed: Optionally configured via WATSONXAI_RANDOM_SEED environment variable</li>
 *   <li>Repetition Penalty: Optionally configured via WATSONXAI_REPETITION_PENALTY environment variable (1.0-2.0)</li>
 *   <li>Min New Tokens: Optionally configured via WATSONXAI_MIN_NEW_TOKENS environment variable</li>
 *   <li>Stop Sequences: Optionally configured via WATSONXAI_STOP_SEQUENCES environment variable</li>
 *   <li>Timeout: Optionally configured via WATSONXAI_TIMEOUT environment variable</li>
 *   <li>Max Retries: Optionally configured via WATSONXAI_MAX_RETRIES environment variable</li>
 *   <li>Request/Response Logging: Optionally configured via WATSONXAI_LOG_REQUESTS_AND_RESPONSES environment variable</li>
 * </ul>
 *
 * <p><strong>Usage:</strong>
 * <pre>{@code
 * // Configuration is automatic through environment variables or defaults
 * WatsonxAiProvider provider = new WatsonxAiProvider();
 * ChatModel model = provider.create("my-model");
 * }</pre>
 *
 * @see WatsonxAiConfig
 * @see ModelProvider
 * @since 1.0
 */
@ForageBean(
        value = "watsonx-ai",
        components = {"camel-langchain4j-agent"},
        feature = "Chat Model",
        description = "IBM Watsonx.ai models")
public class WatsonxAiProvider implements ModelProvider {
    private static final Logger LOG = LoggerFactory.getLogger(WatsonxAiProvider.class);

    /**
     * Creates a new Watsonx.ai chat model instance with the configured parameters.
     *
     * <p>This method creates a {@link WatsonxChatModel} using the API key, URL, project ID,
     * model name, and other parameters from the configuration. The model is ready to use for chat
     * operations once created.
     *
     * @param id the configuration prefix to use (optional)
     * @return a new configured Watsonx.ai chat model instance
     */
    @Override
    public ChatModel create(String id) {
        final WatsonxAiConfig config = new WatsonxAiConfig(id);
        LOG.trace("Creating Watsonx.ai chat model");

        WatsonxChatModel.Builder builder = WatsonxChatModel.builder()
                .apiKey(config.apiKey())
                .url(config.url())
                .projectId(config.projectId())
                .modelName(config.modelName());

        // Configure model behavior parameters
        if (config.temperature() != null) {
            builder.temperature(config.temperature());
        }

        if (config.topP() != null) {
            builder.topP(config.topP());
        }

        if (config.stopSequences() != null) {
            builder.stopSequences(config.stopSequences());
        }

        // Configure logging settings
        if (config.logRequestsAndResponses() != null) {
            builder.logRequests(config.logRequestsAndResponses());
            builder.logResponses(config.logRequestsAndResponses());
        }

        return builder.build();
    }
}
