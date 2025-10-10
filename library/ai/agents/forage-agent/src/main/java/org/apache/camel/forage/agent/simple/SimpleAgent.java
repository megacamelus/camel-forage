package org.apache.camel.forage.agent.simple;

import dev.langchain4j.data.message.Content;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.tool.ToolProvider;
import java.util.List;
import org.apache.camel.component.langchain4j.agent.api.Agent;
import org.apache.camel.component.langchain4j.agent.api.AgentConfiguration;
import org.apache.camel.component.langchain4j.agent.api.AiAgentBody;
import org.apache.camel.forage.agent.factory.ConfigurationAware;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple implementation of an AI agent that provides basic chat functionality
 */
public class SimpleAgent implements Agent, ConfigurationAware {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleAgent.class);

    private AgentConfiguration configuration;

    public SimpleAgent() {}

    @Override
    public void configure(AgentConfiguration configuration) {
        this.configuration = configuration;
    }

    private boolean hasMemory() {
        return configuration.getChatMemoryProvider() != null;
    }

    @Override
    public String chat(AiAgentBody aiAgentBody, ToolProvider toolProvider) {
        LOG.info("Chatting using ForageAgent");

        if (hasMemory()) {
            LOG.info("Chatting with memory");
            ForageAgentWithMemory agentService = createAiAgentService(toolProvider, ForageAgentWithMemory.class);

            return aiAgentBody.getSystemMessage() != null
                    ? agentService.chat(
                            aiAgentBody.getMemoryId(), aiAgentBody.getUserMessage(), aiAgentBody.getSystemMessage())
                    : agentService.chat(aiAgentBody.getMemoryId(), aiAgentBody.getUserMessage());
        } else {
            LOG.info("Chatting without memory");
            ForageAgentWithoutMemory agentService = createAiAgentService(toolProvider, ForageAgentWithoutMemory.class);

            if (aiAgentBody.getContent() != null) {
                if (aiAgentBody.getContent() instanceof List contents) {
                    return agentService.chat(aiAgentBody.getUserMessage(), contents);
                } else if (aiAgentBody.getContent() instanceof Content content) {
                    return agentService.chat(aiAgentBody.getUserMessage(), List.of(content));
                }
            }

            return aiAgentBody.getSystemMessage() != null
                    ? agentService.chat(aiAgentBody.getUserMessage(), aiAgentBody.getSystemMessage())
                    : agentService.chat(aiAgentBody.getUserMessage());
        }
    }

    /**
     * Create AI service with a single universal tool that handles multiple Camel routes and Memory Provider
     */
    private <T> T createAiAgentService(ToolProvider toolProvider, Class<T> clazz) {
        LOG.info("Creating of type {}", clazz.getSimpleName());
        AiServices<T> builder = AiServices.builder(clazz).chatModel(configuration.getChatModel());

        if (hasMemory()) {
            builder = builder.chatMemoryProvider(configuration.getChatMemoryProvider());
        }

        // Apache Camel Tool Provider
        if (toolProvider != null) {
            builder.toolProvider(toolProvider);
        }

        // RAG
        if (configuration.getRetrievalAugmentor() != null) {
            builder.retrievalAugmentor(configuration.getRetrievalAugmentor());
        }

        // Input Guardrails
        if (configuration.getInputGuardrailClasses() != null
                && !configuration.getInputGuardrailClasses().isEmpty()) {
            builder.inputGuardrailClasses((List) configuration.getInputGuardrailClasses());
        }

        // Output Guardrails
        if (configuration.getOutputGuardrailClasses() != null
                && !configuration.getOutputGuardrailClasses().isEmpty()) {
            builder.outputGuardrailClasses((List) configuration.getOutputGuardrailClasses());
        }

        return builder.build();
    }
}
