package org.apache.camel.forage.models.chat.multimodel;

import static org.apache.camel.forage.models.chat.multimodel.MultiModelConfigEntries.AVAILABLE_MODELS;
import static org.apache.camel.forage.models.chat.multimodel.MultiModelConfigEntries.DEFAULT_MODEL;

import java.util.List;
import java.util.Optional;
import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigHelper;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.core.util.config.MissingConfigException;

/**
 * Configuration class for multi-model integration in the Camel Forage framework.
 *
 * <p>This configuration class manages the settings required to use multiple AI models
 * simultaneously. It handles model selection, available model configuration, and routing
 * between different model providers.
 *
 * <p><strong>Required Configuration:</strong>
 * <ul>
 *   <li><strong>MULTIMODEL_DEFAULT_MODEL</strong> - The default model type to use when no specific model is requested</li>
 * </ul>
 *
 * <p><strong>Optional Configuration:</strong>
 * <ul>
 *   <li><strong>MULTIMODEL_AVAILABLE_MODELS</strong> - Comma-separated list of available model types (default: "openai,ollama,gemini")</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (MULTIMODEL_DEFAULT_MODEL, MULTIMODEL_AVAILABLE_MODELS, etc.)</li>
 *   <li>System properties (multimodel.default.model, multimodel.available.models, etc.)</li>
 *   <li>forage-model-multi-model.properties file in classpath</li>
 *   <li>Default values if none of the above are provided (only for available models)</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables
 * export MULTIMODEL_DEFAULT_MODEL="openai"
 * export MULTIMODEL_AVAILABLE_MODELS="openai,ollama,gemini"
 *
 * // Create and use configuration
 * MultiModelConfig config = new MultiModelConfig();
 * String defaultModel = config.defaultModel();      // Returns "openai"
 * String[] availableModels = config.availableModels(); // Returns ["openai", "ollama", "gemini"]
 * }</pre>
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
public class MultiModelConfig implements Config {

    private static final String DEFAULT_AVAILABLE_MODELS = "openai,ollama,gemini";
    private final String prefix;

    /**
     * Constructs a new MultiModelConfig and registers configuration parameters with the ConfigStore.
     */
    public MultiModelConfig() {
        this(null);
    }

    public MultiModelConfig(String prefix) {
        this.prefix = prefix;

        // First register new configuration modules. This happens only if a prefix is provided
        MultiModelConfigEntries.register(prefix);

        // Then, loads the configurations from the properties file associated with this Config module
        ConfigStore.getInstance().load(MultiModelConfig.class, this, this::register);

        // Lastly, load the overrides defined in system properties and environment variables
        MultiModelConfigEntries.loadOverrides(prefix);
    }

    @Override
    public void register(String name, String value) {
        Optional<ConfigModule> config = MultiModelConfigEntries.find(prefix, name);

        config.ifPresent(module -> ConfigStore.getInstance().set(module, value));
    }

    /**
     * Returns the unique identifier for this multi-model configuration module.
     *
     * @return the module name "forage-model-multi-model"
     */
    @Override
    public String name() {
        return "forage-model-multi-model";
    }

    /**
     * Returns the default model type to use when no specific model is requested.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>MULTIMODEL_DEFAULT_MODEL environment variable</li>
     *   <li>multimodel.default.model system property</li>
     *   <li>default-model property in forage-model-multi-model.properties</li>
     * </ol>
     *
     * @return the default model type
     * @throws MissingConfigException if no default model is configured
     */
    public String defaultModel() {
        return ConfigStore.getInstance()
                .get(DEFAULT_MODEL.asNamed(prefix))
                .orElseThrow(() -> new MissingConfigException("Missing default model configuration"));
    }

    /**
     * Returns the list of available model types.
     *
     * <p>This method retrieves the comma-separated list of available model types
     * that can be used by the multimodel provider.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>MULTIMODEL_AVAILABLE_MODELS environment variable</li>
     *   <li>multimodel.available.models system property</li>
     *   <li>available-models property in forage-model-multi-model.properties</li>
     *   <li>Default value: "openai,ollama,gemini"</li>
     * </ol>
     *
     * @return list of available model types, never null or empty
     */
    public List<String> availableModels() {
        return ConfigHelper.readAsList(AVAILABLE_MODELS.asNamed(prefix));
    }
}