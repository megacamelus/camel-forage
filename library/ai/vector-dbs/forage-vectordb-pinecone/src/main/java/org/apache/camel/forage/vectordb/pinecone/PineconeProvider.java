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

    @Override
    public PineconeEmbeddingStore create(String id) {
        LOG.trace("Creating pinecone embedding store");

        PineconeConfig config = new PineconeConfig(id);

        return PineconeEmbeddingStore.builder()
                .apiKey(config.apiKey())
                .index(config.index())
                .nameSpace(config.nameSpace())
                .createIndex(null)
                .metadataTextKey(config.metadataTextKey())
                .build();
    }
}
