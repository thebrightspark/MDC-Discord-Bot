package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.MDCBot;
import mdcbot.utils.Util;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.MessageEmbed;

public class CommandUsage extends CommandBase
{
    public CommandUsage()
    {
        super("usage", "Shows usage for commands", "<commandName>");
    }

    @Override
    protected void doCommand(CommandEvent event)
    {
        Guild guild = event.getGuild();
        String args = event.getArgs();
        if(args.isEmpty())
            fail(event, getUsageEmbed(guild));

        MessageEmbed usage = MDCBot.getCommandUsage(guild, args);
        reply(event, usage != null ? usage : Util.createBotMessage(guild, "Command has no usage description"));
    }
}
