package mdcbot.listeners;

import mdcbot.LogLevel;
import mdcbot.MDCBot;
import mdcbot.utils.Util;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class MutedListener extends ListenerBase
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if(MDCBot.isMemberMuted(event.getMember()))
        {
            event.getMessage().delete().queue();
            info("Deleted message from %s in %s because user is muted:\n%s",
                    event.getAuthor().getAsMention(), event.getChannel().getAsMention(), event.getMessage().getContent());
            Util.logChannel(LogLevel.INFO, "Deleted message from %s in %s because user is muted:\n%s",
                    event.getAuthor().getAsMention(), event.getChannel().getAsMention(), event.getMessage().getContent());
        }
    }
}
