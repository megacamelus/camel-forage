package io.kaoto.forage.memory.chat.messagewindow;

import static dev.langchain4j.data.message.ChatMessageDeserializer.messagesFromJson;
import static dev.langchain4j.data.message.ChatMessageSerializer.messagesToJson;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentChatMemoryStore implements ChatMemoryStore {
    private static final Logger LOG = LoggerFactory.getLogger(PersistentChatMemoryStore.class);
    private final Map<Object, String> memoryMap = new ConcurrentHashMap<>();

    public PersistentChatMemoryStore() {
        LOG.trace(
                "Creating PersistentChatMemoryStore {}", Thread.currentThread().getId());
    }

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String json = memoryMap.get(memoryId);
        return json != null ? messagesFromJson(json) : List.of();
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        String json = messagesToJson(messages);
        memoryMap.put(memoryId, json);
        if (LOG.isTraceEnabled()) {
            LOG.trace(
                    "Updated PersistentChatMemoryStore {}: {}",
                    Thread.currentThread().getId(),
                    memoryMap);
        }
    }

    @Override
    public void deleteMessages(Object memoryId) {
        if (LOG.isTraceEnabled()) {
            LOG.trace(
                    "Deleted PersistentChatMemoryStore {}: {}",
                    Thread.currentThread().getId(),
                    memoryMap);
        }
        memoryMap.remove(memoryId);
    }

    public int getMemoryCount() {
        return memoryMap.size();
    }

    public void clearAll() {
        LOG.trace(
                "Clearing PersistentChatMemoryStore {}", Thread.currentThread().getId());
        memoryMap.clear();
    }
}
