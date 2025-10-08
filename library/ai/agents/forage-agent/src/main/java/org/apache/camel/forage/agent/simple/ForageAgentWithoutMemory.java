package org.apache.camel.forage.agent.simple;

import dev.langchain4j.data.message.Content;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import java.util.List;

public interface ForageAgentWithoutMemory {

    /**
     * Simple AI service interface without memory support
     */
    String chat(@UserMessage String userMessage);

    @SystemMessage("{{prompt}}")
    String chat(@UserMessage String userMessage, @V("prompt") String systemMessage);

    String chat(@UserMessage String userMessage, @UserMessage List<Content> contents);
}
