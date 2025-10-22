package org.apache.camel.forage.models.chat.ollama;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.apache.camel.forage.core.ai.ModelProvider;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating Ollama chat models with configurable parameters.
 *
 * <p>This provider creates instances of {@link OllamaChatModel} using configuration
 * values managed by {@link OllamaConfig}. The configuration supports environment
 * variables, system properties, and configuration files for flexible deployment.
 *
 * <p><strong>Configuration:</strong>
 * <ul>
 *   <li>Base URL: Configured via OLLAMA_BASE_URL environment variable or defaults to "http://localhost:11434"</li>
 *   <li>Model Name: Configured via OLLAMA_MODEL_NAME environment variable or defaults to "llama3"</li>
 *   <li>Temperature: Optionally configured via OLLAMA_TEMPERATURE environment variable (no default)</li>
 *   <li>Top-K: Optionally configured via OLLAMA_TOP_K environment variable (no default)</li>
 *   <li>Top-P: Optionally configured via OLLAMA_TOP_P environment variable (no default)</li>
 *   <li>Min-P: Optionally configured via OLLAMA_MIN_P environment variable (no default)</li>
 *   <li>Context Size: Optionally configured via OLLAMA_NUM_CTX environment variable (no default)</li>
 *   <li>Request Logging: Optionally configured via OLLAMA_LOG_REQUESTS environment variable (no default)</li>
 *   <li>Response Logging: Optionally configured via OLLAMA_LOG_RESPONSES environment variable (no default)</li>
 * </ul>
 *
 * <p><strong>Usage:</strong>
 * <pre>{@code
 * // Configuration is automatic through environment variables or defaults
 * OllamaProvider provider = new OllamaProvider();
 * ChatModel model = provider.newModel();
 * }</pre>
 *
 * @see OllamaConfig
 * @see ModelProvider
 * @since 1.0
 */
@ForageBean(
        value = "ollama",
        components = {"camel-langchain4j-agent"},
        feature = "Chat Model",
        description = "Locally-hosted models via Ollama (Llama, Mistral, etc.)")
public class OllamaProvider implements ModelProvider {
    private static final Logger LOG = LoggerFactory.getLogger(OllamaProvider.class);

    /**
     * Creates a new Ollama chat model instance with the configured parameters.
     *
     * <p>This method creates an {@link OllamaChatModel} using the base URL and
     * model name from the configuration. The model is ready to use for chat
     * operations once created.
     *
     * @return a new configured Ollama chat model instance
     */
    @Override
    public ChatModel create(String id) {
        final OllamaConfig config = new OllamaConfig(id);

        String baseUrl = config.baseUrl();
        String modelName = config.modelName();
        Double temperature = config.temperature();
        Integer topK = config.topK();
        Double topP = config.topP();
        Double minP = config.minP();
        Integer numCtx = config.numCtx();
        Boolean logRequests = config.logRequests();
        Boolean logResponses = config.logResponses();

        LOG.trace(
                "Creating Ollama model: {} at {} with configuration: temperature={}, topK={}, topP={}, minP={}, numCtx={}, logRequests={}, logResponses={}",
                modelName,
                baseUrl,
                temperature,
                topK,
                topP,
                minP,
                numCtx,
                logRequests,
                logResponses);

        OllamaChatModel.OllamaChatModelBuilder builder =
                OllamaChatModel.builder().baseUrl(baseUrl).modelName(modelName);

        // Only set optional parameters if they are configured
        if (temperature != null) {
            builder.temperature(temperature);
        }

        if (topK != null) {
            builder.topK(topK);
        }

        if (topP != null) {
            builder.topP(topP);
        }

        if (minP != null) {
            builder.minP(minP);
        }

        if (numCtx != null) {
            builder.numCtx(numCtx);
        }

        if (logRequests != null) {
            builder.logRequests(logRequests);
        }

        if (logResponses != null) {
            builder.logResponses(logResponses);
        }

        return builder.build();
    }
}
