package io.kaoto.forage.jdbc.common.idempotent;

import io.kaoto.forage.jdbc.common.DataSourceFactoryConfig;
import javax.sql.DataSource;
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

public class ForageJdbcMessageIdRepository extends JdbcMessageIdRepository {

    public static final String DEFAULT_TABLENAME = JdbcMessageIdRepository.DEFAULT_TABLENAME;
    public static final String DEFAULT_TABLE_EXISTS_STRING = JdbcMessageIdRepository.DEFAULT_TABLE_EXISTS_STRING;
    public static final String DEFAULT_CREATE_STRING = JdbcMessageIdRepository.DEFAULT_CREATE_STRING;
    public static final String DEFAULT_QUERY_STRING = JdbcMessageIdRepository.DEFAULT_QUERY_STRING;
    public static final String DEFAULT_INSERT_STRING = JdbcMessageIdRepository.DEFAULT_INSERT_STRING;
    public static final String DEFAULT_DELETE_STRING = JdbcMessageIdRepository.DEFAULT_DELETE_STRING;
    public static final String DEFAULT_CLEAR_STRING = JdbcMessageIdRepository.DEFAULT_CLEAR_STRING;

    public ForageJdbcMessageIdRepository(
            DataSourceFactoryConfig config, DataSource dataSource, ForageIdRepository forageIdRepository) {
        setTableName(config.idempotentRepositoryTableName());

        setJdbcTemplate(new JdbcTemplate(dataSource));
        setDataSource(dataSource);
        setCreateString(forageIdRepository.createString());
        setClearString(forageIdRepository.clearString());
        setDeleteString(forageIdRepository.deleteString());
        setInsertString(forageIdRepository.insertString());
        setQueryString(forageIdRepository.queryString());
        setTableExistsString(forageIdRepository.tableExistsString());

        setProcessorName(config.idempotentRepositoryProcessorName());

        setCreateTableIfNotExists(config.enableIdempotentRepositoryTableCreate());

        if (config.transactionEnabled()) {
            JtaTransactionManager jtaTransactionManager =
                    new JtaTransactionManager(com.arjuna.ats.jta.TransactionManager.transactionManager());
            TransactionTemplate transactionTemplate = new TransactionTemplate(jtaTransactionManager);
            setTransactionTemplate(transactionTemplate);
        }
    }
}
