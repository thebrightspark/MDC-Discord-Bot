package mdcbot;

import org.apache.log4j.Level;

public class Util
{
    private static void log(Level level, String text, Object... args)
    {
        MDCBot.LOG.log(level, String.format(text, args));
    }

    public static void info(String text, Object... args)
    {
        log(Level.INFO, text, args);
    }

    public static void debug(String text, Object... args)
    {
        log(Level.DEBUG, text, args);
    }

    public static void warn(String text, Object... args)
    {
        log(Level.WARN, text, args);
    }

    public static void error(String text, Object... args)
    {
        log(Level.ERROR, text, args);
    }
}
