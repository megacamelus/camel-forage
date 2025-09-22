package org.apache.camel.forage.jdbc.common;

import java.io.InputStream;

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
}
