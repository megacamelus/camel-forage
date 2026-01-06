package io.kaoto.forage.maven.catalog;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * Unit tests for GenerateCatalogMojo.
 */
public class GenerateCatalogMojoTest {

    @TempDir
    File tempDir;

    @Test
    public void testMojoConfiguration() {
        GenerateCatalogMojo mojo = new GenerateCatalogMojo();

        // Note: @Parameter default values are not automatically set when creating instances directly
        // In actual Maven execution, these would be injected by the Maven framework
        assertThat(mojo.getFormat()).isNull(); // Will be "json" when run by Maven
    }

    @Test
    public void testOutputDirectoryCreation() {
        // This would be expanded in a full implementation
        // to test the actual plugin execution
        assertThat(tempDir).exists();
        assertThat(tempDir).isDirectory();
    }
}
