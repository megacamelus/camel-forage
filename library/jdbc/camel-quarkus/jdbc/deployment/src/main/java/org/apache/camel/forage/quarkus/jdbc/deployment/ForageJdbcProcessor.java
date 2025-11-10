/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.camel.forage.quarkus.jdbc.deployment;

import io.quarkus.deployment.annotations.BuildProducer;
import io.quarkus.deployment.annotations.BuildStep;
import io.quarkus.deployment.annotations.ExecutionTime;
import io.quarkus.deployment.annotations.Record;
import io.quarkus.deployment.builditem.FeatureBuildItem;
import io.quarkus.runtime.RuntimeValue;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.camel.forage.core.annotations.ForageFactory;
import org.apache.camel.forage.core.util.config.ConfigHelper;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.jdbc.common.DataSourceFactoryConfig;
import org.apache.camel.forage.quarkus.jdbc.ForageJdbcRecorder;
import org.apache.camel.processor.aggregate.jdbc.JdbcAggregationRepository;
import org.apache.camel.processor.idempotent.jdbc.JdbcMessageIdRepository;
import org.apache.camel.quarkus.core.deployment.spi.CamelContextBuildItem;
import org.apache.camel.quarkus.core.deployment.spi.CamelRuntimeBeanBuildItem;
import org.jboss.logging.Logger;
import org.junit.platform.commons.util.StringUtils;

@ForageFactory(
        value = "CamelQuarkusDataSourceConfigSource",
        components = {"camel-sql", "camel-jdbc"},
        description = "Default Camel Quarkus DataSource config source",
        factoryType = "DataSource",
        autowired = true)
class ForageJdbcProcessor {

    private static final Logger LOG = Logger.getLogger(ForageJdbcProcessor.class);
    private static final String FEATURE = "camel-forage-jdbc";

    @BuildStep
    FeatureBuildItem feature() {
        return new FeatureBuildItem(FEATURE);
    }

    @BuildStep
    @Record(value = ExecutionTime.STATIC_INIT)
    void registerAggregation(
            CamelContextBuildItem context, ForageJdbcRecorder recorder, BuildProducer<CamelRuntimeBeanBuildItem> beans)
            throws Exception {

        DataSourceFactoryConfig config = new DataSourceFactoryConfig();
        Set<String> prefixes = ConfigStore.getInstance().readPrefixes(config, "(.+).jdbc\\..*");

        Map<String, DataSourceFactoryConfig> configs = prefixes.isEmpty()
                ? Collections.singletonMap("dataSource", new DataSourceFactoryConfig())
                : prefixes.stream().collect(Collectors.toMap(n -> n, n -> new DataSourceFactoryConfig(n)));

        for (Map.Entry<String, DataSourceFactoryConfig> entry : configs.entrySet()) {
            if (StringUtils.isNotBlank(entry.getValue().aggregationRepositoryName())) {
                // create aggregation repository
                RuntimeValue<JdbcAggregationRepository> aggRepo = recorder.createAggregationRepository(
                        entry.getKey(), context.getCamelContext(), entry.getValue());
                if (aggRepo != null) {
                    beans.produce(new CamelRuntimeBeanBuildItem(
                            entry.getValue().aggregationRepositoryName(),
                            JdbcAggregationRepository.class.getName(),
                            aggRepo));
                }
            } else {
                // if aggregation name is blank,but there is another aggregation property, show warning
                logMissingMandatoryProperty(
                        "aggregation",
                        entry,
                        "Aggregation name has to be provided in order to create aggregation repositories (`%s` is already provided)");
            }

            if (entry.getValue().enableIdempotentRepository()) {
                if (StringUtils.isNotBlank(entry.getValue().idempotentRepositoryTableName())) {
                    // create idempotent repository
                    RuntimeValue<JdbcMessageIdRepository> idRepo = recorder.createIdempotentRepository(
                            entry.getKey(), context.getCamelContext(), entry.getValue());
                    if (idRepo != null) {
                        beans.produce(new CamelRuntimeBeanBuildItem(
                                entry.getValue().idempotentRepositoryTableName(),
                                JdbcMessageIdRepository.class.getName(),
                                idRepo));
                    }
                } else {
                    // if idempotent table name is blank,but there is another idempotent property, show warning
                    logMissingMandatoryProperty(
                            "idempotent",
                            entry,
                            "Idempotent repository table name has to be provided in order to create idempotent repository (`%s` is already provided)");
                }
            }
        }
    }

    private static void logMissingMandatoryProperty(
            String aggregation, Map.Entry<String, DataSourceFactoryConfig> entry, String warnMsg) {
        ConfigHelper.getGetterMethods(DataSourceFactoryConfig.class).stream()
                .filter(m -> m.getName().toLowerCase().contains(aggregation))
                .forEach(m -> {
                    try {
                        Object value = m.invoke(entry.getValue());
                        if (value != null && StringUtils.isNotBlank(value.toString())) {
                            LOG.warn(warnMsg.formatted(m.getName()));
                        }
                    } catch (Exception e) {
                        // ignore any error
                    }
                });
    }
}
