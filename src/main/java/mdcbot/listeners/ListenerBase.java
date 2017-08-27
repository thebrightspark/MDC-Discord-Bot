package mdcbot.listeners;

import mdcbot.LogLevel;
import mdcbot.MDCBot;
import mdcbot.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.GenericMessageEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.time.Instant;

public class ListenerBase extends ListenerAdapter
{
    /**
     * Logs to the console and log channel
     */
    private void log(GenericMessageEvent event, LogLevel level, String text, Object... args)
    {
        String logText = String.format(text, args);
        Util.log(level, text, args);
        if(MDCBot.logChannel != null)
        {
            EmbedBuilder message = new EmbedBuilder();
            message.setColor(level.colour);
            User botUser = event.getJDA().getSelfUser();
            message.setAuthor(botUser.getName(), null, botUser.getEffectiveAvatarUrl());
            message.setTitle(level.toString());
            message.setDescription(logText);
            message.setTimestamp(Instant.now());
            MDCBot.logChannel.sendMessage(message.build()).queue();
        }
    }

    protected void info(GenericMessageEvent event, String text, Object... args)
    {
        log(event, LogLevel.INFO, text, args);
    }

    protected void error(GenericMessageEvent event, String text, Object... args)
    {
        log(event, LogLevel.ERROR, text, args);
    }

    protected void warn(GenericMessageEvent event, String text, Object... args)
    {
        log(event, LogLevel.WARN, text, args);
    }

    protected void debug(GenericMessageEvent event, String text, Object... args)
    {
        log(event, LogLevel.DEBUG, text, args);
    }
}
