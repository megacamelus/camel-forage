package org.apache.camel.forage.models.chat.openai;

import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

/**
 * Configuration class for OpenAI model integration in the Camel Forage framework.
 *
 * <p>This configuration class manages the settings required to connect to and use OpenAI
 * models. It handles authentication credentials, model selection, and various parameters
 * through environment variables with appropriate fallback mechanisms.
 *
 * <p><strong>Required Configuration:</strong>
 * <ul>
 *   <li><strong>OPENAI_API_KEY</strong> - Your OpenAI API key for authentication</li>
 * </ul>
 *
 * <p><strong>Optional Configuration:</strong>
 * <ul>
 *   <li><strong>OPENAI_MODEL_NAME</strong> - The specific OpenAI model to use (default: "gpt-3.5-turbo")</li>
 *   <li><strong>OPENAI_BASE_URL</strong> - Custom base URL for OpenAI API (default: OpenAI's standard API)</li>
 *   <li><strong>OPENAI_TEMPERATURE</strong> - Temperature for response randomness, 0.0-2.0 (no default)</li>
 *   <li><strong>OPENAI_MAX_TOKENS</strong> - Maximum tokens to generate (no default)</li>
 *   <li><strong>OPENAI_TOP_P</strong> - Top-P (nucleus) sampling parameter, 0.0-1.0 (no default)</li>
 *   <li><strong>OPENAI_FREQUENCY_PENALTY</strong> - Frequency penalty, -2.0 to 2.0 (no default)</li>
 *   <li><strong>OPENAI_PRESENCE_PENALTY</strong> - Presence penalty, -2.0 to 2.0 (no default)</li>
 *   <li><strong>OPENAI_LOG_REQUESTS</strong> - Enable request logging, true/false (no default)</li>
 *   <li><strong>OPENAI_LOG_RESPONSES</strong> - Enable response logging, true/false (no default)</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (OPENAI_API_KEY, OPENAI_MODEL_NAME, etc.)</li>
 *   <li>System properties (openai.api.key, openai.model.name, etc.)</li>
 *   <li>camel-forage-model-open-ai.properties file in classpath</li>
 *   <li>Default values if none of the above are provided (only for model name)</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables
 * export OPENAI_API_KEY="your-api-key-here"
 * export OPENAI_MODEL_NAME="gpt-4"
 * export OPENAI_TEMPERATURE="0.7"
 * export OPENAI_MAX_TOKENS="1000"
 *
 * // Create and use configuration
 * OpenAIConfig config = new OpenAIConfig();
 * String apiKey = config.apiKey();        // Returns the configured API key
 * String model = config.modelName();      // Returns the configured model name
 * Double temperature = config.temperature(); // Returns 0.7 or null if not set
 * }</pre>
 *
 * <p><strong>Security Considerations:</strong>
 * The API key is sensitive information and should be properly secured. Never commit API keys
 * to version control. Use environment variables or secure configuration management systems
 * in production environments.
 *
 * <p>This class automatically registers itself and its configuration parameters with the
 * {@link ConfigStore} during construction, making the configuration values available
 * to other components in the framework.
 *
 * @see Config
 * @see ConfigStore
 * @see ConfigModule
 * @since 1.0
 */
public class OpenAIConfig implements Config {

    private static final ConfigModule API_KEY = ConfigModule.of(OpenAIConfig.class, "openai.api.key");
    private static final ConfigModule MODEL_NAME = ConfigModule.of(OpenAIConfig.class, "openai.model.name");
    private static final ConfigModule BASE_URL = ConfigModule.of(OpenAIConfig.class, "openai.base.url");
    private static final ConfigModule TEMPERATURE = ConfigModule.of(OpenAIConfig.class, "openai.temperature");
    private static final ConfigModule MAX_TOKENS = ConfigModule.of(OpenAIConfig.class, "openai.max.tokens");
    private static final ConfigModule TOP_P = ConfigModule.of(OpenAIConfig.class, "openai.top.p");
    private static final ConfigModule FREQUENCY_PENALTY =
            ConfigModule.of(OpenAIConfig.class, "openai.frequency.penalty");
    private static final ConfigModule PRESENCE_PENALTY = ConfigModule.of(OpenAIConfig.class, "openai.presence.penalty");
    private static final ConfigModule LOG_REQUESTS = ConfigModule.of(OpenAIConfig.class, "openai.log.requests");
    private static final ConfigModule LOG_RESPONSES = ConfigModule.of(OpenAIConfig.class, "openai.log.responses");

    private static final String DEFAULT_MODEL_NAME = "gpt-3.5-turbo";

    /**
     * Constructs a new OpenAIConfig and registers configuration parameters with the ConfigStore.
     *
     * <p>During construction, this class:
     * <ul>
     *   <li>Registers the API key configuration to be sourced from OPENAI_API_KEY environment variable</li>
     *   <li>Registers the model name configuration to be sourced from OPENAI_MODEL_NAME environment variable</li>
     *   <li>Registers optional parameters (base URL, temperature, max tokens, etc.) from their respective environment variables</li>
     *   <li>Attempts to load additional properties from camel-forage-model-open-ai.properties</li>
     * </ul>
     *
     * <p>Configuration values are resolved when this constructor is called, with default values
     * used when no configuration is provided through environment variables, system properties,
     * or configuration files (only for model name).
     */
    public OpenAIConfig() {
        ConfigStore.getInstance().add(API_KEY, ConfigEntry.fromModule(API_KEY, "OPENAI_API_KEY"));
        ConfigStore.getInstance().add(MODEL_NAME, ConfigEntry.fromModule(MODEL_NAME, "OPENAI_MODEL_NAME"));
        ConfigStore.getInstance().add(BASE_URL, ConfigEntry.fromModule(BASE_URL, "OPENAI_BASE_URL"));
        ConfigStore.getInstance().add(TEMPERATURE, ConfigEntry.fromModule(TEMPERATURE, "OPENAI_TEMPERATURE"));
        ConfigStore.getInstance().add(MAX_TOKENS, ConfigEntry.fromModule(MAX_TOKENS, "OPENAI_MAX_TOKENS"));
        ConfigStore.getInstance().add(TOP_P, ConfigEntry.fromModule(TOP_P, "OPENAI_TOP_P"));
        ConfigStore.getInstance()
                .add(FREQUENCY_PENALTY, ConfigEntry.fromModule(FREQUENCY_PENALTY, "OPENAI_FREQUENCY_PENALTY"));
        ConfigStore.getInstance()
                .add(PRESENCE_PENALTY, ConfigEntry.fromModule(PRESENCE_PENALTY, "OPENAI_PRESENCE_PENALTY"));
        ConfigStore.getInstance().add(LOG_REQUESTS, ConfigEntry.fromModule(LOG_REQUESTS, "OPENAI_LOG_REQUESTS"));
        ConfigStore.getInstance().add(LOG_RESPONSES, ConfigEntry.fromModule(LOG_RESPONSES, "OPENAI_LOG_RESPONSES"));
        ConfigStore.getInstance().add(OpenAIConfig.class, this, this::register);
    }

    private ConfigModule resolve(String name) {
        if (API_KEY.name().equals(name)) {
            return API_KEY;
        }

        if (MODEL_NAME.name().equals(name)) {
            return MODEL_NAME;
        }

        if (BASE_URL.name().equals(name)) {
            return BASE_URL;
        }

        if (TEMPERATURE.name().equals(name)) {
            return TEMPERATURE;
        }

        if (MAX_TOKENS.name().equals(name)) {
            return MAX_TOKENS;
        }

        if (TOP_P.name().equals(name)) {
            return TOP_P;
        }

        if (FREQUENCY_PENALTY.name().equals(name)) {
            return FREQUENCY_PENALTY;
        }

        if (PRESENCE_PENALTY.name().equals(name)) {
            return PRESENCE_PENALTY;
        }

        if (LOG_REQUESTS.name().equals(name)) {
            return LOG_REQUESTS;
        }

        if (LOG_RESPONSES.name().equals(name)) {
            return LOG_RESPONSES;
        }

        throw new IllegalArgumentException("Unknown config entry: " + name);
    }

    @Override
    public void register(String name, String value) {
        ConfigModule config = resolve(name);

        ConfigStore.getInstance().set(config, value);
    }

    /**
     * Returns the unique identifier for this OpenAI configuration module.
     *
     * <p>This name corresponds to the module artifact and is used for:
     * <ul>
     *   <li>Loading configuration files (camel-forage-model-open-ai.properties)</li>
     *   <li>Identifying this module in logs and error messages</li>
     *   <li>Distinguishing this configuration from other AI model configurations</li>
     * </ul>
     *
     * @return the module name "camel-forage-model-open-ai"
     */
    @Override
    public String name() {
        return "forage-model-open-ai";
    }

    /**
     * Returns the OpenAI API key for authentication.
     *
     * <p>This method retrieves the API key that was configured through environment variables,
     * system properties, or configuration files. The API key is required for all interactions
     * with OpenAI's services.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OPENAI_API_KEY environment variable</li>
     *   <li>openai.api.key system property</li>
     *   <li>api-key property in camel-forage-model-open-ai.properties</li>
     * </ol>
     *
     * @return the OpenAI API key
     * @throws MissingConfigException if no API key is configured
     */
    public String apiKey() {
        return ConfigStore.getInstance()
                .get(API_KEY)
                .orElseThrow(() -> new MissingConfigException("Missing OpenAI API key"));
    }

    /**
     * Returns the name of the OpenAI model to use.
     *
     * <p>This method retrieves the model name that specifies which OpenAI model
     * should be used for AI operations. Different models have different capabilities,
     * performance characteristics, and pricing.
     *
     * <p><strong>Common Model Names:</strong>
     * <ul>
     *   <li><strong>gpt-3.5-turbo</strong> - Cost-effective model for most tasks</li>
     *   <li><strong>gpt-4</strong> - More capable model for complex tasks</li>
     *   <li><strong>gpt-4-turbo</strong> - Latest GPT-4 with improved performance</li>
     *   <li><strong>gpt-4o</strong> - Optimized model with multimodal capabilities</li>
     * </ul>
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OPENAI_MODEL_NAME environment variable</li>
     *   <li>openai.model.name system property</li>
     *   <li>model-name property in camel-forage-model-open-ai.properties</li>
     *   <li>Default value: "gpt-3.5-turbo"</li>
     * </ol>
     *
     * @return the OpenAI model name, never null
     */
    public String modelName() {
        return ConfigStore.getInstance().get(MODEL_NAME).orElse(DEFAULT_MODEL_NAME);
    }

    /**
     * Returns the base URL for the OpenAI API.
     *
     * <p>This method retrieves the base URL for OpenAI API calls. This is typically
     * used when connecting to OpenAI-compatible services or when using a custom proxy.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OPENAI_BASE_URL environment variable</li>
     *   <li>openai.base.url system property</li>
     *   <li>base-url property in camel-forage-model-open-ai.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * @return the base URL, or null if not configured (uses OpenAI's default)
     */
    public String baseUrl() {
        return ConfigStore.getInstance().get(BASE_URL).orElse(null);
    }

    /**
     * Returns the temperature setting for response randomness.
     *
     * <p>Temperature controls the randomness of the model's responses. Lower values make
     * the output more focused and deterministic, while higher values increase creativity
     * and randomness.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OPENAI_TEMPERATURE environment variable</li>
     *   <li>openai.temperature system property</li>
     *   <li>temperature property in camel-forage-model-open-ai.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * <p><strong>Valid Range:</strong> 0.0 to 2.0 (typical range is 0.0 to 1.0)
     *
     * @return the temperature value, or null if not configured
     */
    public Double temperature() {
        return ConfigStore.getInstance()
                .get(TEMPERATURE)
                .map(Double::parseDouble)
                .orElse(null);
    }

    /**
     * Returns the maximum number of tokens to generate.
     *
     * <p>This parameter controls the maximum length of the generated response.
     * The total length of input tokens and generated tokens is limited by the
     * model's context length.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OPENAI_MAX_TOKENS environment variable</li>
     *   <li>openai.max.tokens system property</li>
     *   <li>max-tokens property in camel-forage-model-open-ai.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * <p><strong>Valid Range:</strong> Positive integers (model-dependent maximum)
     *
     * @return the maximum tokens value, or null if not configured
     */
    public Integer maxTokens() {
        return ConfigStore.getInstance().get(MAX_TOKENS).map(Integer::parseInt).orElse(null);
    }

    /**
     * Returns the top-P (nucleus) sampling parameter.
     *
     * <p>Top-P sampling selects from the smallest set of tokens whose cumulative
     * probability exceeds P. This provides dynamic vocabulary filtering based
     * on the probability distribution.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OPENAI_TOP_P environment variable</li>
     *   <li>openai.top.p system property</li>
     *   <li>top-p property in camel-forage-model-open-ai.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * <p><strong>Valid Range:</strong> 0.0 to 1.0
     *
     * @return the top-P value, or null if not configured
     */
    public Double topP() {
        return ConfigStore.getInstance().get(TOP_P).map(Double::parseDouble).orElse(null);
    }

    /**
     * Returns the frequency penalty parameter.
     *
     * <p>Frequency penalty reduces the likelihood of repeating the same line of text.
     * Positive values penalize tokens based on their frequency in the text so far.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OPENAI_FREQUENCY_PENALTY environment variable</li>
     *   <li>openai.frequency.penalty system property</li>
     *   <li>frequency-penalty property in camel-forage-model-open-ai.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * <p><strong>Valid Range:</strong> -2.0 to 2.0
     *
     * @return the frequency penalty value, or null if not configured
     */
    public Double frequencyPenalty() {
        return ConfigStore.getInstance()
                .get(FREQUENCY_PENALTY)
                .map(Double::parseDouble)
                .orElse(null);
    }

    /**
     * Returns the presence penalty parameter.
     *
     * <p>Presence penalty increases the likelihood of talking about new topics.
     * Positive values penalize tokens that have already appeared in the text.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OPENAI_PRESENCE_PENALTY environment variable</li>
     *   <li>openai.presence.penalty system property</li>
     *   <li>presence-penalty property in camel-forage-model-open-ai.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * <p><strong>Valid Range:</strong> -2.0 to 2.0
     *
     * @return the presence penalty value, or null if not configured
     */
    public Double presencePenalty() {
        return ConfigStore.getInstance()
                .get(PRESENCE_PENALTY)
                .map(Double::parseDouble)
                .orElse(null);
    }

    /**
     * Returns whether request logging is enabled.
     *
     * <p>When enabled, the OpenAI client will log all requests sent to the server.
     * This is useful for debugging and monitoring but should be disabled in production
     * to avoid logging sensitive information.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OPENAI_LOG_REQUESTS environment variable</li>
     *   <li>openai.log.requests system property</li>
     *   <li>log-requests property in camel-forage-model-open-ai.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * <p><strong>Valid Values:</strong> "true" or "false" (case-insensitive)
     *
     * @return true if request logging is enabled, false if disabled, null if not configured
     */
    public Boolean logRequests() {
        return ConfigStore.getInstance()
                .get(LOG_REQUESTS)
                .map(Boolean::parseBoolean)
                .orElse(null);
    }

    /**
     * Returns whether response logging is enabled.
     *
     * <p>When enabled, the OpenAI client will log all responses received from the server.
     * This is useful for debugging and monitoring but should be disabled in production
     * to avoid logging sensitive information.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OPENAI_LOG_RESPONSES environment variable</li>
     *   <li>openai.log.responses system property</li>
     *   <li>log-responses property in camel-forage-model-open-ai.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * <p><strong>Valid Values:</strong> "true" or "false" (case-insensitive)
     *
     * @return true if response logging is enabled, false if disabled, null if not configured
     */
    public Boolean logResponses() {
        return ConfigStore.getInstance()
                .get(LOG_RESPONSES)
                .map(Boolean::parseBoolean)
                .orElse(null);
    }
}
