package io.kaoto.forage.jdbc.common;

import io.kaoto.forage.jdbc.common.idempotent.ForageIdRepository;
import javax.sql.DataSource;

public record ForageDataSource(DataSource dataSource, ForageIdRepository forageIdRepository) {}
