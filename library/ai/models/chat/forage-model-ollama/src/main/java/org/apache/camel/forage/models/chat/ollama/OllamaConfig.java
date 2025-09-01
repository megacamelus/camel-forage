package org.apache.camel.forage.models.chat.ollama;

import org.apache.camel.forage.core.util.config.Config;
import org.apache.camel.forage.core.util.config.ConfigEntry;
import org.apache.camel.forage.core.util.config.ConfigModule;
import org.apache.camel.forage.core.util.config.ConfigStore;

/**
 * Configuration class for Ollama AI model integration in the Camel Forage framework.
 *
 * <p>This configuration class manages the settings required to connect to and use Ollama
 * AI models. It handles server connection details and model selection through environment
 * variables with appropriate fallback mechanisms and default values.
 *
 * <p><strong>Configuration Parameters:</strong>
 * <ul>
 *   <li><strong>OLLAMA_BASE_URL</strong> - The base URL of the Ollama server (default: "http://localhost:11434")</li>
 *   <li><strong>OLLAMA_MODEL_NAME</strong> - The specific Ollama model to use (default: "llama3")</li>
 *   <li><strong>OLLAMA_TEMPERATURE</strong> - Temperature for response randomness, 0.0-2.0 (no default)</li>
 *   <li><strong>OLLAMA_TOP_K</strong> - Top-K sampling parameter, positive integer (no default)</li>
 *   <li><strong>OLLAMA_TOP_P</strong> - Top-P (nucleus) sampling parameter, 0.0-1.0 (no default)</li>
 *   <li><strong>OLLAMA_MIN_P</strong> - Minimum probability threshold, 0.0-1.0 (no default)</li>
 *   <li><strong>OLLAMA_NUM_CTX</strong> - Context window size, positive integer (no default)</li>
 *   <li><strong>OLLAMA_LOG_REQUESTS</strong> - Enable request logging, true/false (no default)</li>
 *   <li><strong>OLLAMA_LOG_RESPONSES</strong> - Enable response logging, true/false (no default)</li>
 * </ul>
 *
 * <p><strong>Configuration Sources:</strong>
 * Configuration values are resolved in the following order of precedence:
 * <ol>
 *   <li>Environment variables (OLLAMA_BASE_URL, OLLAMA_MODEL_NAME, OLLAMA_TEMPERATURE, etc.)</li>
 *   <li>System properties (ollama.base.url, ollama.model.name, ollama.temperature, etc.)</li>
 *   <li>forage-model-ollama.properties file in classpath</li>
 *   <li>Default values if none of the above are provided (only for base URL and model name)</li>
 * </ol>
 *
 * <p><strong>Usage Example:</strong>
 * <pre>{@code
 * // Set environment variables (optional)
 * export OLLAMA_BASE_URL="http://my-ollama-server:11434"
 * export OLLAMA_MODEL_NAME="llama3.1"
 * export OLLAMA_TEMPERATURE="0.7"
 * export OLLAMA_TOP_K="40"
 * export OLLAMA_TOP_P="0.9"
 * export OLLAMA_NUM_CTX="2048"
 * export OLLAMA_LOG_REQUESTS="false"
 * export OLLAMA_LOG_RESPONSES="false"
 *
 * // Create and use configuration
 * OllamaConfig config = new OllamaConfig();
 * String baseUrl = config.baseUrl();      // Returns the configured base URL
 * String model = config.modelName();      // Returns the configured model name
 * Double temperature = config.temperature(); // Returns 0.7 or null if not set
 * }</pre>
 *
 * <p><strong>Default Values:</strong>
 * <ul>
 *   <li>Base URL: "http://localhost:11434" (standard Ollama local installation)</li>
 *   <li>Model Name: "llama3" (popular general-purpose model)</li>
 *   <li>All other parameters: No defaults (return null if not configured)</li>
 * </ul>
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
public class OllamaConfig implements Config {

    private static final ConfigModule BASE_URL = ConfigModule.of(OllamaConfig.class, "ollama.base.url");
    private static final ConfigModule MODEL_NAME = ConfigModule.of(OllamaConfig.class, "ollama.model.name");
    private static final ConfigModule TEMPERATURE = ConfigModule.of(OllamaConfig.class, "ollama.temperature");
    private static final ConfigModule TOP_K = ConfigModule.of(OllamaConfig.class, "ollama.top.k");
    private static final ConfigModule TOP_P = ConfigModule.of(OllamaConfig.class, "ollama.top.p");
    private static final ConfigModule MIN_P = ConfigModule.of(OllamaConfig.class, "ollama.min.p");
    private static final ConfigModule NUM_CTX = ConfigModule.of(OllamaConfig.class, "ollama.num.ctx");
    private static final ConfigModule LOG_REQUESTS = ConfigModule.of(OllamaConfig.class, "ollama.log.requests");
    private static final ConfigModule LOG_RESPONSES = ConfigModule.of(OllamaConfig.class, "ollama.log.responses");

    private static final String DEFAULT_BASE_URL = "http://localhost:11434";
    private static final String DEFAULT_MODEL_NAME = "llama3";

    /**
     * Constructs a new OllamaConfig and registers configuration parameters with the ConfigStore.
     *
     * <p>During construction, this class:
     * <ul>
     *   <li>Registers the base URL configuration to be sourced from OLLAMA_BASE_URL environment variable</li>
     *   <li>Registers the model name configuration to be sourced from OLLAMA_MODEL_NAME environment variable</li>
     *   <li>Registers advanced parameters (temperature, topK, topP, minP, numCtx) from their respective environment variables</li>
     *   <li>Registers logging parameters (logRequests, logResponses) from their respective environment variables</li>
     *   <li>Attempts to load additional properties from forage-model-ollama.properties</li>
     * </ul>
     *
     * <p>Configuration values are resolved when this constructor is called, with default values
     * used when no configuration is provided through environment variables, system properties,
     * or configuration files.
     */
    public OllamaConfig() {
        ConfigStore.getInstance().add(BASE_URL, ConfigEntry.fromModule(BASE_URL, "OLLAMA_BASE_URL"));
        ConfigStore.getInstance().add(MODEL_NAME, ConfigEntry.fromModule(MODEL_NAME, "OLLAMA_MODEL_NAME"));
        ConfigStore.getInstance().add(TEMPERATURE, ConfigEntry.fromModule(TEMPERATURE, "OLLAMA_TEMPERATURE"));
        ConfigStore.getInstance().add(TOP_K, ConfigEntry.fromModule(TOP_K, "OLLAMA_TOP_K"));
        ConfigStore.getInstance().add(TOP_P, ConfigEntry.fromModule(TOP_P, "OLLAMA_TOP_P"));
        ConfigStore.getInstance().add(MIN_P, ConfigEntry.fromModule(MIN_P, "OLLAMA_MIN_P"));
        ConfigStore.getInstance().add(NUM_CTX, ConfigEntry.fromModule(NUM_CTX, "OLLAMA_NUM_CTX"));
        ConfigStore.getInstance().add(LOG_REQUESTS, ConfigEntry.fromModule(LOG_REQUESTS, "OLLAMA_LOG_REQUESTS"));
        ConfigStore.getInstance().add(LOG_RESPONSES, ConfigEntry.fromModule(LOG_RESPONSES, "OLLAMA_LOG_RESPONSES"));
        ConfigStore.getInstance().add(OllamaConfig.class, this, this::register);
    }

    private ConfigModule resolve(String name) {
        if (BASE_URL.name().equals(name)) {
            return BASE_URL;
        }

        if (MODEL_NAME.name().equals(name)) {
            return MODEL_NAME;
        }

        if (TEMPERATURE.name().equals(name)) {
            return TEMPERATURE;
        }

        if (TOP_K.name().equals(name)) {
            return TOP_K;
        }

        if (TOP_P.name().equals(name)) {
            return TOP_P;
        }

        if (MIN_P.name().equals(name)) {
            return MIN_P;
        }

        if (NUM_CTX.name().equals(name)) {
            return NUM_CTX;
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
     * Returns the unique identifier for this Ollama configuration module.
     *
     * <p>This name corresponds to the module artifact and is used for:
     * <ul>
     *   <li>Loading configuration files (forage-model-ollama.properties)</li>
     *   <li>Identifying this module in logs and error messages</li>
     *   <li>Distinguishing this configuration from other AI model configurations</li>
     * </ul>
     *
     * @return the module name "forage-model-ollama"
     */
    @Override
    public String name() {
        return "forage-model-ollama";
    }

    /**
     * Returns the base URL of the Ollama server.
     *
     * <p>This method retrieves the base URL that was configured through environment variables,
     * system properties, or configuration files. The base URL specifies where the Ollama
     * server is running and should include the protocol and port.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OLLAMA_BASE_URL environment variable</li>
     *   <li>ollama.base.url system property</li>
     *   <li>base-url property in forage-model-ollama.properties</li>
     *   <li>Default value: "http://localhost:11434"</li>
     * </ol>
     *
     * <p><strong>Example Values:</strong>
     * <ul>
     *   <li>"http://localhost:11434" - Local Ollama installation</li>
     *   <li>"http://ollama-server:11434" - Remote server</li>
     *   <li>"https://my-ollama.example.com" - HTTPS endpoint</li>
     * </ul>
     *
     * @return the Ollama server base URL, never null
     */
    public String baseUrl() {
        return ConfigStore.getInstance().get(BASE_URL).orElse(DEFAULT_BASE_URL);
    }

    /**
     * Returns the name of the Ollama model to use.
     *
     * <p>This method retrieves the model name that specifies which Ollama model
     * should be used for AI operations. The model must be available on the
     * configured Ollama server.
     *
     * <p><strong>Common Model Names:</strong>
     * <ul>
     *   <li><strong>llama3</strong> - Meta's Llama 3 model (8B parameters)</li>
     *   <li><strong>llama3.1</strong> - Updated Llama 3.1 model</li>
     *   <li><strong>mistral</strong> - Mistral 7B model</li>
     *   <li><strong>codellama</strong> - Code-specialized Llama model</li>
     *   <li><strong>phi3</strong> - Microsoft's Phi-3 model</li>
     * </ul>
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OLLAMA_MODEL_NAME environment variable</li>
     *   <li>ollama.model.name system property</li>
     *   <li>model-name property in forage-model-ollama.properties</li>
     *   <li>Default value: "llama3"</li>
     * </ol>
     *
     * @return the Ollama model name, never null
     */
    public String modelName() {
        return ConfigStore.getInstance().get(MODEL_NAME).orElse(DEFAULT_MODEL_NAME);
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
     *   <li>OLLAMA_TEMPERATURE environment variable</li>
     *   <li>ollama.temperature system property</li>
     *   <li>temperature property in forage-model-ollama.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * <p><strong>Valid Range:</strong> 0.0 to 2.0 (typical range is 0.1 to 1.0)
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
     * Returns the top-K sampling parameter.
     *
     * <p>Top-K limits the model to consider only the K most likely next tokens.
     * This helps control the diversity of responses by filtering out unlikely options.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OLLAMA_TOP_K environment variable</li>
     *   <li>ollama.top.k system property</li>
     *   <li>top-k property in forage-model-ollama.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * <p><strong>Valid Range:</strong> Positive integers (typical range is 1 to 100)
     *
     * @return the top-K value, or null if not configured
     */
    public Integer topK() {
        return ConfigStore.getInstance().get(TOP_K).map(Integer::parseInt).orElse(null);
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
     *   <li>OLLAMA_TOP_P environment variable</li>
     *   <li>ollama.top.p system property</li>
     *   <li>top-p property in forage-model-ollama.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * <p><strong>Valid Range:</strong> 0.0 to 1.0 (typical range is 0.1 to 0.95)
     *
     * @return the top-P value, or null if not configured
     */
    public Double topP() {
        return ConfigStore.getInstance().get(TOP_P).map(Double::parseDouble).orElse(null);
    }

    /**
     * Returns the minimum probability threshold (min-P) parameter.
     *
     * <p>Min-P sets a minimum probability threshold below which tokens are filtered out.
     * This helps prevent the model from selecting very unlikely tokens while maintaining
     * response quality.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OLLAMA_MIN_P environment variable</li>
     *   <li>ollama.min.p system property</li>
     *   <li>min-p property in forage-model-ollama.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * <p><strong>Valid Range:</strong> 0.0 to 1.0 (typical range is 0.0 to 0.1)
     *
     * @return the min-P value, or null if not configured
     */
    public Double minP() {
        return ConfigStore.getInstance().get(MIN_P).map(Double::parseDouble).orElse(null);
    }

    /**
     * Returns the context window size (num_ctx) parameter.
     *
     * <p>This parameter controls the size of the context window that the model uses
     * to generate responses. A larger context window allows the model to consider
     * more previous conversation history but uses more memory and computation.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OLLAMA_NUM_CTX environment variable</li>
     *   <li>ollama.num.ctx system property</li>
     *   <li>num-ctx property in forage-model-ollama.properties</li>
     *   <li>No default value (returns null if not configured)</li>
     * </ol>
     *
     * <p><strong>Valid Range:</strong> Positive integers (model-dependent, typically 512 to 8192)
     *
     * @return the context window size, or null if not configured
     */
    public Integer numCtx() {
        return ConfigStore.getInstance().get(NUM_CTX).map(Integer::parseInt).orElse(null);
    }

    /**
     * Returns whether request logging is enabled.
     *
     * <p>When enabled, the Ollama client will log all requests sent to the server.
     * This is useful for debugging and monitoring but should be disabled in production
     * to avoid logging sensitive information.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OLLAMA_LOG_REQUESTS environment variable</li>
     *   <li>ollama.log.requests system property</li>
     *   <li>log-requests property in forage-model-ollama.properties</li>
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
     * <p>When enabled, the Ollama client will log all responses received from the server.
     * This is useful for debugging and monitoring but should be disabled in production
     * to avoid logging sensitive information.
     *
     * <p><strong>Configuration Sources (in order of precedence):</strong>
     * <ol>
     *   <li>OLLAMA_LOG_RESPONSES environment variable</li>
     *   <li>ollama.log.responses system property</li>
     *   <li>log-responses property in forage-model-ollama.properties</li>
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
