package io.kaoto.forage.plugin.jms;

import io.kaoto.forage.core.common.RuntimeType;
import io.kaoto.forage.jms.common.ConnectionFactoryConfig;
import io.kaoto.forage.jms.common.ConnectionFactoryConfigEntries;
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
public class JmsExportCustomizer extends AbstractExportCustomizer<ConnectionFactoryConfig> {
    private static final Logger LOG = LoggerFactory.getLogger(JmsExportCustomizer.class);

    @Override
    protected final ConnectionFactoryConfig getConfig(String prefix) {
        return new ConnectionFactoryConfig(prefix);
    }

    @Override
    protected final String getPrefix() {
        return "jms";
    }

    @Override
    public final Set<String> getDependencies(RuntimeType runtime) {
        // read all values of jmsKind
        Set<String> jmsKinds = readAllValuesFromProperty(ConnectionFactoryConfigEntries.JMS_KIND);

        Set<String> dependencies =
                new LinkedHashSet<>(Arrays.asList(ExportHelper.getDependencies(runtime, ExportHelper.ResourceType.jms)
                        .split(",")));

        dependencies.addAll(jmsKinds.stream()
                .map(jmsKind -> {
                    // get ${runtime}.${jmsKind} value at first
                    String deps = ExportHelper.getString(runtime.name() + "." + jmsKind, ExportHelper.ResourceType.jms);
                    // if value is null, try to read ${runtime}.jmsKind and replace "${jmsKind}" withthe real value
                    if (deps == null || deps.isEmpty()) {
                        deps = ExportHelper.getString(runtime.name() + ".jmsKind", ExportHelper.ResourceType.jms)
                                .replaceAll("\\$\\{jmsKind}", jmsKind);
                    }
                    return deps;
                })
                .toList());

        if (runtime == RuntimeType.quarkus) {
            // in case of pooled quarkus.pooled-jms is required
            if (!readAllValuesFromProperty(ConnectionFactoryConfigEntries.POOL_ENABLED)
                    .isEmpty()) {
                dependencies.add(ExportHelper.getString("quarkus.pooled-jms", ExportHelper.ResourceType.jms));
            }
            // in case of transacted, both quarkus.pooled-jms and quarkus.narayana-jta are required
            if (!readAllValuesFromProperty(ConnectionFactoryConfigEntries.TRANSACTION_ENABLED)
                    .isEmpty()) {
                dependencies.add(ExportHelper.getString("quarkus.pooled-jms", ExportHelper.ResourceType.jms));
                dependencies.add(ExportHelper.getString("quarkus.narayana-jta", ExportHelper.ResourceType.jms));
            }
        }

        LOG.debug("Exported Dependencies:");
        dependencies.forEach(LOG::debug);

        return dependencies;
    }
}
