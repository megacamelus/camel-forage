package io.kaoto.forage.models.chat.google;

import static io.kaoto.forage.models.chat.google.GoogleConfigEntries.API_KEY;
import static io.kaoto.forage.models.chat.google.GoogleConfigEntries.MODEL_NAME;

import java.util.Optional;
import io.kaoto.forage.core.util.config.Config;
import io.kaoto.forage.core.util.config.ConfigModule;
import io.kaoto.forage.core.util.config.ConfigStore;
import io.kaoto.forage.core.util.config.MissingConfigException;

/**
 * Configuration class for Google Gemini AI model integration in the Forage framework.
 *
 * <p>This configuration class manages the settings required to connect to and use Google's
 * Gemini AI models. It handles authentication credentials and model selection through
 * environment variables with appropriate fallback mechanisms.
 *
 * <p><strong>Required Configuration:</strong>
 * <ul>
 *   <li><strong>GOOGLE_API_KEY</strong> - Your Google AI API key for authentication</li>
 *   <li><strong>GOOGLE_MODEL_NAME</strong> - The specific Gemini model to use (e.g., "gemini-pro", "gemini-pro-vision")</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (GOOGLE_API_KEY, GOOGLE_MODEL_NAME)</li>
 *   <li>System properties (google.api.key, google.model.name)</li>
 *   <li>forage-model-google-gemini.properties file in classpath</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables
 * export GOOGLE_API_KEY="your-api-key-here"
 * export GOOGLE_MODEL_NAME="gemini-pro"
 *
 * // Create and use configuration
 * GoogleConfig config = new GoogleConfig();
 * String apiKey = config.apiKey();        // Returns the configured API key
 * String model = config.modelName();      // Returns the configured model name
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
public class GoogleConfig implements Config {

    private final String prefix;

    /**
     * Constructs a new GoogleConfig and registers configuration parameters with the ConfigStore.
     *
     * <p>During construction, this class:
     * <ul>
     *   <li>Registers the API key configuration to be sourced from GOOGLE_API_KEY environment variable</li>
     *   <li>Registers the model name configuration to be sourced from GOOGLE_MODEL_NAME environment variable</li>
     *   <li>Attempts to load additional properties from forage-model-google-gemini.properties</li>
     * </ul>
     *
     * <p>Configuration values are resolved when this constructor is called, but accessed lazily
     * through the getter methods. If required configuration is missing, exceptions will be thrown
     * when the getter methods are called, not during construction.
     */
    public GoogleConfig() {
        this(null);
    }

    public GoogleConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        GoogleConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(GoogleConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        GoogleConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = GoogleConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    /**
     * Returns the unique identifier for this Google Gemini configuration module.
     *
     * <p>This name corresponds to the module artifact and is used for:
     * <ul>
     *   <li>Loading configuration files (forage-model-google-gemini.properties)</li>
     *   <li>Identifying this module in logs and error messages</li>
     *   <li>Distinguishing this configuration from other AI model configurations</li>
     * </ul>
     *
     * @return the module name "forage-model-google-gemini"
     */
    @Override
    public String name() {
        return "forage-model-google-gemini";
    }

    /**
     * Returns the Google AI API key for authentication.
     *
     * <p>This method retrieves the API key that was configured through environment variables,
     * system properties, or configuration files. The API key is required for all interactions
     * with Google's Gemini AI services.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>GOOGLE_API_KEY environment variable</li>
     *   <li>google.api.key system property</li>
     *   <li>api-key property in forage-model-google-gemini.properties</li>
     * </ol>
     *
     * @return the Google AI API key
     * @throws MissingConfigException if no API key is configured
     */
    public String apiKey() {
        return ConfigStore.getInstance()
                .get(API_KEY.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Google API key"));
    }

    /**
     * Returns the name of the Google Gemini model to use.
     *
     * <p>This method retrieves the model name that specifies which Gemini model variant
     * should be used for AI operations. Different models have different capabilities,
     * performance characteristics, and pricing.
     *
     * <p><strong>Common Model Names:</strong>
     * <ul>
     *   <li><strong>gemini-pro</strong> - General-purpose model for text generation</li>
     *   <li><strong>gemini-pro-vision</strong> - Model with image understanding capabilities</li>
     *   <li><strong>gemini-1.5-pro</strong> - Latest version with enhanced capabilities</li>
     * </ul>
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>GOOGLE_MODEL_NAME environment variable</li>
     *   <li>google.model.name system property</li>
     *   <li>model-name property in forage-model-google-gemini.properties</li>
     * </ol>
     *
     * @return the Google Gemini model name
     * @throws MissingConfigException if no model name is configured
     */
    public String modelName() {
        return ConfigStore.getInstance()
                .get(MODEL_NAME.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing Google model name"));
    }
}
