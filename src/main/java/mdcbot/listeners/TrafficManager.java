package mdcbot.listeners;

import mdcbot.DatedUser;
import mdcbot.MDCBot;
import mdcbot.Util;
import mdcbot.io.FileManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;

/**
 * This class records the amount of messages sent in all public channels and the amount of unique users who sent the
 * messages within the last 24 hours.
 *
 * You can get a "traffic rating" using the traffic command
 * {@link mdcbot.command.CommandTraffic}
 *
 * TODO: Periodically dump all of the data into a file in a new thread
 */
public class TrafficManager extends ListenerAdapter
{
    private static List<DatedUser> users = new ArrayList<>();
    private static List<Date> messages = new ArrayList<>();
    private static float maxRatio = -1f;
    private static float lastRatio = -1f;
    private static FileManager fm_users = new FileManager(MDCBot.SAVES_DIR,  MDCBot.TRAFFIC_USERS_FILE);
    private static FileManager fm_messages = new FileManager(MDCBot.SAVES_DIR,  MDCBot.TRAFFIC_MESSAGES_FILE);

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if(event.getChannelType() != ChannelType.TEXT || !(event.getChannel() instanceof TextChannel) || event.getAuthor().isBot())
            return;

        TextChannel channel = (TextChannel) event.getChannel();
        //Make sure the channel isn't restricted to certain roles (i.e. @everyone can read and write on this channel)
        for(PermissionOverride perm : channel.getRolePermissionOverrides())
        {
            if(perm.isRoleOverride() && perm.getRole().isPublicRole())
            {
                List<Permission> deniedPerms = perm.getDenied();
                if(deniedPerms.contains(Permission.MESSAGE_READ) || deniedPerms.contains(Permission.MESSAGE_WRITE))
                    return;
            }
        }

        //Record this message
        Date messageTimestamp = new Date(event.getMessage().getCreationTime().toEpochSecond() * 1000);
        Util.info("Adding message with date: %s", messageTimestamp);
        if(!hasUser(event.getAuthor()))
            users.add(new DatedUser(event.getAuthor(), messageTimestamp));
        messages.add(messageTimestamp);

        //Make sure the list is kept sorted
        messages.sort(Comparator.naturalOrder());

        checkData();
    }

    private boolean hasUser(User user)
    {
        for(DatedUser datedUser : users)
            if(datedUser.getUser().getIdLong() == user.getIdLong())
                return true;
        return false;
    }

    private static void checkData()
    {
        //Get a Date that is 24 hours before now
        Instant minInstant = Instant.now().minus(24, ChronoUnit.HOURS);
        Date minDate = new Date(minInstant.toEpochMilli());

        //Remove any users that are older than 24 hours
        Iterator<DatedUser> usersIterator = users.iterator();
        while(usersIterator.hasNext())
            if(usersIterator.next().getDate().before(minDate))
                usersIterator.remove();

        //Remove any messages that are older than 24 hours
        Iterator<Date> messagesIterator = messages.iterator();
        while(messagesIterator.hasNext())
        {
            Date date = messagesIterator.next();
            if(date.before(minDate))
                messagesIterator.remove();
            else
                break;
        }

        //Refresh max ratio
        getRatio();
    }

    private static void saveToFiles()
    {
        //Save users
        StringBuilder sb = new StringBuilder();
        for(DatedUser datedUser : users)
            sb.append(datedUser).append("\n");
        fm_users.writeToFile(sb.toString());

        //Save messages
        sb = new StringBuilder();
        for(Date date : messages)
            sb.append(date.getTime()).append("\n");
        fm_messages.writeToFile(sb.toString());
    }

    private static void readFromFiles()
    {
        //Read users
        users.clear();
        for(String line : fm_users.readFromFile())
            users.add(new DatedUser(line));

        //Read messages
        messages.clear();
        for(String line : fm_messages.readFromFile())
        {
            try
            {
                messages.add(new Date(Long.parseLong(line)));
            }
            catch(NumberFormatException e)
            {
                Util.error("Couldn't parse to Date: " + line);
                e.printStackTrace();
            }
        }
    }

    private static float getRatio()
    {
        float ratio = lastRatio = (float) messages.size() / (float) users.size();
        if(ratio > maxRatio)
            maxRatio = ratio;
        return ratio;
    }

    public static int getNumUsers()
    {
        return users.size();
    }

    public static int getNumMessages()
    {
        return messages.size();
    }

    /**
     * Returns an integer between 0 and 10 representing how busy the server has been over the last 24 hours
     */
    public static int getTraffic()
    {
        return Math.round((getRatio() / maxRatio) * 10f);
    }

    public static float getLastRatio()
    {
        return lastRatio;
    }

    public static float getMaxRatio()
    {
        return maxRatio;
    }
}
