package io.kaoto.forage.plugin.datasource;

import io.kaoto.forage.core.common.RuntimeType;
import io.kaoto.forage.jdbc.common.DataSourceFactoryConfig;
import io.kaoto.forage.jdbc.common.DataSourceFactoryConfigEntries;
import io.kaoto.forage.plugin.AbstractExportCustomizer;
import io.kaoto.forage.plugin.ExportHelper;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Implementation of export customizer for datasource properties.
 *
 * <p>
 * Adds quarkus or spring-boot runtime dependencies, thus making export command less verbose.
 * </p>
 */
public class DatasourceExportCustomizer extends AbstractExportCustomizer<DataSourceFactoryConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(DatasourceExportCustomizer.class);

    @Override
    protected final DataSourceFactoryConfig getConfig(String prefix) {
        return new DataSourceFactoryConfig(prefix);
    }

    @Override
    protected final String getPrefix() {
        return "jdbc";
    }

    @Override
    protected final Set<String> getDependencies(RuntimeType runtime) {

        // read all values of jmsKind
        Set<String> dbKinds = readAllValuesFromProperty(DataSourceFactoryConfigEntries.DB_KIND);

        Set<String> dependencies = new LinkedHashSet<>(
                Arrays.asList(ExportHelper.getDependencies(runtime, ExportHelper.ResourceType.datasource)
                        .split(",")));
        dependencies.addAll(dbKinds.stream()
                .map(dbKind -> ExportHelper.getString(runtime.name() + ".dbKind", ExportHelper.ResourceType.datasource)
                        .replaceAll("\\$\\{dbKind}", dbKind))
                .toList());

        // quarkus runtime in case of transacted, need narayana dependency
        if (runtime == RuntimeType.quarkus
                && !readAllValuesFromProperty(DataSourceFactoryConfigEntries.TRANSACTION_ENABLED)
                        .isEmpty()) {
            dependencies.add(ExportHelper.getString("quarkus.narayana-jta", ExportHelper.ResourceType.datasource));
        }

        return dependencies;
    }
}
