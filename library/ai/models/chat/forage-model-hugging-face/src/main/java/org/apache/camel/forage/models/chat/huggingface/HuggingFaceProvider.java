package org.apache.camel.forage.models.chat.huggingface;

import static java.time.Duration.ofSeconds;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.huggingface.HuggingFaceChatModel;
import org.apache.camel.forage.core.ai.ModelProvider;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating HuggingFace chat models
 */
@ForageBean(
        value = "hugging-face",
        components = {"camel-langchain4j-agent"},
        description = "HuggingFace Inference API chat model provider")
public class HuggingFaceProvider implements ModelProvider {
    private static final Logger LOG = LoggerFactory.getLogger(HuggingFaceProvider.class);

    @Override
    public ChatModel create(String id) {
        final HuggingFaceConfig config = new HuggingFaceConfig(id);
        LOG.trace("Creating HuggingFace chat model");

        HuggingFaceChatModel.Builder builder = HuggingFaceChatModel.builder().accessToken(config.apiKey());

        // Configure optional model ID
        if (config.modelId() != null) {
            builder.modelId(config.modelId());
        }

        // Configure model behavior parameters
        if (config.temperature() != null) {
            builder.temperature(config.temperature());
        }

        if (config.maxNewTokens() != null) {
            builder.maxNewTokens(config.maxNewTokens());
        }

        if (config.waitForModel() != null) {
            builder.waitForModel(config.waitForModel());
        }

        // Configure connection and reliability settings
        if (config.timeoutSeconds() != null) {
            builder.timeout(ofSeconds(config.timeoutSeconds()));
        }

        return builder.build();
    }
}
