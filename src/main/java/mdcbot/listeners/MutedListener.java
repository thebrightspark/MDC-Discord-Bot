package mdcbot.listeners;

import mdcbot.MDCBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class MutedListener extends ListenerBase
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if(MDCBot.isMemberMuted(event.getMember()))
        {
            event.getMessage().delete().queue();
            info(event, "Deleted message from %s in %s because user is muted:\n%s",
                    event.getAuthor().getAsMention(), event.getChannel().getAsMention(), event.getMessage().getContent());
        }
    }
}
