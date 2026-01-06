package io.kaoto.forage.plugin.config;

import org.apache.camel.dsl.jbang.core.commands.CamelCommand;
import org.apache.camel.dsl.jbang.core.commands.CamelJBangMain;
import picocli.CommandLine;

@CommandLine.Command(name = "config", description = "Forage configuration management commands")
public class ConfigCommand extends CamelCommand {

    public ConfigCommand(CamelJBangMain main) {
        super(main);
    }

    @Override
    public Integer doCall() throws Exception {
        new CommandLine(this).execute("--help");
        return 0;
    }
}
