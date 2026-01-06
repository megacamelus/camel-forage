package io.kaoto.forage.memory.chat.tck;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import dev.langchain4j.data.message.AiMessage;
import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import io.kaoto.forage.core.ai.ChatMemoryBeanProvider;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Technology Compatibility Kit (TCK) for testing ChatMemoryFactory implementations.
 *
 * <p>This abstract test class provides a comprehensive suite of tests that all ChatMemoryFactory
 * implementations should pass. It validates the core functionality including:
 *
 * <ul>
 *   <li>Factory instantiation and configuration
 *   <li>ChatMemoryProvider creation
 *   <li>ChatMemory lifecycle and basic operations
 *   <li>Message storage and retrieval
 *   <li>Memory isolation between different memory IDs
 * </ul>
 *
 * <p>To use this TCK, extend this class and implement the {@link #createChatMemoryFactory()}
 * method to provide an instance of your ChatMemoryFactory implementation.
 *
 * <p><strong>Example usage:</strong>
 * <pre>{@code
 * public class MyMemoryFactoryTest extends ChatMemoryFactoryTCK {
 *     @Override
 *     protected ChatMemoryFactory createChatMemoryFactory() {
 *         return new MyMemoryFactory();
 *     }
 * }
 * }</pre>
 *
 * @since 1.0
 */
public abstract class ChatMemoryBeanProviderTCK {

    /**
     * Creates a ChatMemoryFactory instance for testing.
     *
     * <p>Implementations should return a properly configured instance of their
     * ChatMemoryFactory that is ready for testing. The factory should be able
     * to create functional ChatMemoryProvider instances.
     *
     * @return a ChatMemoryFactory instance for testing
     */
    protected abstract ChatMemoryBeanProvider createChatMemoryFactory();

    @Test
    void shouldCreateFactoryWithoutException() {
        assertThatCode(this::createChatMemoryFactory).doesNotThrowAnyException();
    }

    @Test
    void shouldCreateChatMemoryFactory() {
        ChatMemoryBeanProvider factory = createChatMemoryFactory();

        assertThat(factory).isNotNull();
    }

    @Test
    void shouldCreateChatMemoryProvider() {
        ChatMemoryBeanProvider factory = createChatMemoryFactory();

        ChatMemoryProvider provider = factory.create();

        assertThat(provider).isNotNull();
    }

    @Test
    void shouldCreateMultipleChatMemoryProviders() {
        ChatMemoryBeanProvider factory = createChatMemoryFactory();

        ChatMemoryProvider provider1 = factory.create();
        ChatMemoryProvider provider2 = factory.create();

        assertThat(provider1).isNotNull();
        assertThat(provider2).isNotNull();
        // Note: Implementations may return the same provider instance if they share state
        // What matters is that each provider can create independent ChatMemory instances
    }

    @Test
    void shouldCreateChatMemoryFromProvider() {
        ChatMemoryBeanProvider factory = createChatMemoryFactory();
        ChatMemoryProvider provider = factory.create();

        ChatMemory memory = provider.get("test-memory-id");

        assertThat(memory).isNotNull();
    }

    @Test
    void shouldStoreAndRetrieveMessages() {
        ChatMemoryBeanProvider factory = createChatMemoryFactory();
        ChatMemoryProvider provider = factory.create();
        ChatMemory memory = provider.get("test-memory-id");

        UserMessage userMessage = UserMessage.from("Hello, how are you?");
        AiMessage aiMessage = AiMessage.from("I'm doing well, thank you!");

        memory.add(userMessage);
        memory.add(aiMessage);

        List<ChatMessage> messages = memory.messages();

        assertThat(messages).hasSize(2);
        assertThat(messages.get(0)).isEqualTo(userMessage);
        assertThat(messages.get(1)).isEqualTo(aiMessage);
    }

    @Test
    void shouldIsolateMemoryBetweenDifferentIds() {
        ChatMemoryBeanProvider factory = createChatMemoryFactory();
        ChatMemoryProvider provider = factory.create();

        ChatMemory memory1 = provider.get("memory-1");
        ChatMemory memory2 = provider.get("memory-2");

        UserMessage message1 = UserMessage.from("Message for memory 1");
        UserMessage message2 = UserMessage.from("Message for memory 2");

        memory1.add(message1);
        memory2.add(message2);

        assertThat(memory1.messages()).hasSize(1);
        assertThat(memory1.messages().get(0)).isEqualTo(message1);

        assertThat(memory2.messages()).hasSize(1);
        assertThat(memory2.messages().get(0)).isEqualTo(message2);
    }

    @Test
    void shouldReturnSameMemoryForSameId() {
        ChatMemoryBeanProvider factory = createChatMemoryFactory();
        ChatMemoryProvider provider = factory.create();

        ChatMemory memory1 = provider.get("same-id");
        ChatMemory memory2 = provider.get("same-id");

        UserMessage message = UserMessage.from("Test message");
        memory1.add(message);

        List<ChatMessage> messagesFromMemory1 = memory1.messages();
        List<ChatMessage> messagesFromMemory2 = memory2.messages();

        assertThat(messagesFromMemory1).hasSize(1);
        assertThat(messagesFromMemory2).hasSize(1);
        assertThat(messagesFromMemory1.get(0)).isEqualTo(message);
        assertThat(messagesFromMemory2.get(0)).isEqualTo(message);
    }

    @Test
    void shouldHandleEmptyMemory() {
        ChatMemoryBeanProvider factory = createChatMemoryFactory();
        ChatMemoryProvider provider = factory.create();
        ChatMemory memory = provider.get("empty-memory");

        List<ChatMessage> messages = memory.messages();

        assertThat(messages).isEmpty();
    }

    @Test
    void shouldClearMemory() {
        ChatMemoryBeanProvider factory = createChatMemoryFactory();
        ChatMemoryProvider provider = factory.create();
        ChatMemory memory = provider.get("clear-test");

        memory.add(UserMessage.from("First message"));
        memory.add(AiMessage.from("Second message"));

        assertThat(memory.messages()).hasSize(2);

        memory.clear();

        assertThat(memory.messages()).isEmpty();
    }

    @Test
    void shouldHandleMultipleMessageTypes() {
        ChatMemoryBeanProvider factory = createChatMemoryFactory();
        ChatMemoryProvider provider = factory.create();
        ChatMemory memory = provider.get("multi-type-test");

        UserMessage userMessage = UserMessage.from("User question");
        AiMessage aiMessage = AiMessage.from("AI response");

        memory.add(userMessage);
        memory.add(aiMessage);

        List<ChatMessage> messages = memory.messages();

        assertThat(messages).hasSize(2);
        assertThat(messages.get(0)).isInstanceOf(UserMessage.class);
        assertThat(messages.get(1)).isInstanceOf(AiMessage.class);
        assertThat(((UserMessage) messages.get(0)).singleText()).isEqualTo("User question");
        assertThat(((AiMessage) messages.get(1)).text()).isEqualTo("AI response");
    }
}
