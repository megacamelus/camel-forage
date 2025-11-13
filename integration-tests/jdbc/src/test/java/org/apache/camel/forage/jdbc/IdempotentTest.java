package org.apache.camel.forage.jdbc;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import org.apache.camel.forage.integration.tests.IntegrationTestSetupExtension;
import org.citrusframework.GherkinTestActionRunner;
import org.citrusframework.TestActionSupport;
import org.citrusframework.annotations.CitrusResource;
import org.citrusframework.annotations.CitrusTest;
import org.citrusframework.junit.jupiter.CitrusSupport;
import org.citrusframework.spi.Resource;
import org.citrusframework.spi.Resources;
import org.eclipse.microprofile.config.ConfigProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@CitrusSupport
@Testcontainers
@ExtendWith(IntegrationTestSetupExtension.class)
public class IdempotentTest implements TestActionSupport {

    static final String IMAGE_NAME = ConfigProvider.getConfig().getValue("postgres.container.image", String.class);
    static final Path INPUT_FOLDER = Paths.get("target", "data", "in");

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
                    DockerImageName.parse(IMAGE_NAME).asCompatibleSubstituteFor("postgres"))
            .withExposedPorts(5432)
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("postgresql");

    @BeforeAll
    static void setPathIntoRoute() throws IOException {
        Resource resource = Resources.fromClasspath(
                IdempotentTest.class.getSimpleName() + "/jdbc-idempotent.camel.yaml", IdempotentTest.class);
        try (InputStream is = resource.getInputStream()) {
            String content = new String(is.readAllBytes());
            // Use hardcoded path
            Files.createDirectories(INPUT_FOLDER);
            content = content.replace("${path}", INPUT_FOLDER.toAbsolutePath().toString());

            Files.writeString(
                    resource.getFile().toPath().getParent().resolve("jdbc-idempotent-resolved.camel.yaml"), content);
        }
    }

    @Test
    @CitrusTest()
    public void aggregationTest(@CitrusResource GherkinTestActionRunner runner)
            throws IOException, InterruptedException {
        Files.createDirectories(INPUT_FOLDER);

        // running jbang forage run with required resources and required runtime
        runner.given(camel().jbang()
                .custom("forage", "run")
                .processName("route")
                .addResource(Resources.fromClasspath(
                        getClass().getSimpleName() + "/jdbc-idempotent-resolved.camel.yaml", getClass()))
                .addResource(Resources.fromClasspath(
                        getClass().getSimpleName() + "/forage-datasource-factory.properties", getClass()))
                .withArg(System.getProperty(IntegrationTestSetupExtension.RUNTIME_PROPERTY))
                .withEnvs(Collections.singletonMap("JDBC_URL", postgres.getJdbcUrl()))
                .dumpIntegrationOutput(true));

        // create a temp file with content `A`
        Path fileA = Files.write(Files.createTempFile("tempFile", ".txt"), "A".getBytes(), StandardOpenOption.WRITE);

        // copy test.txt to input folder
        Files.move(fileA, INPUT_FOLDER.resolve("test.txt"));

        // validation of logged message
        runner.then(camel().jbang()
                .verify()
                .integration("route")
                .waitForLogMessage("Processed file: test.txt with content: A"));

        // create a temp file with content `A`
        Path fileB = Files.write(Files.createTempFile("tempFile", ".txt"), "B".getBytes(), StandardOpenOption.WRITE);

        // copy test.txt to input folder
        Files.move(fileB, INPUT_FOLDER.resolve("test.txt"));

        String error = null;
        try {
            runner.then(camel().jbang()
                    .verify()
                    .integration("route")
                    .maxAttempts(3)
                    .delayBetweenAttempts(5000)
                    .waitForLogMessage("Processed file: test.txt with content: B"));
        } catch (Exception e) {
            error = e.getMessage();
        }
        Assertions.assertTrue(error != null && error.startsWith("Action timeout after"));
    }
}
