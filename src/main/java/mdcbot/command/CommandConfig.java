package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.Config;

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
        if(mParts.length <= 2)
        {
            fail(event, getUsage());
            return;
        }
        String key = mParts[0];
        StringBuilder valueBuilder = new StringBuilder(mParts[1]);
        if(mParts.length > 2)
            for(int i = 2; i < mParts.length; i++)
                valueBuilder.append(" ").append(mParts[i]);
        String value = valueBuilder.toString();

        if(Config.has(key))
        {
            if(Config.canModify(key))
            {
                String oldValue = Config.set(key, value);
                String log = "Set config '%s' to '%s' (Was '%s')";
                if(Config.needsRestart(key))
                    log += "\nNOTE: Config will only take effect after bot restart.";
                info(event, log, key, value, oldValue);
            }
            else
                info(event, "Config '%s' can't be modified using commands");
        }
        else
            info(event, "Config '%s' doesn't exist", key);
    }
}
