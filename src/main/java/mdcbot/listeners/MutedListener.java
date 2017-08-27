package mdcbot.listeners;

import mdcbot.MDCBot;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class MutedListener extends ListenerAdapter
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if(MDCBot.isMemberMuted(event.getMember()))
            event.getMessage().delete().queue();
    }
}
