package org.apache.camel.forage.vectordb.pinecone;

import dev.langchain4j.store.embedding.pinecone.PineconeEmbeddingStore;
import org.apache.camel.forage.core.vectordb.EmbeddingStoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating Google Gemini chat models
 */
public class PineconeProvider implements EmbeddingStoreProvider {
    private static final Logger LOG = LoggerFactory.getLogger(PineconeProvider.class);

    private static final PineconeConfig CONFIG = new PineconeConfig();

    @Override
    public PineconeEmbeddingStore newEmbeddingStore() {
        LOG.trace("Creating pinecone embedding store");

        return PineconeEmbeddingStore.builder()
                .apiKey(CONFIG.apiKey())
                .index(CONFIG.index())
                .nameSpace(CONFIG.nameSpace())
                .createIndex(null)
                .metadataTextKey(CONFIG.metadataTextKey())
                .environment(CONFIG.environment())
                .projectId(CONFIG.projectId())
                .build();
    }
}
