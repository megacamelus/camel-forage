package org.apache.camel.forage.vectordb.pgvector;

import dev.langchain4j.store.embedding.pgvector.PgVectorEmbeddingStore;
import org.apache.camel.forage.core.vectordb.EmbeddingStoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating PgVector embedding stores
 */
public class PgVectorProvider implements EmbeddingStoreProvider {
    private static final Logger LOG = LoggerFactory.getLogger(PgVectorProvider.class);

    private static final PgVectorConfig CONFIG = new PgVectorConfig();

    @Override
    public PgVectorEmbeddingStore newEmbeddingStore() {
        LOG.trace("Creating PgVector Embedding Store");

        return PgVectorEmbeddingStore.builder()
                .host(CONFIG.host())
                .port(CONFIG.port())
                .user(CONFIG.user())
                .password(CONFIG.password())
                .database(CONFIG.database())
                .table(CONFIG.table())
                .dimension(CONFIG.dimension())
                .useIndex(CONFIG.useIndex())
                .indexListSize(CONFIG.indexListSize())
                .createTable(CONFIG.createTable())
                .dropTableFirst(CONFIG.dropTableFirst())
                .metadataStorageConfig(CONFIG.metadataStorageConfig())
                .build();
    }
}
