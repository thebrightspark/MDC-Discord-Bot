package mdcbot.listeners;

import mdcbot.LogLevel;
import mdcbot.Util;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class ListenerBase extends ListenerAdapter
{
    /**
     * Logs to the console and log channel
     */
    protected void log(LogLevel level, String text, Object... args)
    {
        Util.log(getClass(), level, text, args);
        Util.logChannel(level, text, args);
    }

    protected void info(String text, Object... args)
    {
        log(LogLevel.INFO, text, args);
    }

    protected void error(String text, Object... args)
    {
        log(LogLevel.ERROR, text, args);
    }

    protected void warn(String text, Object... args)
    {
        log(LogLevel.WARN, text, args);
    }

    protected void debug(String text, Object... args)
    {
        log(LogLevel.DEBUG, text, args);
    }
}
