package org.apache.camel.forage.models.chat.localai;

import static java.time.Duration.ofSeconds;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.apache.camel.forage.core.ai.ModelProvider;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating LocalAI chat models
 */
@ForageBean(
        value = "local-ai",
        components = {"camel-langchain4j-agent"},
        feature = "Chat Model",
        description = "Self-hosted models via LocalAI (OpenAI-compatible)")
public class LocalAiProvider implements ModelProvider {
    private static final Logger LOG = LoggerFactory.getLogger(LocalAiProvider.class);

    @Override
    public ChatModel create(String id) {
        final LocalAiConfig config = new LocalAiConfig(id);
        LOG.trace("Creating LocalAI chat model");

        OpenAiChatModel.OpenAiChatModelBuilder builder =
                OpenAiChatModel.builder().baseUrl(config.baseUrl());

        // Configure optional API key (many LocalAI setups don't require one)
        if (config.apiKey() != null) {
            builder.apiKey(config.apiKey());
        } else {
            // LocalAI often doesn't require an API key, but LangChain4j requires one
            // Use a placeholder value for compatibility
            builder.apiKey("not-needed");
        }

        // Configure optional model name
        if (config.modelName() != null) {
            builder.modelName(config.modelName());
        }

        // Configure model behavior parameters
        if (config.temperature() != null) {
            builder.temperature(config.temperature());
        }

        if (config.maxTokens() != null) {
            builder.maxTokens(config.maxTokens());
        }

        if (config.topP() != null) {
            builder.topP(config.topP());
        }

        if (config.presencePenalty() != null) {
            builder.presencePenalty(config.presencePenalty());
        }

        if (config.frequencyPenalty() != null) {
            builder.frequencyPenalty(config.frequencyPenalty());
        }

        if (config.seed() != null) {
            builder.seed(config.seed().intValue());
        }

        if (config.user() != null) {
            builder.user(config.user());
        }

        // Configure connection and reliability settings
        if (config.timeoutSeconds() != null) {
            builder.timeout(ofSeconds(config.timeoutSeconds()));
        }

        if (config.maxRetries() != null) {
            builder.maxRetries(config.maxRetries());
        }

        // Configure logging settings
        if (config.logRequestsAndResponses() != null) {
            builder.logRequests(config.logRequestsAndResponses());
            builder.logResponses(config.logRequestsAndResponses());
        }

        return builder.build();
    }
}
