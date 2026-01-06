package io.kaoto.forage.maven.catalog;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Maven plugin goal to generate a catalog of Forage components.
 *
 * This Mojo scans the project dependencies and generates a comprehensive catalog
 * of all available Forage components, including their configuration options,
 * capabilities, and usage examples.
 */
@Mojo(
        name = "generate-catalog",
        defaultPhase = LifecyclePhase.PROCESS_CLASSES,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME,
        threadSafe = true)
public class GenerateCatalogMojo extends AbstractMojo {

    /**
     * The Maven project instance.
     */
    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    /**
     * The output directory where the catalog will be generated.
     */
    @Parameter(property = "forage.catalog.outputDirectory", defaultValue = "${project.build.directory}/forage-catalog")
    private File outputDirectory;

    /**
     * The format of the generated catalog.
     * Supported formats: json, yaml, markdown
     */
    @Parameter(property = "forage.catalog.format", defaultValue = "json")
    private String format;

    /**
     * The base directory
     */
    @Parameter(defaultValue = "${project.basedir}")
    protected File baseDir;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Generating Forage component catalog...");

        try {
            // Ensure output directory exists
            ensureOutputDirectory();

            // Initialize catalog generator
            CatalogGenerator generator = new CatalogGenerator(getLog());

            // Configure generator
            generator.setFormat(format);

            // Generate catalog
            CatalogResult result = generator.generateCatalog(project, outputDirectory);

            getLog().info(String.format(
                    "Catalog generation completed. Found %d components, generated %d files",
                    result.getComponentCount(), result.getGeneratedFileCount()));

            // Log generated files
            result.getGeneratedFiles().forEach(file -> getLog().info("Generated: " + file.getAbsolutePath()));

        } catch (Exception e) {
            throw new MojoExecutionException("Failed to generate Forage catalog", e);
        }
    }

    private void ensureOutputDirectory() throws IOException {
        Path outputPath = Paths.get(outputDirectory.getAbsolutePath());
        if (!Files.exists(outputPath)) {
            Files.createDirectories(outputPath);
            getLog().debug("Created output directory: " + outputPath);
        }
    }

    // Getters for testing
    public File getOutputDirectory() {
        return outputDirectory;
    }

    public String getFormat() {
        return format;
    }
}
