package io.kaoto.forage.vectordb.weaviate;

import static org.assertj.core.api.Fail.fail;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import java.net.URL;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.weaviate.WeaviateContainer;

@Testcontainers
public class WeaviateIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(WeaviateIntegrationTest.class);

    @Container
    public static final WeaviateContainer weaviateContainer =
            new WeaviateContainer("cr.weaviate.io/semitechnologies/weaviate:1.25.5");

    @BeforeAll
    public static void setupWeaviateConfiguration() {
        LOG.info("Setting up Weaviate configuration with container: {}", weaviateContainer.getDockerImageName());

        String host = weaviateContainer.getHost();

        URL httpUrl = null;
        URL grpcUrl = null;
        try {
            httpUrl = new URL("http://" + weaviateContainer.getHttpHostAddress());
            grpcUrl = new URL("http://" + weaviateContainer.getGrpcHostAddress());
        } catch (java.net.MalformedURLException mue) {
            mue.printStackTrace();
        }

        Integer httpPort = httpUrl.getPort();
        Integer grpcPort = grpcUrl.getPort();

        LOG.info("Weaviate container configured - Host: {}, HTTP Port: {}, GRPC Port: {}", host, httpPort, grpcPort);

        System.setProperty("weaviate.scheme", "http");
        System.setProperty("weaviate.host", host);
        System.setProperty("weaviate.port", httpPort.toString());
        System.setProperty("weaviate.grpc.port", grpcPort.toString());
        System.setProperty("weaviate.object.class", "TestDocument");
        System.setProperty("weaviate.use.grpc.for.inserts", "false");
        System.setProperty("weaviate.secured.grpc", "false");
    }

    @AfterAll
    public static void teardownWeaviateConfiguration() {
        LOG.info("Cleaning up Weaviate system properties");

        System.clearProperty("weaviate.scheme");
        System.clearProperty("weaviate.host");
        System.clearProperty("weaviate.port");
        System.clearProperty("weaviate.grpc.port");
        System.clearProperty("weaviate.object.class");
        System.clearProperty("weaviate.use.grpc.for.inserts");
        System.clearProperty("weaviate.secured.grpc");

        LOG.info("Weaviate system properties cleared");
    }

    @Test
    public void shouldPerformBasicWeaviateOperations() {
        LOG.info("Testing basic Weaviate operations");

        WeaviateProvider provider = new WeaviateProvider();
        EmbeddingStore<TextSegment> embeddingStore = provider.create();

        try {
            LOG.info("Testing basic embedding operations");

            // Create test embeddings
            float[] vector1 = {0.1f, 0.2f, 0.3f, 0.4f};
            float[] vector2 = {0.5f, 0.6f, 0.7f, 0.8f};

            Embedding embedding1 = new Embedding(vector1);
            Embedding embedding2 = new Embedding(vector2);

            TextSegment segment1 = TextSegment.from("Test document 1", Metadata.from("key1", "value1"));
            TextSegment segment2 = TextSegment.from("Test document 2", Metadata.from("key2", "value2"));

            // Add embeddings
            embeddingStore.add(embedding1, segment1);
            embeddingStore.add(embedding2, segment2);

            // Search for similar embeddings
            EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(embedding1)
                    .maxResults(2)
                    .minScore(0.0)
                    .build();

            EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

            org.assertj.core.api.Assertions.assertThat(searchResult).isNotNull();
            List<EmbeddingMatch<TextSegment>> matches = searchResult.matches();
            org.assertj.core.api.Assertions.assertThat(matches).isNotEmpty();

            LOG.info("Found {} matches", matches.size());
            for (EmbeddingMatch<TextSegment> match : matches) {
                LOG.info("Match: {} with score: {}", match.embedded().text(), match.score());
            }
        } catch (Exception e) {
            fail("Weaviate basic operations test failed (expected for some configurations): {}", e.getMessage());
        }
    }
}
