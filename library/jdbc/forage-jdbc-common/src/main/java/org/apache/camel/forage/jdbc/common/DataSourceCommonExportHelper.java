package org.apache.camel.forage.jdbc.common;

/**
 * Utility class for jdbc configuration value processing and transformation in the Camel Forage framework.
 */
public final class DataSourceCommonExportHelper {

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
        return "org.apache.camel.forage.jdbc.%s.%sJdbc"
                .formatted(dbKind, dbKind.substring(0, 1).toUpperCase() + dbKind.substring(1));
    }
}
