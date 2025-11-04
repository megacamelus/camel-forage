package org.apache.camel.forage.plugin.datasource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import org.apache.camel.forage.core.common.ExportCustomizer;
import org.apache.camel.forage.core.common.RuntimeType;
import org.apache.camel.forage.core.util.config.ConfigStore;
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
                        DataSourceExportHelper.getQuarkusDependencies(),
                        "mvn:io.quarkus:quarkus-jdbc-",
                        ":" + DataSourceExportHelper.getQuarkusVersion(),
                        runtime);
            }
            case springBoot -> {
                listDependencies(
                        dependencies,
                        DataSourceExportHelper.getQSpringBootDependencies(),
                        "mvn:org.apache.camel.forage:forage-jdbc-",
                        ":" + DataSourceExportHelper.getProjectVersion(),
                        runtime);
            }
            case main -> {
                listDependencies(
                        dependencies,
                        DataSourceExportHelper.getPlainDependencies(),
                        "mvn:org.apache.camel.forage:forage-jdbc-",
                        ":" + DataSourceExportHelper.getProjectVersion(),
                        runtime);
            }
        }

        return dependencies;
    }

    private static void listDependencies(
            Set<String> dependencies,
            String basicDependencies,
            String depPrefix,
            String depVersion,
            RuntimeType runtime) {
        dependencies.addAll(Arrays.asList(basicDependencies.split(",")));

        try {
            DataSourceFactoryConfig config = new DataSourceFactoryConfig();
            Set<String> prefixes =
                    ConfigStore.getInstance().readPrefixes(config, DataSourceExportHelper.JDBC_PREFIXES_REGEXP);

            if (!prefixes.isEmpty()) {
                for (String name : prefixes) {
                    DataSourceFactoryConfig dsFactoryConfig = new DataSourceFactoryConfig(name);
                    // todoo get quarkus version
                    dependencies.add(depPrefix + dsFactoryConfig.dbKind() + depVersion);
                }
            } else {
                dependencies.add(depPrefix + config.dbKind() + depVersion);
            }

            if (config.transactionEnabled()) {
                dependencies.add("mvn:io.quarkus:quarkus-narayana-jta:" + DataSourceExportHelper.getQuarkusVersion());
            }
        } catch (Exception ex) {
            // todo log error
        }
    }
}
