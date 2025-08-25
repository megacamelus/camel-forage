package org.apache.camel.forage.core.vectordb;

import dev.langchain4j.store.embedding.EmbeddingStore;
import org.apache.camel.CamelContext;

public interface EmbeddingStoreFactory {
    void setCamelContext(CamelContext camelContext);

    EmbeddingStore createEmbeddingStore();
}
