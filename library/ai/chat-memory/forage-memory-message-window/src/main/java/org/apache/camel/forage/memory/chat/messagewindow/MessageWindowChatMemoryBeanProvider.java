package org.apache.camel.forage.memory.chat.messagewindow;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import org.apache.camel.forage.core.ai.ChatMemoryBeanProvider;
import org.apache.camel.forage.core.annotations.ForageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ForageBean(
        value = "message-window",
        components = {"camel-langchain4j-agent"},
        description = "Message window chat memory factory")
public class MessageWindowChatMemoryBeanProvider implements ChatMemoryBeanProvider {
    private static final Logger LOG = LoggerFactory.getLogger(MessageWindowChatMemoryBeanProvider.class);

    private static final PersistentChatMemoryStore PERSISTENT_CHAT_MEMORY_STORE = new PersistentChatMemoryStore();
    private final ChatMemoryProvider chatMemoryProvider;

    public MessageWindowChatMemoryBeanProvider() {
        chatMemoryProvider = getChatMemoryProvider();
    }

    @Override
    public synchronized ChatMemoryProvider create() {
        return chatMemoryProvider;
    }

    @Override
    public ChatMemoryProvider create(String id) {
        throw new UnsupportedOperationException(
                "Named chat memory stores are not yet supported for the memory chat window");
    }

    private static ChatMemoryProvider getChatMemoryProvider() {
        LOG.trace("Creating MessageWindowChatMemoryFactory");
        return memoryId -> MessageWindowChatMemory.builder()
                .id(memoryId)
                .maxMessages(10)
                .chatMemoryStore(PERSISTENT_CHAT_MEMORY_STORE)
                .build();
    }
}
