package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.utils.Util;

public class CommandDebug extends CommandBase{
    public CommandDebug() {
        super("debug", "For developers only. Unlocks all the debug logging for various parts of the bot.");
    }

    @Override
    protected void doCommand(CommandEvent event) {
        Util.DEBUG = !Util.DEBUG;
    }
}
