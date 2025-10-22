package org.apache.camel.forage.models.chat.mistralai;

import static org.apache.camel.forage.models.chat.mistralai.MistralAiConfigEntries.API_KEY;
import static org.apache.camel.forage.models.chat.mistralai.MistralAiConfigEntries.LOG_REQUESTS_AND_RESPONSES;
import static org.apache.camel.forage.models.chat.mistralai.MistralAiConfigEntries.MAX_RETRIES;
import static org.apache.camel.forage.models.chat.mistralai.MistralAiConfigEntries.MAX_TOKENS;
import static org.apache.camel.forage.models.chat.mistralai.MistralAiConfigEntries.MODEL_NAME;
import static org.apache.camel.forage.models.chat.mistralai.MistralAiConfigEntries.RANDOM_SEED;
import static org.apache.camel.forage.models.chat.mistralai.MistralAiConfigEntries.TEMPERATURE;
import static org.apache.camel.forage.models.chat.mistralai.MistralAiConfigEntries.TIMEOUT;
import static org.apache.camel.forage.models.chat.mistralai.MistralAiConfigEntries.TOP_P;

import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

/**
 * Configuration class for MistralAI integration in the Camel Forage framework.
 *
 * <p>This configuration class manages the settings required to connect to and use MistralAI
 * services. MistralAI provides large language models accessible via their API.
 *
 * <p><strong>Required Configuration:</strong>
 * <ul>
 *   <li><strong>MISTRALAI_API_KEY</strong> - The MistralAI API key</li>
 * </ul>
 *
 * <p><strong>Optional Configuration:</strong>
 * <ul>
 *   <li><strong>MISTRALAI_MODEL_NAME</strong> - The model to use (defaults to "mistral-large-latest")</li>
 *   <li><strong>MISTRALAI_TEMPERATURE</strong> - Controls randomness (0.0-1.0)</li>
 *   <li><strong>MISTRALAI_MAX_TOKENS</strong> - Maximum tokens in response</li>
 *   <li><strong>MISTRALAI_TOP_P</strong> - Nucleus sampling parameter (0.0-1.0)</li>
 *   <li><strong>MISTRALAI_RANDOM_SEED</strong> - Random seed for reproducible results</li>
 *   <li><strong>MISTRALAI_TIMEOUT</strong> - Request timeout in seconds</li>
 *   <li><strong>MISTRALAI_MAX_RETRIES</strong> - Maximum retry attempts</li>
 *   <li><strong>MISTRALAI_LOG_REQUESTS_AND_RESPONSES</strong> - Enable request/response logging</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (MISTRALAI_API_KEY, MISTRALAI_MODEL_NAME, etc.)</li>
 *   <li>System properties (mistralai.api.key, mistralai.model.name, etc.)</li>
 *   <li>forage-model-mistral-ai.properties file in classpath</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables
 * export MISTRALAI_API_KEY="your-api-key"
 * export MISTRALAI_MODEL_NAME="mistral-large-latest"
 *
 * // Create and use configuration
 * MistralAiConfig config = new MistralAiConfig();
 * String apiKey = config.apiKey();     // Returns the configured API key
 * String modelName = config.modelName(); // Returns the configured model name
 * }</pre>
 *
 * <p><strong>Security Considerations:</strong>
 * Never commit API keys to version control. Use environment variables or secure configuration
 * management systems for production deployments.
 *
 * @see Config
 * @see ConfigStore
 * @see ConfigModule
 * @since 1.0
 */
public class MistralAiConfig implements Config {

    private final String prefix;

    /**
     * Constructs a new MistralAiConfig and registers configuration parameters with the ConfigStore.
     */
    public MistralAiConfig() {
        this(null);
    }

    public MistralAiConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        MistralAiConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(MistralAiConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        MistralAiConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = MistralAiConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    /**
     * Returns the unique identifier for this MistralAI configuration module.
     *
     * @return the module name "forage-model-mistral-ai"
     */
    @Override
    public String name() {
        return "forage-model-mistral-ai";
    }

    /**
     * Returns the MistralAI API key for authentication.
     *
     * <p>This is required to authenticate with the MistralAI API.
     * You can obtain an API key from the MistralAI platform.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>MISTRALAI_API_KEY environment variable</li>
     *   <li>mistralai.api.key system property</li>
     *   <li>api-key property in forage-model-mistral-ai.properties</li>
     * </ol>
     *
     * @return the MistralAI API key
     * @throws MissingConfigException if no API key is configured
     */
    public String apiKey() {
        return ConfigStore.getInstance()
                .get(API_KEY.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing MistralAI API key"));
    }

    /**
     * Returns the name of the model to use.
     *
     * <p>MistralAI offers various models with different capabilities and pricing.
     *
     * <p><strong>Common Model Examples:</strong>
     * <ul>
     *   <li><strong>mistral-large-latest</strong> - Latest large model (default)</li>
     *   <li><strong>mistral-medium</strong> - Medium-sized model</li>
     *   <li><strong>mistral-small</strong> - Small, fast model</li>
     *   <li><strong>mistral-tiny</strong> - Smallest, fastest model</li>
     * </ul>
     *
     * @return the model name, defaults to "mistral-large-latest" if not configured
     */
    public String modelName() {
        return ConfigStore.getInstance().get(MODEL_NAME.asNamed(prefix)).orElse(MODEL_NAME.defaultValue());
    }

    /**
     * Returns the temperature setting for response generation.
     *
     * <p>Temperature controls the randomness of the model's output. Lower values make responses
     * more focused and deterministic, while higher values make them more creative and varied.
     *
     * <p><strong>Value Range:</strong> 0.0 to 1.0
     *
     * @return the temperature value, or null if not configured
     */
    public Double temperature() {
        return ConfigStore.getInstance()
                .get(TEMPERATURE.asNamed(prefix))
                .map(Double::parseDouble)
                .orElse(null);
    }

    /**
     * Returns the maximum number of tokens for model responses.
     *
     * <p>This setting limits the length of the model's response.
     *
     * @return the maximum tokens limit, or null if not configured
     */
    public Integer maxTokens() {
        return ConfigStore.getInstance()
                .get(MAX_TOKENS.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(null);
    }

    /**
     * Returns the top-p (nucleus sampling) probability threshold.
     *
     * <p>An alternative to temperature for controlling response diversity.
     *
     * <p><strong>Value Range:</strong> 0.0 to 1.0
     *
     * @return the top-p value, or null if not configured
     */
    public Double topP() {
        return ConfigStore.getInstance()
                .get(TOP_P.asNamed(prefix))
                .map(Double::parseDouble)
                .orElse(null);
    }

    /**
     * Returns the random seed for deterministic response generation.
     *
     * @return the random seed value, or null if not configured
     */
    public Integer randomSeed() {
        return ConfigStore.getInstance()
                .get(RANDOM_SEED.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(null);
    }

    /**
     * Returns the request timeout duration in seconds.
     *
     * @return the timeout in seconds, or null if not configured
     */
    public Integer timeoutSeconds() {
        return ConfigStore.getInstance()
                .get(TIMEOUT.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(null);
    }

    /**
     * Returns the maximum number of retry attempts for failed requests.
     *
     * @return the maximum retry attempts, or null if not configured
     */
    public Integer maxRetries() {
        return ConfigStore.getInstance()
                .get(MAX_RETRIES.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(null);
    }

    /**
     * Returns whether to log both request and response details.
     *
     * @return true if request and response logging is enabled, false if disabled, or null if not configured
     */
    public Boolean logRequestsAndResponses() {
        return ConfigStore.getInstance()
                .get(LOG_REQUESTS_AND_RESPONSES.asNamed(prefix))
                .map(Boolean::parseBoolean)
                .orElse(null);
    }
}
