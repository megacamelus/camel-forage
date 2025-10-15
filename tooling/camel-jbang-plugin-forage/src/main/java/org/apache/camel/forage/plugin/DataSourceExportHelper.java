package org.apache.camel.forage.plugin;

import java.io.InputStream;

/**
 * Utility class for jdbc configuration value processing and transformation in the Camel Forage framework.
 */
public final class DataSourceExportHelper {

    /**
     * Regexp for See {@link org.apache.camel.forage.core.util.config.ConfigStore#readPrefixes(InputStream, String)}
     * to extract all datasource groups from the properties file.
     *
     * <p>From properties
     * <pre>
     *     ds1.jdbc.url=jdbc:postgresql://localhost:5432/postgres
     *     ds2.jdbc.url=jdbc:mysql://localhost:3306/test
     * </pre>
     *  both <Strong>ds1, ds2</Strong> prefixes are extracted.
     * </p>
     * */
    public static final String JDBC_PREFIXES_REGEXP = "(.+).jdbc\\..*";

    /**
     * Gets the quarkus version from the versions.properties file. (which is populated during buildtime)
     *
     * @return the project version
     */
    public static String getQuarkusVersion() {
        return getString("quarkus.version", "Could not determine quarkus version from properties file.");
    }

    /**
     * Gets the camel version from the versions.properties file. (which is populated during buildtime)
     *
     * @return the project version
     */
    public static String getCamelVersion() {
        return getString("camel.version", "Could not determine quarkus version from properties file.");
    }

    /**
     * Gets the quarkus version from the versions.properties file. (which is populated during buildtime)
     *
     * @return the project version
     */
    public static String geProjectVersion() {
        return getString("jdbc.dependency.version", "Could not determine project version from properties file.");
    }

    /**
     * Reads property from the file versions.properties (which contains build time resolved versions)
     */
    private static String getString(String key, String error) {
        try {
            java.util.Properties properties = new java.util.Properties();
            try (InputStream is = DataSourceExportHelper.class
                    .getClassLoader()
                    .getResourceAsStream("datasource-command.properties")) {
                if (is != null) {
                    properties.load(is);
                    String version = properties.getProperty(key);
                    if (version != null && !version.trim().isEmpty()) {
                        return version;
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(error, e);
        }

        // Ultimate fallback
        return null;
    }
}
