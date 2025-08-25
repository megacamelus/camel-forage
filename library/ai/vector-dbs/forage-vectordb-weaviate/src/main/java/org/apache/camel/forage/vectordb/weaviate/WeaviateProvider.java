package org.apache.camel.forage.vectordb.weaviate;

import dev.langchain4j.store.embedding.weaviate.WeaviateEmbeddingStore;
import org.apache.camel.forage.core.vectordb.EmbeddingStoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Provider for creating Google Gemini chat models
 */
public class WeaviateProvider implements EmbeddingStoreProvider {
    private static final Logger LOG = LoggerFactory.getLogger(WeaviateProvider.class);

    private static final WeaviateConfig CONFIG = new WeaviateConfig();

    @Override
    public WeaviateEmbeddingStore newEmbeddingStore() {
        LOG.trace("Creating weaviate embedding store");

        return WeaviateEmbeddingStore.builder()
                .apiKey(CONFIG.apiKey())
                .scheme(CONFIG.scheme())
                .host(CONFIG.host())
                .port(CONFIG.port())
                .useGrpcForInserts(CONFIG.useGrpcForInserts())
                .securedGrpc(CONFIG.securedGrpc())
                .grpcPort(CONFIG.grpcPort())
                .objectClass(CONFIG.objectClass())
                .avoidDups(CONFIG.avoidDups())
                .consistencyLevel(CONFIG.consistencyLevel())
                .metadataKeys(CONFIG.metadataKeys())
                .textFieldName(CONFIG.textFieldName())
                .metadataFieldName(CONFIG.metadataFieldName())
                .build();
    }
}
