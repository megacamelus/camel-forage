package io.kaoto.forage.jdbc.common;

import javax.sql.DataSource;
import io.kaoto.forage.jdbc.common.idempotent.ForageIdRepository;

public record ForageDataSource(DataSource dataSource, ForageIdRepository forageIdRepository) {}
