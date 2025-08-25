package org.apache.camel.forage.core.vectordb;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;

/**
 * Provider interface for creating AI embedding stores
 */
public interface EmbeddingStoreProvider {

    /**
     * Creates a new EmbeddingStore instance
     * @return the created model
     */
    EmbeddingStore<TextSegment> newEmbeddingStore();
}
