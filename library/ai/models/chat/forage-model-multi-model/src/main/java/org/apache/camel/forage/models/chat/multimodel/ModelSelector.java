package org.apache.camel.forage.models.chat.multimodel;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import java.util.List;

public interface ModelSelector {

    void add(ChatModel model);

    ChatModel select(String userMessage);

    ChatModel select(ChatMessage... messages);

    ChatModel select(List<ChatMessage> messages);

    ChatModel select(ChatRequest chatRequest);
}
