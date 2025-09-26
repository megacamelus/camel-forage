package org.apache.camel.forage.jdbc.common;

import java.io.InputStream;
import org.apache.camel.dsl.jbang.core.common.RuntimeType;

/**
 * Utility class for jdbc configuration value processing and transformation in the Camel Forage framework.
 */
public final class DataSourceFactoryConfigHelper {

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
     * Utility method, to translate dbKind into {@link org.apache.camel.forage.jdbc.common.PooledDataSource}.
     *
     * <p>
     *  Examples:
     *  <ul>
     *      <li>postgresql -> org.apache.camel.forage.jdbc.postgres.PostgresJdbc</li>
     *      <li>mysql -> org.apache.camel.forage.jdbc.mysql.MysqlJdbc</li>
     *  </ul>
     *
     * </p>
     */
    public static String transformDbKindIntoProviderClass(String dbKind) {
        var db = dbKind.equals("postgresql") ? "postgres" : dbKind;

        return "org.apache.camel.forage.jdbc.%s.%sJdbc"
                .formatted(db, db.substring(0, 1).toUpperCase() + db.substring(1));
    }

    public static String getDbKindNameForRuntime(String dbKind, RuntimeType runtime) {
        switch (runtime) {
            case quarkus -> {
                if ("postgresql".equals(dbKind)) {
                    return "postgresql";
                }
            }
            case springBoot -> {
                if ("postgresql".equals(dbKind)) {
                    return "postgres";
                }
            }
        }

        return dbKind;
    }

    /**
     * Gets the quarkus version from the versions.properties file. (which is populated during buildtime)
     *
     * @return the project version
     */
    public static String getQuarkusVersion() {
        return getString("quarkus.version", "Could not determine quarkus version from properties file.");
    }

    /**
     * Gets the quarkus version from the versions.properties file. (which is populated during buildtime)
     *
     * @return the project version
     */
    public static String geProjectVersion() {
        return getString("project.version", "Could not determine project version from properties file.");
    }

    /**
     * Reads property from the file versions.properties (which contains build time resolved versions)
     */
    private static String getString(String key, String error) {
        try {
            java.util.Properties properties = new java.util.Properties();
            try (InputStream is =
                    DataSourceFactoryConfigHelper.class.getClassLoader().getResourceAsStream("versions.properties")) {
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
