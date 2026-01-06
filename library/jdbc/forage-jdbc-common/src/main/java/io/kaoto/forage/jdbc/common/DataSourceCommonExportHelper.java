package io.kaoto.forage.jdbc.common;

/**
 * Utility class for jdbc configuration value processing and transformation in the Forage framework.
 */
public final class DataSourceCommonExportHelper {

    /**
     * Utility method, to translate dbKind into {@link io.kaoto.forage.jdbc.common.PooledDataSource}.
     *
     * <p>
     *  Examples:
     *  <ul>
     *      <li>postgresql -> io.kaoto.forage.jdbc.postgres.PostgresJdbc</li>
     *      <li>mysql -> io.kaoto.forage.jdbc.mysql.MysqlJdbc</li>
     *  </ul>
     *
     * </p>
     */
    public static String transformDbKindIntoProviderClass(String dbKind) {
        return "io.kaoto.forage.jdbc.%s.%sJdbc"
                .formatted(dbKind, dbKind.substring(0, 1).toUpperCase() + dbKind.substring(1));
    }
}
