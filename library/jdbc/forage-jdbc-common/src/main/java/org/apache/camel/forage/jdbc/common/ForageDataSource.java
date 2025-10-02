package org.apache.camel.forage.jdbc.common;

import javax.sql.DataSource;
import org.apache.camel.forage.jdbc.common.idempotent.ForageIdRepository;

public record ForageDataSource(DataSource dataSource, ForageIdRepository forageIdRepository) {}
