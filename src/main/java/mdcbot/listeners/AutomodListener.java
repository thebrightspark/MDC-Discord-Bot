package mdcbot.listeners;

import mdcbot.Config;
import mdcbot.EConfigs;
import mdcbot.LogLevel;
import mdcbot.MDCBot;
import mdcbot.utils.Util;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AutomodListener extends ListenerBase
{
    private Map<Long, List<Message>> messageSpamCache = new HashMap<>();

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

        Message message = event.getMessage();
        User author = message.getAuthor();

        //Ignore Admins and Moderators
        if(MDCBot.isMemberBotModerator(event.getGuild().getMember(author)))
            return;

        String content = message.getContent();
        TextChannel channel = event.getChannel();
        String channelName = channel.getName();

        if(checkLinks(channelName, message))
            deleteMessage(message, "Deleted message containing link from member %s (%s) in %s (%s):%s", author.getAsMention(), author.getName(), channel.getAsMention(), channelName, content);

        if(checkFiles(channelName, message))
        {
            StringBuilder sb = new StringBuilder();
            for(Message.Attachment a : message.getAttachments())
                sb.append("\n").append(a.getUrl());
            deleteMessage(message, "Deleted file posted by member %s (%s) in %s (%s):%s", author.getAsMention(), author.getName(), channel.getAsMention(), channelName, sb.toString());
        }

        if(checkSpamCharacters(channelName, content))
            deleteMessage(message, "Deleted message containing spammed characters from member %s (%s) in %s (%s):%s", author.getAsMention(), author.getName(), channel.getAsMention(), channelName, content);

        if(checkSpamMessages(channelName, message))
            for(Message m : messageSpamCache.get(author.getIdLong()))
                deleteMessage(m, "Deleted message due to spam from member %s (%s) in %s (%s):%s", author.getAsMention(), author.getName(), channel.getAsMention(), channelName, m.getContent());

        if(checkBadWords(channelName, content))
            deleteMessage(message, "Deleted message due to bad words from member %s (%s) in %s (%s):%s", author.getAsMention(), author.getName(), channel.getAsMention(), channelName, content);
    }

    private void deleteMessage(Message message, String reason, Object... reasonArgs)
    {
        message.delete().queue();
        Util.logChannel(LogLevel.INFO, reason, reasonArgs);
    }

    private boolean checkLinks(String channelName, Message message)
    {
        //TODO: Check for links
        if(!Config.getContains(EConfigs.CHANNEL_LINK_BLACKLIST, channelName)) return false;
        return false;
    }

    private boolean checkFiles(String channelName, Message message)
    {
        return message.getAttachments().size() > 0 && Config.getContains(EConfigs.CHANNEL_FILE_BLACKLIST, channelName);
    }

    private boolean checkSpamCharacters(String channelName, String content)
    {
        if(!Config.getContains(EConfigs.CHANNEL_SPAM_CHARACTERS_BLACKLIST, channelName)) return false;
        if(content.length() <= 10) return false;

        //Count all the characters in the message
        Map<Character, Integer> characterCount = new HashMap<>();
        for(char c : content.toCharArray())
            characterCount.compute(c, (character, integer) -> integer == null ? 1 : integer + 1);

        //Add up all of the major repeated characters
        int spamCharCount = 0;
        for(int charCount : characterCount.values())
            if(charCount > (int) Math.ceil((float) content.length() * 0.2f))
                spamCharCount += charCount;

        return spamCharCount > (int) Math.ceil((float) content.length() * 0.6f);
    }

    private boolean checkSpamMessages(String channelName, Message message)
    {
        //TODO: Check for spammed messages
        if(!Config.getContains(EConfigs.CHANNEL_SPAM_MESSAGES_BLACKLIST, channelName)) return false;
        return false;
    }

    private boolean checkBadWords(String channelName, String content)
    {
        //TODO: Check for bad words
        //TODO: Debug log the matched bad words
        if(!Config.getContains(EConfigs.CHANNEL_PROFANITY_BLACKLIST, channelName)) return false;
        return false;
    }
}
