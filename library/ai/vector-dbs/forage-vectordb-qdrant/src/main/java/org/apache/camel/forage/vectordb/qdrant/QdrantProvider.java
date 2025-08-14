package org.apache.camel.forage.vectordb.qdrant;

import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore;
import org.apache.camel.forage.core.vectordb.EmbeddingStoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating Qdrant embedding stores
 */
public class QdrantProvider implements EmbeddingStoreProvider {
    private static final Logger LOG = LoggerFactory.getLogger(QdrantProvider.class);

    private static final QdrantConfig CONFIG = new QdrantConfig();

    @Override
    public QdrantEmbeddingStore newEmbeddingStore() {
        LOG.trace("Creating qdrant embedding store");

        return QdrantEmbeddingStore.builder()
                .collectionName(CONFIG.collectionName())
                .apiKey(CONFIG.apiKey())
                .host(CONFIG.host())
                .port(CONFIG.port())
                .useTls(CONFIG.useTls())
                .payloadTextKey(CONFIG.payloadTextKey())
                .build();
    }
}
