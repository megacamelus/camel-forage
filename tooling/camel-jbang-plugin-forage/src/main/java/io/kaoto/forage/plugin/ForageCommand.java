package io.kaoto.forage.plugin;

import org.apache.camel.dsl.jbang.core.commands.CamelCommand;
import org.apache.camel.dsl.jbang.core.commands.CamelJBangMain;
import picocli.CommandLine;

@CommandLine.Command(name = "forage", description = "Forage commands (use --help to see sub commands)")
public class ForageCommand extends CamelCommand {

    public ForageCommand(CamelJBangMain main) {
        super(main);
    }

    @Override
    public Integer doCall() throws Exception {
        //        new CommandLine(this).execute("--help");
        printer().println("Hello from Forage!");
        return 0;
    }
}
