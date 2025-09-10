package org.apache.camel.forage.models.chat.multimodel;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.request.ChatRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MultiChatModelTest {

    @Mock
    private ModelSelector modelSelector;

    @Mock
    private ChatModel selectedModel;

    @Test
    void shouldSelectModelForStringInput() {
        String userMessage = "Hello, world!";
        String expectedResponse = "Hi there!";

        Mockito.when(modelSelector.select(userMessage)).thenReturn(selectedModel);
        Mockito.when(selectedModel.chat(userMessage)).thenReturn(expectedResponse);

        MultiModelChatModel multiModelChatModel = new MultiModelChatModel(modelSelector);
        String response = multiModelChatModel.chat(userMessage);

        Assertions.assertThat(response).isEqualTo(expectedResponse);
        Mockito.verify(modelSelector).select(userMessage);
        Mockito.verify(selectedModel).chat(userMessage);
    }

    @Test
    void shouldSelectModelForChatMessagesVarargs() {
        ChatMessage message1 = UserMessage.from("Hello");
        ChatMessage message2 = UserMessage.from("How are you?");
        ChatResponse expectedResponse = Mockito.mock(ChatResponse.class);

        Mockito.when(modelSelector.select(message1, message2)).thenReturn(selectedModel);
        Mockito.when(selectedModel.chat(message1, message2)).thenReturn(expectedResponse);

        MultiModelChatModel multiModelChatModel = new MultiModelChatModel(modelSelector);
        ChatResponse response = multiModelChatModel.chat(message1, message2);

        Assertions.assertThat(response).isEqualTo(expectedResponse);
        Mockito.verify(modelSelector).select(message1, message2);
        Mockito.verify(selectedModel).chat(message1, message2);
    }

    @Test
    void shouldSelectModelForChatMessagesList() {
        List<ChatMessage> messages = List.of(UserMessage.from("Hello"), UserMessage.from("How are you?"));
        ChatResponse expectedResponse = Mockito.mock(ChatResponse.class);

        Mockito.when(modelSelector.select(messages)).thenReturn(selectedModel);
        Mockito.when(selectedModel.chat(messages)).thenReturn(expectedResponse);

        MultiModelChatModel multiModelChatModel = new MultiModelChatModel(modelSelector);
        ChatResponse response = multiModelChatModel.chat(messages);

        Assertions.assertThat(response).isEqualTo(expectedResponse);
        Mockito.verify(modelSelector).select(messages);
        Mockito.verify(selectedModel).chat(messages);
    }

    @Test
    void shouldSelectModelForChatRequest() {
        ChatRequest chatRequest =
                ChatRequest.builder().messages(UserMessage.from("Hello")).build();
        ChatResponse expectedResponse = Mockito.mock(ChatResponse.class);

        Mockito.when(modelSelector.select(chatRequest)).thenReturn(selectedModel);
        Mockito.when(selectedModel.chat(chatRequest)).thenReturn(expectedResponse);

        MultiModelChatModel multiModelChatModel = new MultiModelChatModel(modelSelector);
        ChatResponse response = multiModelChatModel.chat(chatRequest);

        Assertions.assertThat(response).isEqualTo(expectedResponse);
        Mockito.verify(modelSelector).select(chatRequest);
        Mockito.verify(selectedModel).chat(chatRequest);
    }
}
