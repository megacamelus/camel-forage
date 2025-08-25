package org.apache.camel.forage.vectordb.milvus;

import dev.langchain4j.store.embedding.milvus.MilvusEmbeddingStore;
import org.apache.camel.forage.core.vectordb.EmbeddingStoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating Milvus Embedding Stores
 */
public class MilvusProvider implements EmbeddingStoreProvider {
    private static final Logger LOG = LoggerFactory.getLogger(MilvusProvider.class);

    private static final MilvusConfig CONFIG = new MilvusConfig();

    @Override
    public MilvusEmbeddingStore newEmbeddingStore() {
        LOG.trace("Creating Milvus Embedding Store");

        return MilvusEmbeddingStore.builder()
                .host(CONFIG.host())
                .port(CONFIG.port())
                .collectionName(CONFIG.collectionName())
                .dimension(CONFIG.dimension())
                .indexType(CONFIG.indexType())
                .metricType(CONFIG.metricType())
                .uri(CONFIG.uri())
                .token(CONFIG.token())
                .username(CONFIG.username())
                .password(CONFIG.password())
                .consistencyLevel(CONFIG.consistencyLevel())
                .retrieveEmbeddingsOnSearch(CONFIG.retrieveEmbeddingsOnSearch())
                .autoFlushOnInsert(CONFIG.autoFlushOnInsert())
                .build();
    }
}
