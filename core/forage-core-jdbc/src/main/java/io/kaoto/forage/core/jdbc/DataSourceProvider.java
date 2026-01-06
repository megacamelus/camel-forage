package io.kaoto.forage.core.jdbc;

import io.kaoto.forage.core.common.BeanProvider;
import javax.sql.DataSource;

/**
 * Provider interface for creating JDBC DataSource instances with optional named configurations.
 * Implementations should provide database-specific DataSource creation with connection pooling
 * and transaction management capabilities.
 */
public interface DataSourceProvider extends BeanProvider<DataSource> {

    /**
     * Creates a DataSource with default configuration.
     *
     * @return configured DataSource instance
     */
    @Override
    default DataSource create() {
        return create(null);
    }

    /**
     * Creates a DataSource with named/prefixed configuration.
     * This allows multiple DataSource instances with different configurations.
     *
     * @param id optional configuration prefix for named instances
     * @return configured DataSource instance
     */
    @Override
    DataSource create(String id);

    /**
     * Return a query that can be used to test the connection
     * to the database
     *
     * @return the query
     */
    String getTestQuery();
}
