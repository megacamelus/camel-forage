package io.kaoto.forage.models.chat.dashscope;

import static io.kaoto.forage.models.chat.dashscope.DashscopeConfigEntries.API_KEY;
import static io.kaoto.forage.models.chat.dashscope.DashscopeConfigEntries.ENABLE_SEARCH;
import static io.kaoto.forage.models.chat.dashscope.DashscopeConfigEntries.LOG_REQUESTS_AND_RESPONSES;
import static io.kaoto.forage.models.chat.dashscope.DashscopeConfigEntries.MAX_RETRIES;
import static io.kaoto.forage.models.chat.dashscope.DashscopeConfigEntries.MAX_TOKENS;
import static io.kaoto.forage.models.chat.dashscope.DashscopeConfigEntries.MODEL_NAME;
import static io.kaoto.forage.models.chat.dashscope.DashscopeConfigEntries.REPETITION_PENALTY;
import static io.kaoto.forage.models.chat.dashscope.DashscopeConfigEntries.SEED;
import static io.kaoto.forage.models.chat.dashscope.DashscopeConfigEntries.TEMPERATURE;
import static io.kaoto.forage.models.chat.dashscope.DashscopeConfigEntries.TIMEOUT;
import static io.kaoto.forage.models.chat.dashscope.DashscopeConfigEntries.TOP_K;
import static io.kaoto.forage.models.chat.dashscope.DashscopeConfigEntries.TOP_P;

import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import io.kaoto.forage.core.util.config.MissingConfigException;
import java.util.Optional;

/**
 * Configuration class for Alibaba Dashscope Qwen integration in the Forage framework.
 *
 * <p>This configuration class manages the settings required to connect to and use Alibaba
 * Dashscope Qwen services. It handles authentication credentials, model selection, and
 * performance tuning parameters through environment variables with appropriate fallback mechanisms.
 *
 * <p><strong>Required Configuration:</strong>
 * <ul>
 *   <li><strong>DASHSCOPE_API_KEY</strong> - Your Dashscope API key for authentication</li>
 * </ul>
 *
 * <p><strong>Optional Configuration:</strong>
 * <ul>
 *   <li><strong>DASHSCOPE_MODEL_NAME</strong> - The Qwen model to use (default: "qwen-turbo")</li>
 *   <li><strong>DASHSCOPE_TEMPERATURE</strong> - Temperature for response generation (0.0-2.0)</li>
 *   <li><strong>DASHSCOPE_MAX_TOKENS</strong> - Maximum tokens in response</li>
 *   <li><strong>DASHSCOPE_TOP_P</strong> - Top-p sampling parameter (0.0-1.0)</li>
 *   <li><strong>DASHSCOPE_TOP_K</strong> - Top-k sampling parameter</li>
 *   <li><strong>DASHSCOPE_REPETITION_PENALTY</strong> - Repetition penalty (0.0-2.0)</li>
 *   <li><strong>DASHSCOPE_SEED</strong> - Seed for deterministic responses</li>
 *   <li><strong>DASHSCOPE_ENABLE_SEARCH</strong> - Enable web search functionality</li>
 *   <li><strong>DASHSCOPE_TIMEOUT</strong> - Request timeout in seconds (default: 60)</li>
 *   <li><strong>DASHSCOPE_MAX_RETRIES</strong> - Maximum retry attempts</li>
 *   <li><strong>DASHSCOPE_LOG_REQUESTS_AND_RESPONSES</strong> - Log requests and responses</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (DASHSCOPE_API_KEY, DASHSCOPE_MODEL_NAME, etc.)</li>
 *   <li>System properties (dashscope.api.key, dashscope.model.name, etc.)</li>
 *   <li>forage-model-dashscope.properties file in classpath</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables
 * export DASHSCOPE_API_KEY="your-api-key-here"
 * export DASHSCOPE_MODEL_NAME="qwen-max"
 *
 * // Create and use configuration
 * DashscopeConfig config = new DashscopeConfig();
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
public class DashscopeConfig implements Config {

    private final String prefix;

    /**
     * Constructs a new DashscopeConfig and registers configuration parameters with the ConfigStore.
     */
    public DashscopeConfig() {
        this(null);
    }

    public DashscopeConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        DashscopeConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(DashscopeConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        DashscopeConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = DashscopeConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    /**
     * Returns the unique identifier for this Dashscope configuration module.
     *
     * @return the module name "forage-model-dashscope"
     */
    @Override
    public String name() {
        return "forage-model-dashscope";
    }

    /**
     * Returns the Dashscope API key for authentication.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>DASHSCOPE_API_KEY environment variable</li>
     *   <li>dashscope.api.key system property</li>
     *   <li>api-key property in forage-model-dashscope.properties</li>
     * </ol>
     *
     * @return the Dashscope API key
     * @throws MissingConfigException if no API key is configured
     */
    public String apiKey() {
        return ConfigStore.getInstance()
                .get(API_KEY.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Dashscope API key"));
    }

    /**
     * Returns the Qwen model name to use.
     *
     * <p><strong>Common Model Names:</strong>
     * <ul>
     *   <li><strong>qwen-turbo</strong> - Fast and efficient model (default)</li>
     *   <li><strong>qwen-plus</strong> - Balanced performance and capabilities</li>
     *   <li><strong>qwen-max</strong> - Most capable model</li>
     *   <li><strong>qwen-max-longcontext</strong> - Extended context window</li>
     * </ul>
     *
     * @return the model name, defaults to "qwen-turbo" if not configured
     */
    public String modelName() {
        return ConfigStore.getInstance().get(MODEL_NAME.asNamed(prefix)).orElse(MODEL_NAME.defaultValue());
    }

    /**
     * Returns the temperature setting for response generation.
     *
     * <p>Temperature controls the randomness of the model's output.
     *
     * <p><strong>Value Range:</strong> 0.0 to 2.0
     * <ul>
     *   <li><strong>0.0</strong> - Most deterministic, focused responses</li>
     *   <li><strong>0.7</strong> - Balanced creativity and focus</li>
     *   <li><strong>1.0</strong> - Default setting</li>
     *   <li><strong>2.0</strong> - Maximum creativity and randomness</li>
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
     * Returns the repetition penalty for discouraging token repetition.
     *
     * <p><strong>Value Range:</strong> 0.0 to 2.0
     * <ul>
     *   <li><strong>Values < 1.0</strong> - Encourage repetition</li>
     *   <li><strong>1.0</strong> - No penalty (default)</li>
     *   <li><strong>Values > 1.0</strong> - Discourage repetition</li>
     * </ul>
     *
     * @return the repetition penalty value, or null if not configured (uses service default)
     */
    public Double repetitionPenalty() {
        return ConfigStore.getInstance()
                .get(REPETITION_PENALTY.asNamed(prefix))
                .map(Double::parseDouble)
                .orElse(null);
    }

    /**
     * Returns the seed for deterministic response generation.
     *
     * @return the seed value, or null if not configured (non-deterministic mode)
     */
    public Long seed() {
        return ConfigStore.getInstance()
                .get(SEED.asNamed(prefix))
                .map(Long::parseLong)
                .orElse(null);
    }

    /**
     * Returns whether web search functionality is enabled.
     *
     * <p>When enabled, the model can access web search results to provide
     * more up-to-date and accurate information.
     *
     * @return true if search is enabled, false if disabled, or null if not configured
     */
    public Boolean enableSearch() {
        return ConfigStore.getInstance()
                .get(ENABLE_SEARCH.asNamed(prefix))
                .map(Boolean::parseBoolean)
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
