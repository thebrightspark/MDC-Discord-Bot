package mdcbot;

public class Util
{
    public static void log(LogLevel level, String text, Object... args)
    {
        log(level, String.format(text, args));
    }

    public static void log(LogLevel level, String text)
    {
        MDCBot.LOG.log(level.log4jLevel, text);
    }

    public static void info(String text, Object... args)
    {
        log(LogLevel.INFO, text, args);
    }

    public static void error(String text, Object... args)
    {
        log(LogLevel.ERROR, text, args);
    }

    public static void warn(String text, Object... args)
    {
        log(LogLevel.WARN, text, args);
    }

    public static void debug(String text, Object... args)
    {
        log(LogLevel.DEBUG, text, args);
    }

    public static String listAsCommaSeperatedString(Object[] objects)
    {
        StringBuilder sb = new StringBuilder(objects[0].toString());
        for(int i = 1; i < objects.length; i++)
            sb.append(",").append(objects[i].toString());
        return sb.toString();
    }

    public static String[] splitCommandArgs(String args){
        return args.split("\\s");
    }
}
