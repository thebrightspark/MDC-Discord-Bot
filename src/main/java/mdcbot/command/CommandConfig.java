package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;

public class CommandConfig extends CommandBase
{
    public CommandConfig()
    {
        super("config", "Set config values");
    }

    @Override
    protected String getUsage()
    {
        return super.getUsage() + "<configKey> <value>";
    }

    @Override
    protected void doCommand(CommandEvent event)
    {
        String[] mParts = event.getArgs().split("\\s+");
        if(mParts.length < 2)
            fail(event, getUsage());
        String key = mParts[0];
    }
}
