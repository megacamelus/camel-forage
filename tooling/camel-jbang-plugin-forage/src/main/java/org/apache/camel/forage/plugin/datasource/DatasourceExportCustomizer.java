package org.apache.camel.forage.plugin.datasource;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.camel.forage.core.common.ExportCustomizer;
import org.apache.camel.forage.core.common.RuntimeType;
import org.apache.camel.forage.core.util.config.ConfigStore;
import org.apache.camel.forage.jdbc.common.DataSourceCommonExportHelper;
import org.apache.camel.forage.jdbc.common.DataSourceFactoryConfig;
import org.apache.camel.forage.plugin.DataSourceExportHelper;

/**
 * Implementation of export customizer for datasource properties.
 *
 * <p>
 * Adds quarkus or spring-boot runtime dependencies, thus making export command less verbose.
 * </p>
 */
public class DatasourceExportCustomizer implements ExportCustomizer {

    @Override
    public Set<String> resolveRuntimeDependencies(RuntimeType runtime) {
        Set<String> dependencies = new HashSet<>();

        RuntimeType _runtime = runtime == null ? RuntimeType.main : runtime;

        switch (_runtime) {
            case quarkus -> {
                listDependencies(
                        dependencies,
                        Arrays.asList("mvn:org.apache.camel.forage:forage-quarkus-jdbc-configurer:"
                                + DataSourceExportHelper.geProjectVersion()),
                        "mvn:io.quarkus:quarkus-jdbc-",
                        ":" + DataSourceExportHelper.getQuarkusVersion(),
                        runtime);
            }
            case springBoot -> {
                listDependencies(
                        dependencies,
                        Arrays.asList(
                                "mvn:org.apache.camel.forage:forage-jdbc-starter:"
                                        + DataSourceExportHelper.geProjectVersion(),
                                "mvn:org.apache.camel.forage:forage-jdbc:" + DataSourceExportHelper.geProjectVersion()),
                        "mvn:org.apache.camel.forage:forage-jdbc-",
                        ":" + DataSourceExportHelper.geProjectVersion(),
                        runtime);
            }
            case main -> {
                listDependencies(
                        dependencies,
                        Collections.singletonList(
                                "mvn:org.apache.camel.forage:forage-jdbc:" + DataSourceExportHelper.geProjectVersion()),
                        "mvn:org.apache.camel.forage:forage-jdbc-",
                        ":" + DataSourceExportHelper.geProjectVersion(),
                        runtime);
            }
        }

        return dependencies;
    }

    private static void listDependencies(
            Set<String> dependencies,
            List<String> newDependencies,
            String depPrefix,
            String depVersion,
            RuntimeType runtime) {
        dependencies.addAll(newDependencies);

        try {
            DataSourceFactoryConfig config = new DataSourceFactoryConfig();
            Set<String> prefixes =
                    ConfigStore.getInstance().readPrefixes(config, DataSourceExportHelper.JDBC_PREFIXES_REGEXP);

            if (!prefixes.isEmpty()) {
                for (String name : prefixes) {
                    DataSourceFactoryConfig dsFactoryConfig = new DataSourceFactoryConfig(name);
                    dependencies.add(depPrefix
                            + DataSourceCommonExportHelper.getDbKindNameForRuntime(dsFactoryConfig.dbKind(), runtime)
                            + depVersion);
                }
            } else {
                dependencies.add(depPrefix
                        + DataSourceCommonExportHelper.getDbKindNameForRuntime(config.dbKind(), runtime)
                        + depVersion);
            }

            if(config.transactionEnabled()) {
                dependencies.add("mvn:io.quarkus:quarkus-narayana-jta:" + DataSourceExportHelper.getQuarkusVersion());
            }
        } catch (Exception ex) {
            // todo log error
        }
    }
}
