package io.kaoto.forage.models.chat.anthropic;

import static io.kaoto.forage.models.chat.anthropic.AnthropicConfigEntries.API_KEY;
import static io.kaoto.forage.models.chat.anthropic.AnthropicConfigEntries.LOG_REQUESTS_AND_RESPONSES;
import static io.kaoto.forage.models.chat.anthropic.AnthropicConfigEntries.MAX_RETRIES;
import static io.kaoto.forage.models.chat.anthropic.AnthropicConfigEntries.MAX_TOKENS;
import static io.kaoto.forage.models.chat.anthropic.AnthropicConfigEntries.MODEL_NAME;
import static io.kaoto.forage.models.chat.anthropic.AnthropicConfigEntries.STOP_SEQUENCES;
import static io.kaoto.forage.models.chat.anthropic.AnthropicConfigEntries.TEMPERATURE;
import static io.kaoto.forage.models.chat.anthropic.AnthropicConfigEntries.TIMEOUT;
import static io.kaoto.forage.models.chat.anthropic.AnthropicConfigEntries.TOP_K;
import static io.kaoto.forage.models.chat.anthropic.AnthropicConfigEntries.TOP_P;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import io.kaoto.forage.core.util.config.MissingConfigException;

/**
 * Configuration class for Anthropic Claude integration in the Forage framework.
 *
 * <p>This configuration class manages the settings required to connect to and use Anthropic
 * Claude services. It handles authentication credentials, model selection, and
 * performance tuning parameters through environment variables with appropriate fallback mechanisms.
 *
 * <p><strong>Required Configuration:</strong>
 * <ul>
 *   <li><strong>ANTHROPIC_API_KEY</strong> - Your Anthropic API key for authentication</li>
 * </ul>
 *
 * <p><strong>Optional Configuration:</strong>
 * <ul>
 *   <li><strong>ANTHROPIC_MODEL_NAME</strong> - The Claude model to use (default: "claude-3-haiku-20240307")</li>
 *   <li><strong>ANTHROPIC_TEMPERATURE</strong> - Temperature for response generation (0.0-1.0)</li>
 *   <li><strong>ANTHROPIC_MAX_TOKENS</strong> - Maximum tokens in response</li>
 *   <li><strong>ANTHROPIC_TOP_P</strong> - Top-p sampling parameter (0.0-1.0)</li>
 *   <li><strong>ANTHROPIC_TOP_K</strong> - Top-k sampling parameter</li>
 *   <li><strong>ANTHROPIC_STOP_SEQUENCES</strong> - Stop sequences (comma-separated)</li>
 *   <li><strong>ANTHROPIC_TIMEOUT</strong> - Request timeout in seconds (default: 60)</li>
 *   <li><strong>ANTHROPIC_MAX_RETRIES</strong> - Maximum retry attempts</li>
 *   <li><strong>ANTHROPIC_LOG_REQUESTS_AND_RESPONSES</strong> - Log requests and responses</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (ANTHROPIC_API_KEY, ANTHROPIC_MODEL_NAME, etc.)</li>
 *   <li>System properties (anthropic.api.key, anthropic.model.name, etc.)</li>
 *   <li>forage-model-anthropic.properties file in classpath</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables
 * export ANTHROPIC_API_KEY="your-api-key-here"
 * export ANTHROPIC_MODEL_NAME="claude-3-sonnet-20240229"
 *
 * // Create and use configuration
 * AnthropicConfig config = new AnthropicConfig();
 * String apiKey = config.apiKey();        // Returns the configured API key
 * String modelName = config.modelName();  // Returns the configured model name
 * }</pre>
 *
 * <p><strong>Security Considerations:</strong>
 * The API key is sensitive information and should be properly secured. Never commit API keys
 * to version control. Use environment variables or secure configuration management systems
 * in production environments.
 *
 * @see Config
 * @see ConfigStore
 * @see ConfigModule
 * @since 1.0
 */
public class AnthropicConfig implements Config {

    private final String prefix;

    /**
     * Constructs a new AnthropicConfig and registers configuration parameters with the ConfigStore.
     */
    public AnthropicConfig() {
        this(null);
    }

    public AnthropicConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        AnthropicConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(AnthropicConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        AnthropicConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = AnthropicConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    /**
     * Returns the unique identifier for this Anthropic configuration module.
     *
     * @return the module name "forage-model-anthropic"
     */
    @Override
    public String name() {
        return "forage-model-anthropic";
    }

    /**
     * Returns the Anthropic API key for authentication.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>ANTHROPIC_API_KEY environment variable</li>
     *   <li>anthropic.api.key system property</li>
     *   <li>api-key property in forage-model-anthropic.properties</li>
     * </ol>
     *
     * @return the Anthropic API key
     * @throws MissingConfigException if no API key is configured
     */
    public String apiKey() {
        return ConfigStore.getInstance()
                .get(API_KEY.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Anthropic API key"));
    }

    /**
     * Returns the Claude model name to use.
     *
     * <p><strong>Common Model Names:</strong>
     * <ul>
     *   <li><strong>claude-3-haiku-20240307</strong> - Fast and efficient model (default)</li>
     *   <li><strong>claude-3-sonnet-20240229</strong> - Balanced performance and capabilities</li>
     *   <li><strong>claude-3-opus-20240229</strong> - Most capable model</li>
     *   <li><strong>claude-3-5-sonnet-20241022</strong> - Latest Sonnet model</li>
     * </ul>
     *
     * @return the model name, defaults to "claude-3-haiku-20240307" if not configured
     */
    public String modelName() {
        return ConfigStore.getInstance().get(MODEL_NAME.asNamed(prefix)).orElse(MODEL_NAME.defaultValue());
    }

    /**
     * Returns the temperature setting for response generation.
     *
     * <p>Temperature controls the randomness of the model's output.
     *
     * <p><strong>Value Range:</strong> 0.0 to 1.0
     * <ul>
     *   <li><strong>0.0</strong> - Most deterministic, focused responses</li>
     *   <li><strong>0.5</strong> - Balanced creativity and focus</li>
     *   <li><strong>1.0</strong> - Maximum creativity and randomness</li>
     * </ul>
     *
     * @return the temperature value, or null if not configured (uses service default)
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
     * @return the maximum tokens limit, or null if not configured (uses service default)
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
     * <p><strong>Value Range:</strong> 0.0 to 1.0
     *
     * @return the top-p value, or null if not configured (uses service default)
     */
    public Double topP() {
        return ConfigStore.getInstance()
                .get(TOP_P.asNamed(prefix))
                .map(Double::parseDouble)
                .orElse(null);
    }

    /**
     * Returns the top-k sampling parameter.
     *
     * <p>Limits the model to consider only the top-k most probable tokens.
     *
     * @return the top-k value, or null if not configured (uses service default)
     */
    public Integer topK() {
        return ConfigStore.getInstance()
                .get(TOP_K.asNamed(prefix))
                .map(Integer::parseInt)
                .orElse(null);
    }

    /**
     * Returns the stop sequences for response generation.
     *
     * <p>Stop sequences are strings that, when encountered, will cause the model
     * to stop generating further tokens.
     *
     * @return the stop sequences list, or null if not configured
     */
    public List<String> stopSequences() {
        return ConfigStore.getInstance()
                .get(STOP_SEQUENCES.asNamed(prefix))
                .map(sequences -> Arrays.asList(sequences.split(",")))
                .orElse(null);
    }

    /**
     * Returns the request timeout duration in seconds.
     *
     * @return the timeout in seconds, or null if not configured (uses default of 60 seconds)
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
     * @return the maximum retry attempts, or null if not configured (uses service default)
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
     * <p><strong>Security Note:</strong> Be cautious when enabling in production
     * as this may log sensitive data including API keys and user content.
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
