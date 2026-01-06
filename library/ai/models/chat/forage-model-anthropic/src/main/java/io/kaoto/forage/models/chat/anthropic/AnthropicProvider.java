package io.kaoto.forage.models.chat.anthropic;

import dev.langchain4j.model.chat.ChatModel;
import io.kaoto.forage.core.ai.ModelProvider;
import io.kaoto.forage.core.annotations.ForageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating Anthropic Claude chat models.
 *
 * <p><strong>Note:</strong> This is a placeholder implementation. The actual Anthropic integration
 * will be available when LangChain4J adds comprehensive support for Anthropic Claude in future versions.
 *
 * <p>This provider currently throws an UnsupportedOperationException to indicate that
 * the integration is not yet fully available. Once LangChain4J adds the complete {@code langchain4j-anthropic}
 * integration with a proper builder pattern, this implementation should be updated
 * to use the proper Anthropic integration.
 *
 * <p><strong>Configuration:</strong>
 * The configuration system is already in place and will work once the implementation is completed.
 * Required configuration includes:
 * <ul>
 *   <li>ANTHROPIC_API_KEY - Your Anthropic API key</li>
 *   <li>ANTHROPIC_MODEL_NAME - The Claude model to use (optional, defaults to "claude-3-haiku-20240307")</li>
 * </ul>
 */
@ForageBean(
        value = "anthropic",
        components = {"camel-langchain4j-agent"},
        feature = "Chat Model",
        description = "Anthropic Claude models")
public class AnthropicProvider implements ModelProvider {
    private static final Logger LOG = LoggerFactory.getLogger(AnthropicProvider.class);

    @Override
    public ChatModel create(String id) {
        final AnthropicConfig config = new AnthropicConfig(id);
        LOG.warn("Anthropic integration is not yet fully available. This is a placeholder implementation.");
        LOG.warn(
                "Configuration loaded: API key configured={}, Model name={}",
                config.apiKey() != null,
                config.modelName());

        throw new UnsupportedOperationException("Anthropic integration is not yet fully available in LangChain4J. "
                + "This provider will be implemented once langchain4j-anthropic dependency becomes fully available. "
                + "Configuration is ready: API key is "
                + (config.apiKey() != null ? "configured" : "missing") + ", model name is '"
                + config.modelName() + "'");
    }
}
