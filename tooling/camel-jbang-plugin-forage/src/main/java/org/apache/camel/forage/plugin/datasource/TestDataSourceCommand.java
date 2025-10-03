package org.apache.camel.forage.plugin.datasource;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Set;
import javax.sql.DataSource;
import org.apache.camel.dsl.jbang.core.commands.CamelCommand;
import org.apache.camel.dsl.jbang.core.commands.CamelJBangMain;
import org.apache.camel.forage.core.jdbc.DataSourceProvider;
import org.apache.camel.forage.jdbc.common.DataSourceFactoryConfig;
import org.apache.camel.main.download.DependencyDownloaderClassLoader;
import org.apache.camel.main.download.MavenDependencyDownloader;
import org.apache.camel.tooling.maven.MavenArtifact;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import picocli.CommandLine;

/**
 * Command to test JDBC datasource connections and execute validation queries.
 * Supports multiple database types with automatic dependency downloading.
 */
@CommandLine.Command(
        name = "test-connection",
        description = "Test JDBC datasource connection and execute validation queries")
public class TestDataSourceCommand extends CamelCommand {

    @CommandLine.Parameters(description = "DataSource configuration name (for multiple datasources)", arity = "0..1")
    private String dataSourceName;

    @CommandLine.Option(
            names = {"-v", "--verbose"},
            description = "Enable verbose logging")
    private boolean verbose;

    public TestDataSourceCommand(CamelJBangMain main) {
        super(main);
    }

    /**
     * Executes the datasource connection test.
     *
     * @return 0 if successful, 1 if failed
     * @throws Exception if command execution fails
     */
    @Override
    public Integer doCall() throws Exception {
        try {
            DataSourceFactoryConfig dsFactoryConfig = new DataSourceFactoryConfig(dataSourceName);
            String dbKind = dsFactoryConfig.dbKind().toLowerCase();

            printer().println("Testing connection for database: " + dbKind);
            if (dataSourceName != null) {
                printer().println("Using configuration: " + dataSourceName);
            }

            ClassLoader dbClassLoader = loadJdbcDependency(dbKind);

            DataSourceProvider dataSourceProvider = createDataSourceProvider(dbKind, dbClassLoader);

            return testConnection(dataSourceProvider.create(dataSourceName), dataSourceProvider.getTestQuery());
        } catch (Exception e) {
            printer().printErr("Failed to test datasource connection: " + e.getMessage());
            if (verbose) {
                e.printStackTrace();
                printer().printErr(e);
            }
            return 1;
        }
    }

    /**
     * Creates a datasource using the specified provider class and classloader.
     *
     * @param dbKind the database type
     * @param classLoader the classloader containing the provider
     * @return configured DataSource instance
     * @throws Exception if datasource creation fails
     */
    private DataSourceProvider createDataSourceProvider(String dbKind, ClassLoader classLoader) throws Exception {
        Set<Class<? extends DataSourceProvider>> providers = findImplementations(classLoader);

        DataSourceProvider dataSourceProvider = providers.stream()
                .filter(dsProvider -> dsProvider.getName().toLowerCase().contains(dbKind.toLowerCase()))
                .map(dsProvider -> {
                    try {
                        return dsProvider.getDeclaredConstructor().newInstance();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("No implementation found for " + dbKind));

        return dataSourceProvider;
    }

    /**
     * Downloads and loads JDBC dependencies for the specified database kind.
     *
     * @param kind the database kind (e.g., "postgres", "mysql")
     * @return classloader with loaded JDBC dependencies
     * @throws IllegalStateException if dependency loading fails
     */
    private ClassLoader loadJdbcDependency(String kind) {
        try {
            printer().println("Loading JDBC dependencies for: " + kind);

            DependencyDownloaderClassLoader classLoader =
                    new DependencyDownloaderClassLoader(TestDataSourceCommand.class.getClassLoader());

            MavenDependencyDownloader downloader = new MavenDependencyDownloader();
            downloader.setClassLoader(classLoader);
            downloader.start();

            String artifactId = "forage-jdbc-" + kind;
            String version = getProjectVersion();

            printer().println("Downloading dependency: org.apache.camel.forage:" + artifactId + ":" + version);
            downloader.downloadDependency("org.apache.camel.forage", artifactId, version, true);

            MavenArtifact artifact = downloader.downloadArtifact("org.apache.camel.forage", artifactId, version);
            classLoader.addFile(artifact.getFile());

            Thread.currentThread().setContextClassLoader(classLoader);
            printer().println("Dependencies loaded successfully");

            return classLoader;
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load JDBC dependency for: " + kind, e);
        }
    }

    /**
     * Gets the project version from the datasource-command.properties file.
     * Falls back to a default version if unable to determine.
     *
     * @return the project version
     */
    private String getProjectVersion() {
        try {
            java.util.Properties properties = new java.util.Properties();
            try (java.io.InputStream is =
                    this.getClass().getClassLoader().getResourceAsStream("datasource-command.properties")) {
                if (is != null) {
                    properties.load(is);
                    String version = properties.getProperty("jdbc.dependency.version");
                    if (version != null && !version.trim().isEmpty()) {
                        return version;
                    }
                }
            }
        } catch (Exception e) {
            if (verbose) {
                printer()
                        .printErr(
                                "Warning: Could not determine project version from properties file, using fallback", e);
            }
        }

        // Ultimate fallback
        return "1.0-SNAPSHOT";
    }

    /**
     * Tests the database connection and executes a validation query.
     *
     * @param dataSource The DataSource to test
     * @param testQuery The SQL query to execute for testing
     * @return 0 if connection test successful, 1 otherwise
     */
    private int testConnection(DataSource dataSource, String testQuery) {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            printer().println("Establishing database connection...");
            connection = dataSource.getConnection();
            printer().println("  Database connection established successfully");

            // Display connection information
            String databaseProductName = connection.getMetaData().getDatabaseProductName();
            String databaseProductVersion = connection.getMetaData().getDatabaseProductVersion();
            String driverName = connection.getMetaData().getDriverName();
            String url = connection.getMetaData().getURL();
            String userName = connection.getMetaData().getUserName();

            printer().println("");
            printer().println("Connection Details:");
            printer().println("  Database: " + databaseProductName);
            printer().println("  Version: " + databaseProductVersion);
            printer().println("  Driver: " + driverName);
            printer().println("  URL: " + url);
            printer().println("  User: " + (userName != null ? userName : "<unknown>"));

            // Execute validation query
            if (testQuery != null && !testQuery.trim().isEmpty()) {
                printer().println("");
                printer().println("Executing validation query: " + testQuery);

                statement = connection.createStatement();
                resultSet = statement.executeQuery(testQuery);

                printer().println("  Validation query executed successfully");

                // Display query results
                int columnCount = resultSet.getMetaData().getColumnCount();
                if (resultSet.next()) {
                    StringBuilder result = new StringBuilder("  Result: ");
                    for (int i = 1; i <= columnCount; i++) {
                        if (i > 1) result.append(" | ");
                        String value = resultSet.getString(i);
                        result.append(value != null ? value.trim() : "<null>");
                    }
                    printer().println(result.toString());
                }
            }

            // Test connection validity
            printer().println("");
            printer().println("Validating connection health...");
            if (connection.isValid(5)) {
                printer().println("  Connection is valid and responsive");
                printer().println("");
                printer().println("  Database connection test completed successfully!");
                return 0;
            } else {
                printer().println("  Connection validation failed");
                return 1;
            }

        } catch (SQLException e) {
            printer().println("");
            printer().printErr("  Database connection test failed");
            printer().printErr("Error: " + e.getMessage());

            if (e.getSQLState() != null) {
                printer().printErr("SQL State: " + e.getSQLState());
            }
            if (e.getErrorCode() != 0) {
                printer().printErr("Error Code: " + e.getErrorCode());
            }

            // Provide helpful hints for common issues
            String message = e.getMessage().toLowerCase();
            if (message.contains("connection refused") || message.contains("unable to connect")) {
                printer().printErr("");
                printer().printErr("  Troubleshooting hints:");
                printer().printErr("   • Check if the database server is running");
                printer().printErr("   • Verify the host and port in your JDBC URL");
                printer().printErr("   • Check network connectivity and firewall settings");
            } else if (message.contains("authentication")
                    || message.contains("password")
                    || message.contains("login")) {
                printer().printErr("");
                printer().printErr("  Authentication issue:");
                printer().printErr("   • Verify username and password are correct");
                printer().printErr("   • Check database user permissions");
            }

            if (verbose) {
                printer().printErr(e);
            }
            return 1;
        } finally {
            closeResources(resultSet, statement, connection);
        }
    }

    /**
     * Safely closes database resources in reverse order of creation.
     */
    private void closeResources(ResultSet resultSet, Statement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                if (verbose) {
                    printer().printErr("Warning: Error closing ResultSet", e);
                }
            }
        }

        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                if (verbose) {
                    printer().printErr("Warning: Error closing Statement", e);
                }
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                if (verbose) {
                    printer().printErr("Warning: Error closing Connection", e);
                }
            }
        }
    }

    private Set<Class<? extends DataSourceProvider>> findImplementations(ClassLoader classLoader) {

        // Create Reflections configuration with specific ClassLoader
        Reflections reflections = new Reflections(new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forClassLoader(classLoader))
                .addClassLoaders(classLoader)
                .addScanners(Scanners.SubTypes));

        // Get all subtypes (implementations) of the interface
        return reflections.getSubTypesOf(DataSourceProvider.class);
    }
}
