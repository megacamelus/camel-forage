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
package org.apache.camel.forage.plugin;

import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import org.apache.camel.dsl.jbang.core.commands.CamelJBangMain;
import org.apache.camel.dsl.jbang.core.commands.Export;
import org.apache.camel.dsl.jbang.core.commands.Run;
import org.apache.camel.dsl.jbang.core.common.CamelJBangPlugin;
import org.apache.camel.dsl.jbang.core.common.Plugin;
import org.apache.camel.dsl.jbang.core.common.PluginExporter;
import org.apache.camel.dsl.jbang.core.common.Printer;
import org.apache.camel.forage.core.common.RuntimeType;
import org.apache.camel.forage.plugin.datasource.DataSourceCommand;
import org.apache.camel.forage.plugin.datasource.DatasourceExportCustomizer;
import org.apache.camel.forage.plugin.datasource.TestDataSourceCommand;
import picocli.CommandLine;

@CamelJBangPlugin(name = "camel-jbang-plugin-forage", firstVersion = "4.15.0")
public class ForagePlugin implements Plugin {

    @Override
    public void customize(CommandLine commandLine, CamelJBangMain main) {
        commandLine.addSubcommand(
                "forage",
                new CommandLine(new ForageCommand(main))
                        .addSubcommand(
                                "datasource",
                                new CommandLine(new DataSourceCommand(main))
                                        .addSubcommand(
                                                "test-connection", new CommandLine(new TestDataSourceCommand(main))))
                        .addSubcommand("export", new Export(main))
                        .addSubcommand("run", new Run(main)));
    }

    /**
     * Exporter is used to add runtime dependencies for both `forage run` and `forage export`
     */
    @Override
    public Optional<org.apache.camel.dsl.jbang.core.common.PluginExporter> getExporter() {
        return Optional.of(new PluginExporter() {
            @Override
            public Set<String> getDependencies(org.apache.camel.dsl.jbang.core.common.RuntimeType runtimeType) {
                return new DatasourceExportCustomizer()
                        .resolveRuntimeDependencies(RuntimeType.valueOf(runtimeType.name()));
            }

            @Override
            public void addSourceFiles(Path buildDir, String packageName, Printer printer) throws Exception {}
        });
    }
}
