package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.LogLevel;
import mdcbot.MDCBot;
import mdcbot.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;

import javax.annotation.Nullable;
import java.time.Instant;

public abstract class CommandBase extends Command
{
    public CommandBase(String name, String help)
    {
        this.name = name;
        this.help = help;
    }

    /**
     * Returns a String built with:
     * "[prefix][commandName] "
     */
    protected String getCommand()
    {
        return MDCBot.PREFIX + getName() + " ";
    }

    protected String getUsage()
    {
        return getCommand();
    }

    @Override
    protected void execute(CommandEvent event)
    {
        debug(event, "Executing command: " + event.getMessage().getContent());
        doCommand(event);
    }

    protected abstract void doCommand(CommandEvent event);

    /**
     * Call this method when a command fails at any point
     */
    protected void fail(CommandEvent event, @Nullable String message)
    {
        if(message != null)
            event.reply(message);
        if(event.getClient().getListener()!=null)
            event.getClient().getListener().onTerminatedCommand(event, this);
    }

    /**
     * Logs to the console and log channel
     */
    private void log(CommandEvent event, LogLevel level, String text, Object... args)
    {
        String logText = String.format(text, args);
        Util.log(level, text);
        if(MDCBot.logChannel != null)
        {
            EmbedBuilder message = new EmbedBuilder();
            message.setColor(level.colour);
            User author = event.getAuthor();
            message.setAuthor(author.getName(), null, author.getEffectiveAvatarUrl());
            message.setTitle(level.toString());
            message.setDescription(logText);
            message.setTimestamp(Instant.now());
            MDCBot.logChannel.sendMessage(message.build()).queue();
        }
    }

    protected void info(CommandEvent event, String text, Object... args)
    {
        log(event, LogLevel.INFO, text, args);
    }

    protected void error(CommandEvent event, String text, Object... args)
    {
        log(event, LogLevel.ERROR, text, args);
    }

    protected void warn(CommandEvent event, String text, Object... args)
    {
        log(event, LogLevel.WARN, text, args);
    }

    protected void debug(CommandEvent event, String text, Object... args)
    {
        log(event, LogLevel.DEBUG, text, args);
    }
}
