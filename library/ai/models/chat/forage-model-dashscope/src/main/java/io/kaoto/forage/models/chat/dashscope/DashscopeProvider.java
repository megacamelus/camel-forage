package io.kaoto.forage.models.chat.dashscope;

import dev.langchain4j.model.chat.ChatModel;
import io.kaoto.forage.core.ai.ModelProvider;
import io.kaoto.forage.core.annotations.ForageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating Dashscope Qwen chat models.
 *
 * <p><strong>Note:</strong> This is a placeholder implementation. The actual Dashscope integration
 * will be available when LangChain4J adds support for Alibaba Dashscope in future versions.
 *
 * <p>This provider currently throws an UnsupportedOperationException to indicate that
 * the integration is not yet available. Once LangChain4J adds the {@code langchain4j-dashscope}
 * or {@code langchain4j-alibaba-dashscope} dependency, this implementation should be updated
 * to use the proper Dashscope integration.
 *
 * <p><strong>Configuration:</strong>
 * The configuration system is already in place and will work once the implementation is completed.
 * Required configuration includes:
 * <ul>
 *   <li>DASHSCOPE_API_KEY - Your Dashscope API key</li>
 *   <li>DASHSCOPE_MODEL_NAME - The Qwen model to use (optional, defaults to "qwen-turbo")</li>
 * </ul>
 */
@ForageBean(
        value = "dashscope",
        components = {"camel-langchain4j-agent"},
        feature = "Chat Model",
        description = "Alibaba Cloud Qwen models via DashScope (placeholder)")
public class DashscopeProvider implements ModelProvider {
    private static final Logger LOG = LoggerFactory.getLogger(DashscopeProvider.class);

    @Override
    public ChatModel create(String id) {
        final DashscopeConfig config = new DashscopeConfig(id);
        LOG.warn("Dashscope integration is not yet available. This is a placeholder implementation.");
        LOG.warn(
                "Configuration loaded: API key configured={}, Model name={}",
                config.apiKey() != null,
                config.modelName());

        throw new UnsupportedOperationException("Dashscope integration is not yet available in LangChain4J. "
                + "This provider will be implemented once langchain4j-dashscope dependency becomes available. "
                + "Configuration is ready: API key is "
                + (config.apiKey() != null ? "configured" : "missing") + ", model name is '"
                + config.modelName() + "'");
    }
}
