package org.apache.camel.forage.plugin.datasource;

import org.apache.camel.dsl.jbang.core.commands.CamelCommand;
import org.apache.camel.dsl.jbang.core.commands.CamelJBangMain;
import picocli.CommandLine;

@CommandLine.Command(name = "datasource", description = "Camel Forage datasource")
public class DataSourceCommand extends CamelCommand {

    public DataSourceCommand(CamelJBangMain main) {
        super(main);
    }

    @Override
    public Integer doCall() throws Exception {
        new CommandLine(this).execute("--help");
        return 0;
    }
}
