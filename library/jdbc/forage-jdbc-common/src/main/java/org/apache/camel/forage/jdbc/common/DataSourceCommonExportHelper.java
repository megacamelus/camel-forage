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
        var db = dbKind.equals("postgresql") ? "postgres" : dbKind;

        return "org.apache.camel.forage.jdbc.%s.%sJdbc"
                .formatted(db, db.substring(0, 1).toUpperCase() + db.substring(1));
    }
}
