package org.apache.camel.forage.models.chat.multimodel;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import java.util.List;

public class MultiModelChatModel implements ChatModel {

    private final ModelSelector modelSelector;

    public MultiModelChatModel(ModelSelector modelSelector) {
        this.modelSelector = modelSelector;
    }

    @Override
    public String chat(String userMessage) {
        ChatModel selectedModel = modelSelector.select(userMessage);
        return selectedModel.chat(userMessage);
    }

    @Override
    public ChatResponse chat(ChatMessage... messages) {
        ChatModel selectedModel = modelSelector.select(messages);
        return selectedModel.chat(messages);
    }

    @Override
    public ChatResponse chat(List<ChatMessage> messages) {
        ChatModel selectedModel = modelSelector.select(messages);
        return selectedModel.chat(messages);
    }

    @Override
    public ChatResponse chat(ChatRequest chatRequest) {
        ChatModel selectedModel = modelSelector.select(chatRequest);
        return selectedModel.chat(chatRequest);
    }
}
