package mdcbot;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.time.Instant;

public class Util
{
    /**
     * Logs to the console
     */
    public static void log(LogLevel level, String text, Object... args)
    {
        MDCBot.LOG.log(level.log4jLevel, String.format(text, args));
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

    /**
     * Logs to the assigned log channel if it has been set
     */
    public static void logChannel(LogLevel level, User author, String text, Object... args)
    {
        if(MDCBot.logChannel != null)
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

    /**
     * Creates an embedded message for the bot to send (uses the bot's main role colour)
     */
    public static MessageEmbed createBotMessage(Guild guild, String message, Object... args)
    {
        return createEmbedMessage(guild.getMember(MDCBot.jda.getSelfUser()).getColor(), message, args);
    }

    /**
     * Creates an embedded message using the member's main role colour
     */
    public static MessageEmbed createMemberMessage(Member member, String message, Object... args)
    {
        return createEmbedMessage(member.getColor(), message, args);
    }

    /**
     * Creates a simple embedded message
     */
    private static MessageEmbed createEmbedMessage(Color colour, String message, Object... args)
    {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(colour);
        builder.setTitle(String.format(message, args));
        return builder.build();
    }

    public static String listAsCommaSeperatedString(Object[] objects)
    {
        StringBuilder sb = new StringBuilder(objects[0].toString());
        for(int i = 1; i < objects.length; i++)
            sb.append(",").append(objects[i].toString());
        return sb.toString();
    }

    public static String[] splitCommandArgs(String args){
        return args.split("\\s+");
    }

    public static String getFullUser(User user)
    {
        return user.getName() + "#" + user.getDiscriminator();
    }
}
