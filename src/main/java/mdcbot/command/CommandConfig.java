package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.Config;
import mdcbot.EConfigs;
import mdcbot.MDCBot;
import mdcbot.Util;

public class CommandConfig extends CommandBase
{
    public CommandConfig()
    {
        super("config", "Set config values");
        rolePermission = RolePermission.ADMIN;
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
        info(event, Util.listAsCommaSeperatedString(mParts));
        if(mParts.length < 2)
        {
            fail(event, getUsage());
            return;
        }
        EConfigs key = EConfigs.getByName(mParts[0]);
        if(key == null)
        {
            fail(event, "Config '" + mParts[0] + "' does not exist");
            return;
        }
        StringBuilder valueBuilder = new StringBuilder(mParts[1]);
        if(mParts.length >= 2)
            for(int i = 2; i < mParts.length; i++)
                valueBuilder.append(" ").append(mParts[i]);
        String value = valueBuilder.toString();

        if(Config.has(key))
        {
            if(key.canCommandModify)
            {
                //If enabling/disabling command, check it exists first
                if(key == EConfigs.DISABLED_COMMANDS && !MDCBot.doesCommandExist(value))
                {
                    fail(event, "Command '" + value + "' does not exist");
                    return;
                }
                String oldValue = Config.set(key, value);
                String log = "Set config '%s' to '%s' (Was '%s')";
                if(key.needsRestart)
                    log += "\nNOTE: Config will only take effect after bot restart.";
                info(event, log, key, Config.get(key), oldValue);
            }
            else
                info(event, "Config '%s' can't be modified using commands", key);
        }
        else
            info(event, "Config '%s' doesn't exist", key);
    }
}
