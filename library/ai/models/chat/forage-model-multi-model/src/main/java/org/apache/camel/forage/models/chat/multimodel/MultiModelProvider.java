package org.apache.camel.forage.models.chat.multimodel;

import dev.langchain4j.model.chat.ChatModel;
import java.util.List;
import org.apache.camel.forage.core.ai.ModelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating multi-model chat instances that can dynamically switch between different AI models.
 *
 * <p>This provider creates instances of {@link MultiModelChatModel} that can delegate to different
 * underlying model providers based on configuration or runtime decisions. It supports registering
 * multiple model providers and selecting the appropriate one based on model type or other criteria.
 *
 * <p><strong>Configuration:</strong>
 * <ul>
 *   <li>Default Model: Configured via MULTIMODEL_DEFAULT_MODEL environment variable (required)</li>
 *   <li>Available Models: Configured via MULTIMODEL_AVAILABLE_MODELS environment variable (comma-separated)</li>
 *   <li>Model Mapping: Individual model configurations via environment variables</li>
 * </ul>
 *
 * <p><strong>Usage:</strong>
 * <pre>{@code
 * MultiModelProvider provider = new MultiModelProvider();
 * ChatModel model = provider.create();
 * }</pre>
 *
 * @see MultiModelConfig
 * @see ModelProvider
 * @since 1.0
 */
public abstract class MultiModelProvider implements ModelProvider {
    private static final Logger LOG = LoggerFactory.getLogger(MultiModelProvider.class);
    
    private ModelSelector modelSelector;

    /**
     * Creates a new multi-model chat instance with the configured parameters.
     *
     * <p>This method creates a {@link MultiModelChatModel} that can delegate to different
     * underlying model providers based on configuration. The model providers are discovered
     * and configured based on the multi-model configuration.
     *
     * @param id the configuration prefix/id for this multi-model instance
     * @return a new configured multi-model chat instance
     */
    @Override
    public ChatModel create(String id) {
        MultiModelConfig config = new MultiModelConfig(id);
        
        String defaultModel = config.defaultModel();
        List<String> availableModels = config.availableModels();
        
        LOG.trace("Creating MultiModel with default: {} and available models: {}", 
                  defaultModel, String.join(", ", availableModels));
        
        // Initialize model providers based on configuration
        for (String modelType : availableModels) {
            ModelProvider provider = createProviderForModel(modelType, id);
            if (provider != null) {
                modelSelector.add(provider.create(id));
            }
        }
        
        return new MultiModelChatModel(modelSelector);
    }
    
    /**
     * Creates a provider for a specific model type.
     * 
     * @param modelType the type of model to create a provider for
     * @param id the configuration prefix/id
     * @return a model provider for the specified type, or null if not supported
     */
    protected abstract ModelProvider createProviderForModel(String modelType, String id);
}