package org.apache.camel.forage.jdbc.common;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.camel.dsl.jbang.core.common.RuntimeType;
import org.apache.camel.forage.core.common.ExportCustomizer;
import org.apache.camel.forage.core.util.config.ConfigStore;

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

        switch (runtime) {
            case quarkus -> {
                listDependencies(
                        dependencies,
                        Arrays.asList("mvn:org.apache.camel.forage:forage-quarkus-jdbc-configurer:"
                                + DataSourceFactoryConfigHelper.geProjectVersion()),
                        "mvn:io.quarkus:quarkus-jdbc-",
                        ":" + DataSourceFactoryConfigHelper.getQuarkusVersion(),
                        runtime);
            }
            case springBoot -> {
                listDependencies(
                        dependencies,
                        Arrays.asList(
                                "mvn:org.apache.camel.forage:forage-jdbc-starter:"
                                        + DataSourceFactoryConfigHelper.geProjectVersion(),
                                "mvn:org.apache.camel.forage:forage-jdbc:"
                                        + DataSourceFactoryConfigHelper.geProjectVersion()),
                        "mvn:org.apache.camel.forage:forage-jdbc-",
                        ":" + DataSourceFactoryConfigHelper.geProjectVersion(),
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
                    ConfigStore.getInstance().readPrefixes(config, DataSourceFactoryConfigHelper.JDBC_PREFIXES_REGEXP);

            if (!prefixes.isEmpty()) {
                for (String name : prefixes) {
                    DataSourceFactoryConfig dsFactoryConfig = new DataSourceFactoryConfig(name);
                    // todoo get quarkus version
                    dependencies.add(depPrefix
                            + DataSourceFactoryConfigHelper.getDbKindNameForRuntime(dsFactoryConfig.dbKind(), runtime)
                            + depVersion);
                }
            } else {
                // todo get quarkus version
                dependencies.add(depPrefix
                        + DataSourceFactoryConfigHelper.getDbKindNameForRuntime(config.dbKind(), runtime)
                        + depVersion);
            }

        } catch (Exception ex) {
            // todo log error
        }
    }
}
