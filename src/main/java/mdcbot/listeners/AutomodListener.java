package mdcbot.listeners;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

public class AutomodListener extends ListenerBase
{
    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        //TODO: Automod!
        /*
        Want the following:
            * Configurable auto deleting of links and file uploads
            * Whitelist/Blacklist for links and bad words
            * Regex on bad words (print to the log what words were matched in the message
            * Configurable muting when someone's done bad enough things:
                * Posted too many bad words within X time (default 5mins?)
                * Posted too many messages (configurable) within X time (default 5 messages in 2s?)
                * Posted too many duplicate characters in a message (default >60% similar characters in a message?)
         */
    }
}
