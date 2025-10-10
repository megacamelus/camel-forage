package org.apache.camel.forage.agent.simple;

import dev.langchain4j.data.message.Content;
import dev.langchain4j.service.UserMessage;
import java.util.List;

public interface ForageAgentWithoutMemory {

    /**
     * Simple AI service interface without memory support
     */
    String chat(String userMessage);

    String chat(String userMessage, String systemMessage);

    String chat(@UserMessage String userMessage, @UserMessage List<Content> contents);
}
