package io.kaoto.forage.models.chat.azureopenai;

import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.API_KEY;
import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.DEPLOYMENT_NAME;
import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.ENDPOINT;
import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.FREQUENCY_PENALTY;
import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.LOG_REQUESTS_AND_RESPONSES;
import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.MAX_RETRIES;
import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.MAX_TOKENS;
import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.PRESENCE_PENALTY;
import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.SEED;
import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.SERVICE_VERSION;
import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.TEMPERATURE;
import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.TIMEOUT;
import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.TOP_P;
import static io.kaoto.forage.models.chat.azureopenai.AzureOpenAiConfigEntries.USER;

import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import io.kaoto.forage.core.util.config.MissingConfigException;
import java.util.Optional;

/**
 * Configuration class for Azure OpenAI integration in the Forage framework.
 *
 * <p>This configuration class manages the settings required to connect to and use Azure OpenAI
 * services. It handles authentication credentials, endpoint configuration, and deployment
 * selection through environment variables with appropriate fallback mechanisms.
 *
 * <p><strong>Required Configuration:</strong>
 * <ul>
 *   <li><strong>AZURE_OPENAI_API_KEY</strong> - Your Azure OpenAI API key for authentication</li>
 *   <li><strong>AZURE_OPENAI_ENDPOINT</strong> - The Azure OpenAI resource endpoint URL</li>
 *   <li><strong>AZURE_OPENAI_DEPLOYMENT_NAME</strong> - The deployment name of your Azure OpenAI model</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (AZURE_OPENAI_API_KEY, AZURE_OPENAI_ENDPOINT, AZURE_OPENAI_DEPLOYMENT_NAME)</li>
 *   <li>System properties (azure.openai.api.key, azure.openai.endpoint, azure.openai.deployment.name)</li>
 *   <li>forage-model-azure-openai.properties file in classpath</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables
 * export AZURE_OPENAI_API_KEY="your-api-key-here"
 * export AZURE_OPENAI_ENDPOINT="https://your-resource.openai.azure.com/"
 * export AZURE_OPENAI_DEPLOYMENT_NAME="gpt-35-turbo"
 *
 * // Create and use configuration
 * AzureOpenAiConfig config = new AzureOpenAiConfig();
 * String apiKey = config.apiKey();              // Returns the configured API key
 * String endpoint = config.endpoint();          // Returns the configured endpoint
 * String deployment = config.deploymentName();  // Returns the configured deployment name
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
public class AzureOpenAiConfig implements Config {

    private final String prefix;

    /**
     * Constructs a new AzureOpenAiConfig and registers configuration parameters with the ConfigStore.
     *
     * <p>During construction, this class:
     * <ul>
     *   <li>Registers the API key configuration to be sourced from AZURE_OPENAI_API_KEY environment variable</li>
     *   <li>Registers the endpoint configuration to be sourced from AZURE_OPENAI_ENDPOINT environment variable</li>
     *   <li>Registers the deployment name configuration to be sourced from AZURE_OPENAI_DEPLOYMENT_NAME environment variable</li>
     *   <li>Attempts to load additional properties from forage-model-azure-openai.properties</li>
     * </ul>
     *
     * <p>Configuration values are resolved when this constructor is called, but accessed lazily
     * through the getter methods. If required configuration is missing, exceptions will be thrown
     * when the getter methods are called, not during construction.
     */
    public AzureOpenAiConfig() {
        this(null);
    }

    public AzureOpenAiConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        AzureOpenAiConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(AzureOpenAiConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        AzureOpenAiConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = AzureOpenAiConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    /**
     * Returns the unique identifier for this Azure OpenAI configuration module.
     *
     * <p>This name corresponds to the module artifact and is used for:
     * <ul>
     *   <li>Loading configuration files (forage-model-azure-openai.properties)</li>
     *   <li>Identifying this module in logs and error messages</li>
     *   <li>Distinguishing this configuration from other AI model configurations</li>
     * </ul>
     *
     * @return the module name "forage-model-azure-openai"
     */
    @Override
    public String name() {
        return "forage-model-azure-openai";
    }

    /**
     * Returns the Azure OpenAI API key for authentication.
     *
     * <p>This method retrieves the API key that was configured through environment variables,
     * system properties, or configuration files. The API key is required for all interactions
     * with Azure OpenAI services.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>AZURE_OPENAI_API_KEY environment variable</li>
     *   <li>azure.openai.api.key system property</li>
     *   <li>api-key property in forage-model-azure-openai.properties</li>
     * </ol>
     *
     * @return the Azure OpenAI API key
     * @throws MissingConfigException if no API key is configured
     */
    public String apiKey() {
        return ConfigStore.getInstance()
                .get(API_KEY.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Azure OpenAI API key"));
    }

    /**
     * Returns the Azure OpenAI endpoint URL.
     *
     * <p>This method retrieves the endpoint URL for your Azure OpenAI resource.
     * The endpoint URL is specific to your Azure OpenAI resource and region.
     *
     * <p><strong>Example Endpoint Format:</strong>
     * {@code https://your-resource-name.openai.azure.com/}
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>AZURE_OPENAI_ENDPOINT environment variable</li>
     *   <li>azure.openai.endpoint system property</li>
     *   <li>endpoint property in forage-model-azure-openai.properties</li>
     * </ol>
     *
     * @return the Azure OpenAI endpoint URL
     * @throws MissingConfigException if no endpoint is configured
     */
    public String endpoint() {
        return ConfigStore.getInstance()
                .get(ENDPOINT.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Azure OpenAI endpoint"));
    }

    /**
     * Returns the name of the Azure OpenAI deployment to use.
     *
     * <p>This method retrieves the deployment name that specifies which model deployment
     * should be used for AI operations. The deployment name corresponds to a specific
     * model deployment you have created in your Azure OpenAI resource.
     *
     * <p><strong>Common Deployment Examples:</strong>
     * <ul>
     *   <li><strong>gpt-35-turbo</strong> - GPT-3.5 Turbo deployment</li>
     *   <li><strong>gpt-4</strong> - GPT-4 deployment</li>
     *   <li><strong>text-embedding-ada-002</strong> - Text embedding model deployment</li>
     * </ul>
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>AZURE_OPENAI_DEPLOYMENT_NAME environment variable</li>
     *   <li>azure.openai.deployment.name system property</li>
     *   <li>deployment-name property in forage-model-azure-openai.properties</li>
     * </ol>
     *
     * @return the Azure OpenAI deployment name
     * @throws MissingConfigException if no deployment name is configured
     */
    public String deploymentName() {
        return ConfigStore.getInstance()
                .get(DEPLOYMENT_NAME.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Azure OpenAI deployment name"));
    }

    /**
     * Returns the Azure OpenAI service version to use.
     *
     * <p>This method retrieves the API version that specifies which version of the Azure OpenAI
     * service API to use. Different versions may have different features and capabilities.
     *
     * <p><strong>Common Service Versions:</strong>
     * <ul>
     *   <li><strong>2024-02-01</strong> - Latest stable version</li>
     *   <li><strong>2023-12-01-preview</strong> - Preview version with latest features</li>
     *   <li><strong>2023-05-15</strong> - Stable version</li>
     * </ul>
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>AZURE_OPENAI_SERVICE_VERSION environment variable</li>
     *   <li>azure.openai.service.version system property</li>
     *   <li>service-version property in forage-model-azure-openai.properties</li>
     * </ol>
     *
     * @return the Azure OpenAI service version, or null if not configured (uses service default)
     */
    public String serviceVersion() {
        return ConfigStore.getInstance().get(SERVICE_VERSION.asNamed(prefix)).orElse(null);
    }

    /**
     * Returns the temperature setting for response generation.
     *
     * <p>Temperature controls the randomness of the model's output. Lower values make responses
     * more focused and deterministic, while higher values make them more creative and varied.
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
     * <p>This setting limits the length of the model's response. The total token count includes
     * both the input prompt and the generated response.
     *
     * <p><strong>Token Guidelines:</strong>
     * <ul>
     *   <li>1 token ≈ 0.75 words for English text</li>
     *   <li>Different models have different maximum context windows</li>
     *   <li>Setting too low may truncate responses</li>
     *   <li>Setting too high may increase costs and latency</li>
     * </ul>
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
     * <p>An alternative to temperature for controlling response diversity. The model considers
     * only the most probable tokens whose cumulative probability exceeds the top-p value.
     *
     * <p><strong>Value Range:</strong> 0.0 to 1.0
     * <ul>
     *   <li><strong>0.1</strong> - Very focused, considers only top 10% probability mass</li>
     *   <li><strong>0.5</strong> - Moderately focused</li>
     *   <li><strong>0.9</strong> - More diverse responses</li>
     *   <li><strong>1.0</strong> - No filtering (considers all tokens)</li>
     * </ul>
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
     * Returns the presence penalty for discouraging new topic introduction.
     *
     * <p>Penalizes tokens based on whether they appear in the text so far, encouraging
     * the model to avoid introducing entirely new topics.
     *
     * <p><strong>Value Range:</strong> -2.0 to 2.0
     * <ul>
     *   <li><strong>Negative values</strong> - Encourage new topics</li>
     *   <li><strong>0.0</strong> - No penalty (default)</li>
     *   <li><strong>Positive values</strong> - Discourage new topics</li>
     * </ul>
     *
     * @return the presence penalty value, or null if not configured (uses service default)
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
     * <p>Penalizes tokens based on their frequency in the text so far, discouraging
     * the model from repeating the same phrases or words.
     *
     * <p><strong>Value Range:</strong> -2.0 to 2.0
     * <ul>
     *   <li><strong>Negative values</strong> - Encourage repetition</li>
     *   <li><strong>0.0</strong> - No penalty (default)</li>
     *   <li><strong>Positive values</strong> - Discourage repetition</li>
     * </ul>
     *
     * @return the frequency penalty value, or null if not configured (uses service default)
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
     * <p>When provided, the model will attempt to generate deterministic responses
     * for the same input and seed combination. Useful for reproducible results
     * in testing and development scenarios.
     *
     * <p><strong>Usage Notes:</strong>
     * <ul>
     *   <li>Same seed + same input = similar output (not guaranteed to be identical)</li>
     *   <li>Different seeds with same input = different outputs</li>
     *   <li>Useful for A/B testing and debugging</li>
     * </ul>
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
     * Returns the user identifier for tracking and monitoring.
     *
     * <p>This identifier can be used to track API usage per user, implement
     * rate limiting, and provide better monitoring and analytics.
     *
     * <p><strong>Usage Examples:</strong>
     * <ul>
     *   <li>User ID from your application</li>
     *   <li>Session identifier</li>
     *   <li>Department or team identifier</li>
     * </ul>
     *
     * @return the user identifier, or null if not configured
     */
    public String user() {
        return ConfigStore.getInstance().get(USER.asNamed(prefix)).orElse(null);
    }

    /**
     * Returns the request timeout duration in seconds.
     *
     * <p>Specifies how long to wait for a response from the Azure OpenAI service
     * before timing out the request.
     *
     * <p><strong>Recommended Values:</strong>
     * <ul>
     *   <li><strong>30</strong> - For quick responses</li>
     *   <li><strong>60</strong> - Default recommendation</li>
     *   <li><strong>120</strong> - For complex requests or slower models</li>
     * </ul>
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
     * <p>When a request fails due to transient issues (network problems, service
     * temporary unavailability), this setting controls how many times to retry
     * before giving up.
     *
     * <p><strong>Recommended Values:</strong>
     * <ul>
     *   <li><strong>0</strong> - No retries (fail fast)</li>
     *   <li><strong>3</strong> - Default recommendation</li>
     *   <li><strong>5</strong> - For mission-critical applications</li>
     * </ul>
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
     * <p>When enabled, logs the details of both requests sent to and responses received
     * from Azure OpenAI service. Useful for debugging and monitoring, but may expose
     * sensitive information in logs.
     *
     * <p><strong>Security Note:</strong> Be cautious when enabling in production
     * as this may log sensitive data including API keys, user content, and generated responses.
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
