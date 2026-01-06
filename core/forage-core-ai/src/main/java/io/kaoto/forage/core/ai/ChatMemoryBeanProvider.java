package io.kaoto.forage.core.ai;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import io.kaoto.forage.core.common.BeanProvider;

/**
 * Creates chat memory provider. This is named like this to avoid confusion with the ChatMemoryProvider from LangChain4j
 */
public interface ChatMemoryBeanProvider extends BeanProvider<ChatMemoryProvider> {}
