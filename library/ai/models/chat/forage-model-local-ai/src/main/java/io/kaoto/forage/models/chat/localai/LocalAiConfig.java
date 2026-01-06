package io.kaoto.forage.models.chat.localai;

import static io.kaoto.forage.models.chat.localai.LocalAiConfigEntries.API_KEY;
import static io.kaoto.forage.models.chat.localai.LocalAiConfigEntries.BASE_URL;
import static io.kaoto.forage.models.chat.localai.LocalAiConfigEntries.FREQUENCY_PENALTY;
import static io.kaoto.forage.models.chat.localai.LocalAiConfigEntries.LOG_REQUESTS_AND_RESPONSES;
import static io.kaoto.forage.models.chat.localai.LocalAiConfigEntries.MAX_RETRIES;
import static io.kaoto.forage.models.chat.localai.LocalAiConfigEntries.MAX_TOKENS;
import static io.kaoto.forage.models.chat.localai.LocalAiConfigEntries.MODEL_NAME;
import static io.kaoto.forage.models.chat.localai.LocalAiConfigEntries.PRESENCE_PENALTY;
import static io.kaoto.forage.models.chat.localai.LocalAiConfigEntries.SEED;
import static io.kaoto.forage.models.chat.localai.LocalAiConfigEntries.TEMPERATURE;
import static io.kaoto.forage.models.chat.localai.LocalAiConfigEntries.TIMEOUT;
import static io.kaoto.forage.models.chat.localai.LocalAiConfigEntries.TOP_P;
import static io.kaoto.forage.models.chat.localai.LocalAiConfigEntries.USER;

import java.util.Optional;
import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import io.kaoto.forage.core.util.config.MissingConfigException;

/**
 * Configuration class for LocalAI integration in the Forage framework.
 *
 * <p>This configuration class manages the settings required to connect to and use LocalAI
 * services. LocalAI is a drop-in replacement for OpenAI API that runs models locally,
 * providing privacy and cost benefits while maintaining OpenAI API compatibility.
 *
 * <p><strong>Required Configuration:</strong>
 * <ul>
 *   <li><strong>LOCALAI_BASE_URL</strong> - The LocalAI server endpoint URL</li>
 * </ul>
 *
 * <p><strong>Optional Configuration:</strong>
 * <ul>
 *   <li><strong>LOCALAI_API_KEY</strong> - API key if LocalAI server requires authentication</li>
 *   <li><strong>LOCALAI_MODEL_NAME</strong> - The model to use (must be available on LocalAI server)</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (LOCALAI_BASE_URL, LOCALAI_MODEL_NAME, etc.)</li>
 *   <li>System properties (localai.base.url, localai.model.name, etc.)</li>
 *   <li>forage-model-local-ai.properties file in classpath</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables
 * export LOCALAI_BASE_URL="http://localhost:8080"
 * export LOCALAI_MODEL_NAME="gpt-3.5-turbo"
 *
 * // Create and use configuration
 * LocalAiConfig config = new LocalAiConfig();
 * String baseUrl = config.baseUrl();     // Returns the configured base URL
 * String modelName = config.modelName(); // Returns the configured model name
 * }</pre>
 *
 * <p><strong>Security Considerations:</strong>
 * LocalAI typically runs locally, but if using authentication, ensure API keys are properly secured.
 * Never commit API keys to version control.
 *
 * @see Config
 * @see ConfigStore
 * @see ConfigModule
 * @since 1.0
 */
public class LocalAiConfig implements Config {

    private final String prefix;

    /**
     * Constructs a new LocalAiConfig and registers configuration parameters with the ConfigStore.
     */
    public LocalAiConfig() {
        this(null);
    }

    public LocalAiConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        LocalAiConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(LocalAiConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        LocalAiConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = LocalAiConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    /**
     * Returns the unique identifier for this LocalAI configuration module.
     *
     * @return the module name "forage-model-local-ai"
     */
    @Override
    public String name() {
        return "forage-model-local-ai";
    }

    /**
     * Returns the LocalAI API key for authentication.
     *
     * <p>This is optional for LocalAI installations that don't require authentication.
     * Many LocalAI setups run without API key requirements for local development.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>LOCALAI_API_KEY environment variable</li>
     *   <li>localai.api.key system property</li>
     *   <li>api-key property in forage-model-local-ai.properties</li>
     * </ol>
     *
     * @return the LocalAI API key, or null if not configured
     */
    public String apiKey() {
        return ConfigStore.getInstance().get(API_KEY.asNamed(prefix)).orElse(null);
    }

    /**
     * Returns the LocalAI server base URL.
     *
     * <p>This is the endpoint where your LocalAI server is running.
     * LocalAI typically runs on localhost during development.
     *
     * <p><strong>Example URLs:</strong>
     * <ul>
     *   <li><strong>http://localhost:8080</strong> - Default LocalAI installation</li>
     *   <li><strong>http://localai:8080</strong> - Docker container setup</li>
     *   <li><strong>https://your-localai-server.com</strong> - Remote LocalAI instance</li>
     * </ul>
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>LOCALAI_BASE_URL environment variable</li>
     *   <li>localai.base.url system property</li>
     *   <li>base-url property in forage-model-local-ai.properties</li>
     * </ol>
     *
     * @return the LocalAI base URL
     * @throws MissingConfigException if no base URL is configured
     */
    public String baseUrl() {
        return ConfigStore.getInstance()
                .get(BASE_URL.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing LocalAI base URL"));
    }

    /**
     * Returns the name of the model to use.
     *
     * <p>This should correspond to a model that is available on your LocalAI server.
     * LocalAI supports various model formats and can load models on demand.
     *
     * <p><strong>Common Model Examples:</strong>
     * <ul>
     *   <li><strong>gpt-3.5-turbo</strong> - OpenAI-compatible model name</li>
     *   <li><strong>gpt-4</strong> - Another OpenAI-compatible name</li>
     *   <li><strong>llama2</strong> - Direct model reference</li>
     *   <li><strong>mistral</strong> - Mistral model</li>
     * </ul>
     *
     * @return the model name, or null if not configured
     */
    public String modelName() {
        return ConfigStore.getInstance().get(MODEL_NAME.asNamed(prefix)).orElse(null);
    }

    /**
     * Returns the temperature setting for response generation.
     *
     * <p>Temperature controls the randomness of the model's output. Lower values make responses
     * more focused and deterministic, while higher values make them more creative and varied.
     *
     * <p><strong>Value Range:</strong> 0.0 to 2.0
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
     * Returns the presence penalty for discouraging new topic introduction.
     *
     * <p><strong>Value Range:</strong> -2.0 to 2.0
     *
     * @return the presence penalty value, or null if not configured
     */
    public Double presencePenalty() {
        return ConfigStore.getInstance()
                .get(PRESENCE_PENALTY.asNamed(prefix))
                .map(Double::parseDouble)
                .orElse(null);
    }

    /**
     * Returns the frequency penalty for discouraging token repetition.
     *
     * <p><strong>Value Range:</strong> -2.0 to 2.0
     *
     * @return the frequency penalty value, or null if not configured
     */
    public Double frequencyPenalty() {
        return ConfigStore.getInstance()
                .get(FREQUENCY_PENALTY.asNamed(prefix))
                .map(Double::parseDouble)
                .orElse(null);
    }

    /**
     * Returns the seed for deterministic response generation.
     *
     * @return the seed value, or null if not configured
     */
    public Long seed() {
        return ConfigStore.getInstance()
                .get(SEED.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(null);
    }

    /**
     * Returns the user identifier for tracking and monitoring.
     *
     * @return the user identifier, or null if not configured
     */
    public String user() {
        return ConfigStore.getInstance().get(USER.asNamed(prefix)).orElse(null);
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
