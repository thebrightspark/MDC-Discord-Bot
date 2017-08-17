package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.MDCBot;
import mdcbot.Util;

import javax.annotation.Nullable;

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
        debug("Executing command: " + event.getMessage().getContent());
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

    protected void info(String text, Object... args)
    {
        Util.info(text, args);
    }

    protected void debug(String text, Object... args)
    {
        Util.debug(text, args);
    }

    protected void warn(String text, Object... args)
    {
        Util.warn(text, args);
    }

    protected void error(String text, Object... args)
    {
        Util.error(text, args);
    }
}
