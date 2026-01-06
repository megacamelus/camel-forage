package io.kaoto.forage.core.ai;

import dev.langchain4j.model.chat.ChatModel;
import io.kaoto.forage.core.common.BeanProvider;

/**
 * Provider interface for creating AI models
 */
public interface ModelProvider extends BeanProvider<ChatModel> {}
