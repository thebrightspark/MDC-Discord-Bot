package mdcbot.utils;

import mdcbot.Config;
import mdcbot.EConfigs;
import mdcbot.LogLevel;
import mdcbot.MDCBot;
import mdcbot.command.CommandBase;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;
import org.apache.log4j.Logger;

import javax.annotation.Nullable;
import java.awt.Color;
import java.time.Instant;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Util
{
    private static Map<Class, Logger> loggers = new HashMap<>();

    public static boolean DEBUG = false;

    private static Logger getLoggerFromMap(Class clazz)
    {
        loggers.computeIfAbsent(clazz, Util::getLogger);
        return loggers.get(clazz);
    }

    public static Logger getLogger(Class clazz)
    {
        return Logger.getLogger(clazz.getSimpleName());
    }

    /**
     * Logs to the console
     */
    public static void log(Class clazz, LogLevel level, String text, Object... args)
    {
       getLoggerFromMap(clazz).log(level.log4jLevel, String.format(text, args));
    }

    public static void info(Class clazz, String text, Object... args)
    {
        log(clazz, LogLevel.INFO, text, args);
    }

    public static void error(Class clazz, String text, Object... args)
    {
        log(clazz, LogLevel.ERROR, text, args);
    }

    public static void warn(Class clazz, String text, Object... args)
    {
        log(clazz, LogLevel.WARN, text, args);
    }

    public static void debug(Class clazz, String text, Object... args)
    {
        log(clazz, LogLevel.DEBUG, text, args);
    }

    public static void trace(Class clazz, String text, Object... args)
    {
        log(clazz, LogLevel.TRACE, text, args);
    }

    /**
     * Logs to the assigned log channel if it has been set
     */
    public static void logChannel(LogLevel level, User author, String text, Object... args)
    {
        if(MDCBot.logChannel != null && level.isHigherThanOrEqualTo(Config.get(EConfigs.LOG_LEVEL)))
        {
            if(author == null)
                author = MDCBot.jda.getSelfUser();
            EmbedBuilder message = new EmbedBuilder();
            message.setColor(level.colour);
            message.setAuthor(getFullUser(author), null, author.getEffectiveAvatarUrl());
            message.setTitle(level.toString());
            message.setDescription(String.format(text, args));
            message.setTimestamp(Instant.now());
            MDCBot.logChannel.sendMessage(message.build()).queue();
        }
    }

    public static void logChannel(LogLevel level, String text, Object... args)
    {
        logChannel(level, null, text, args);
    }

    public static Color getBotColour(Guild guild)
    {
        return guild == null ? Color.BLUE : guild.getMember(MDCBot.jda.getSelfUser()).getColor();
    }

    /**
     * Creates an embedded message for the bot to send (uses the bot's main role colour)
     */
    public static MessageEmbed createBotMessage(Guild guild, String message, Object... args)
    {
        return createEmbedMessage(getBotColour(guild), String.format(message, args), null);
    }

    /**
     * Creates an embedded message for the bot to send (uses the bot's main role colour)
     */
    public static MessageEmbed createBotMessage(Guild guild, String title, String description, Object... args)
    {
        return createEmbedMessage(getBotColour(guild), title, String.format(description, args));
    }

    /**
     * Creates an embedded message using the member's main role colour
     */
    public static MessageEmbed createMemberMessage(Member member, String message, Object... args)
    {
        return createEmbedMessage(member.getColor(), String.format(message, args), null);
    }

    public static MessageEmbed createUsageMessage(Guild guild, CommandBase command)
    {
        return createEmbedMessage(getBotColour(guild), command.getName() + " command usage:", command.getUsage());
    }

    /**
     * Creates a simple embedded message
     */
    public static MessageEmbed createEmbedMessage(Color colour, @Nullable String title, @Nullable String description)
    {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(colour);
        if(title != null)
            builder.setTitle(title);
        if(description != null)
            builder.setDescription(description);
        return builder.build();
    }

    public static String listAsCommaSeperatedString(Object[] objects)
    {
        StringBuilder sb = new StringBuilder(objects[0].toString());
        for(int i = 1; i < objects.length; i++)
            sb.append(",").append(objects[i].toString());
        return sb.toString();
    }

    public static List<String> splitCommandArgs(String args){
        List<String> ret = new ArrayList<>();
        Matcher mat = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(args);
        while(mat.find()){
            ret.add(mat.group(1));
        }
        return ret;
    }

    public static String getFullUser(User user)
    {
        return user.getName() + "#" + user.getDiscriminator();
    }
}
