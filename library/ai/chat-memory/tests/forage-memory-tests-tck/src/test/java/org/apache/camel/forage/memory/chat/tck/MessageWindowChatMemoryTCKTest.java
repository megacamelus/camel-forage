package org.apache.camel.forage.memory.chat.tck;

import org.apache.camel.forage.core.ai.ChatMemoryFactory;
import org.apache.camel.forage.memory.chat.messagewindow.MessageWindowChatMemoryFactory;
import org.junit.jupiter.api.Test;

/**
 * Test for MessageWindowChatMemoryFactory using the ChatMemoryFactoryTCK.
 *
 * <p>This test validates the MessageWindowChatMemoryFactory implementation
 * from the forage-memory-message-window module against the comprehensive
 * test suite provided by the TCK.
 *
 * @since 1.0
 */
class MessageWindowChatMemoryTCKTest extends ChatMemoryFactoryTCK {

    @Override
    protected ChatMemoryFactory createChatMemoryFactory() {
        return new MessageWindowChatMemoryFactory();
    }

    @Test
    void demonstratesMessageWindowTCKUsage() {
        // This test exists to demonstrate that the TCK is working
        // All actual tests are inherited from ChatMemoryFactoryTCK
    }
}
