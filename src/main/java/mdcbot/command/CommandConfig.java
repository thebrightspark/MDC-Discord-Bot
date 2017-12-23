package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.Config;
import mdcbot.EConfigs;
import mdcbot.MDCBot;
import mdcbot.utils.Util;
import net.dv8tion.jda.core.EmbedBuilder;

public class CommandConfig extends CommandBase
{
    public CommandConfig()
    {
        super("config", "Set config values", "<configKey> <value>");
        rolePermission = RolePermission.ADMIN;
    }

    @Override
    protected void doCommand(CommandEvent event)
    {
        String[] mParts = (String[]) Util.splitCommandArgs(event.getArgs()).toArray();
        if(mParts.length == 1 && mParts[0].equalsIgnoreCase("list"))
        {
            //List all config keys
            EmbedBuilder message = new EmbedBuilder().setTitle("Current Configs:");
            for(EConfigs eConfig : EConfigs.values())
                if(eConfig.canCommandModify)
                    message.addField(eConfig.toString(), Config.get(eConfig), true);
            reply(event, message.build());
            return;
        }
        if(mParts.length < 2)
        {
            fail(event, getUsageEmbed(event.getGuild()));
            return;
        }
        EConfigs key = EConfigs.getByName(mParts[0]);
        if(key == null)
        {
            fail(event, "Config '" + mParts[0] + "' does not exist");
            return;
        }
        StringBuilder valueBuilder = new StringBuilder(mParts[1]);
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
