package org.apache.camel.forage.vectordb.mariadb;

import static org.assertj.core.api.Fail.fail;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
public class MariaDbIntegrationTest {
    private static final Logger LOG = LoggerFactory.getLogger(MariaDbIntegrationTest.class);
    private static final int EMBEDDING_DIMENSION = 4;

    @Container
    public static final MariaDBContainer<?> mariadbContainer = new MariaDBContainer<>("mariadb:11.8")
            .withDatabaseName("test_vectordb")
            .withUsername("test_user")
            .withPassword("test_password");

    /**
     * Sets up the MariaDB test environment using TestContainers.
     *
     * <p>This method:</p>
     * <ul>
     *   <li>Starts a MariaDB container with test database</li>
     *   <li>Configures system properties for the MariaDB provider</li>
     *   <li>Sets up test table configuration with auto-creation and cleanup</li>
     * </ul>
     */
    @BeforeAll
    public static void setupMariaDbConfiguration() {
        LOG.info("Starting MariaDB container: {}", mariadbContainer.getDockerImageName());

        // Extract connection details from the TestContainers instance
        String database = mariadbContainer.getDatabaseName();
        String username = mariadbContainer.getUsername();
        String password = mariadbContainer.getPassword();

        String jdbcUrl = mariadbContainer.getJdbcUrl();

        LOG.info("MariaDB container started - jdbcURL: {}", jdbcUrl);
        LOG.info("JDBC URL: {}", jdbcUrl);

        // Configure system properties for the MariaDB provider
        System.setProperty("mariadb.url", jdbcUrl);
        System.setProperty("mariadb.user", username);
        System.setProperty("mariadb.password", password);
        System.setProperty("mariadb.table", "test_embeddings");
        System.setProperty("mariadb.dimension", String.valueOf(EMBEDDING_DIMENSION));
        System.setProperty("mariadb.distance.type", "COSINE");
        System.setProperty("mariadb.create.table", "true"); // Auto-create table
        System.setProperty("mariadb.drop.table.first", "true"); // Clean start
    }

    /**
     * Cleans up the test environment by clearing system properties.
     *
     * <p>Removes all MariaDB-related system properties to avoid interference
     * with other tests. The container is automatically stopped by TestContainers.</p>
     */
    @AfterAll
    public static void teardownMariaDbConfiguration() {
        LOG.info("Cleaning up MariaDB system properties");

        // Clear all test-specific system properties
        System.clearProperty("mariadb.url");
        System.clearProperty("mariadb.user");
        System.clearProperty("mariadb.password");
        System.clearProperty("mariadb.table");
        System.clearProperty("mariadb.dimension");
        System.clearProperty("mariadb.distance.type");
        System.clearProperty("mariadb.create.table");
        System.clearProperty("mariadb.drop.table.first");

        LOG.info("MariaDB system properties cleared");
    }

    @Test
    public void shouldPerformBasicMariaDbOperations() {
        LOG.info("Testing basic MariaDB operations");

        // Create provider and embedding store using TestContainers MariaDB instance
        MariaDbProvider provider = new MariaDbProvider();
        EmbeddingStore<TextSegment> embeddingStore = provider.create();

        try {
            LOG.info("Testing basic embedding operations");

            // Create test embeddings with dimension matching EMBEDDING_DIMENSION (4)
            // These represent simple 4-dimensional vectors for testing
            float[] vector1 = {0.1f, 0.2f, 0.3f, 0.4f};
            float[] vector2 = {0.5f, 0.6f, 0.7f, 0.8f};

            Embedding embedding1 = new Embedding(vector1);
            Embedding embedding2 = new Embedding(vector2);

            // Create text segments with metadata for testing
            TextSegment segment1 = TextSegment.from("Test document 1", Metadata.from("key1", "value1"));
            TextSegment segment2 = TextSegment.from("Test document 2", Metadata.from("key2", "value2"));

            // Store embeddings in MariaDB - this tests the add operation
            embeddingStore.add(embedding1, segment1);
            embeddingStore.add(embedding2, segment2);

            // Perform similarity search using the first embedding as query
            // This tests the vector similarity search functionality
            EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                    .queryEmbedding(embedding1)
                    .maxResults(2) // Return up to 2 matches
                    .minScore(0.0) // Include all matches regardless of score
                    .build();

            EmbeddingSearchResult<TextSegment> searchResult = embeddingStore.search(searchRequest);

            // Verify search results
            org.assertj.core.api.Assertions.assertThat(searchResult).isNotNull();
            List<EmbeddingMatch<TextSegment>> matches = searchResult.matches();
            org.assertj.core.api.Assertions.assertThat(matches).isNotEmpty();

            // Log results for debugging
            LOG.info("Found {} matches", matches.size());
            for (EmbeddingMatch<TextSegment> match : matches) {
                LOG.info("Match: {} with score: {}", match.embedded().text(), match.score());
            }
        } catch (Exception e) {
            // This may fail in certain environments, which is acceptable for integration tests
            fail("MariaDB basic operations test failed (expected for some configurations): {}", e.getMessage());
        }
    }
}
