package org.apache.camel.forage.plugin;

import org.apache.camel.dsl.jbang.core.commands.CamelJBangMain;
import org.apache.camel.dsl.jbang.core.commands.Run;
import org.apache.camel.forage.core.common.RuntimeType;
import org.apache.camel.forage.plugin.datasource.DatasourceExportCustomizer;

public class ForageRun extends Run {
    public ForageRun(CamelJBangMain main) {
        super(main);
    }

    /**
     * This method is used only for the camel run command with runtime=main
     * All other runtimes extends dependencies by interface {@link org.apache.camel.dsl.jbang.core.common.PluginExporter)
     * from {@link org.apache.camel.forage.plugin.ForagePlugin}
     */
    @Override
    protected void addDependencies(String... deps) {
        super.addDependencies(new DatasourceExportCustomizer()
                .resolveRuntimeDependencies(RuntimeType.main)
                .toArray(new String[0]));
    }
}
