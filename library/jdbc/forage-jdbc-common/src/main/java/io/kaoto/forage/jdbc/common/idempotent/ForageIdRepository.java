package io.kaoto.forage.jdbc.common.idempotent;

public interface ForageIdRepository {

    default String tableExistsString() {
        return ForageJdbcMessageIdRepository.DEFAULT_TABLE_EXISTS_STRING;
    }

    default String createString() {
        return ForageJdbcMessageIdRepository.DEFAULT_CREATE_STRING;
    }

    default String queryString() {
        return ForageJdbcMessageIdRepository.DEFAULT_QUERY_STRING;
    }

    default String insertString() {
        return ForageJdbcMessageIdRepository.DEFAULT_INSERT_STRING;
    }

    default String deleteString() {
        return ForageJdbcMessageIdRepository.DEFAULT_DELETE_STRING;
    }

    default String clearString() {
        return ForageJdbcMessageIdRepository.DEFAULT_CLEAR_STRING;
    }
}
