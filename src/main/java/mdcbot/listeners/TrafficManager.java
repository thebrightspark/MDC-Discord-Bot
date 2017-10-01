package mdcbot.listeners;

import mdcbot.DatedUser;
import mdcbot.LogLevel;
import mdcbot.MDCBot;
import mdcbot.utils.Util;
import mdcbot.io.FileManager;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.PermissionOverride;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This class records the amount of messages sent in all public channels and the amount of unique users who sent the
 * messages within the last 24 hours.
 *
 * You can get a "traffic rating" using the traffic command
 * {@link mdcbot.command.CommandTraffic}
 */
public class TrafficManager extends ListenerAdapter
{
    private static Logger log = Util.getLogger(TrafficManager.class);
    private static List<DatedUser> users = new ArrayList<>();
    private static List<Date> messages = new ArrayList<>();
    private static float maxRatio = -1f;
    private static Date maxRatioDateMin, maxRatioDateMax;
    private static float lastRatio = -1f;
    private static FileManager fm_users = new FileManager(MDCBot.SAVES_DIR,  MDCBot.TRAFFIC_USERS_FILE);
    private static FileManager fm_messages = new FileManager(MDCBot.SAVES_DIR,  MDCBot.TRAFFIC_MESSAGES_FILE);
    private static FileManager fm_maxratio = new FileManager(MDCBot.SAVES_DIR,  MDCBot.TRAFFIC_MAXRATIO_FILE);
    private static boolean dataChanged = false;

    public static void init()
    {
        readFromFiles();

        //Setup a thread to run every 30s to save the traffic data
        ScheduledExecutorService executorService = new ScheduledThreadPoolExecutor(1);
        executorService.scheduleAtFixedRate(() ->
        {
            if(dataChanged) saveToFiles();
        }, 10, 30, TimeUnit.SECONDS);
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event)
    {
        if(event.getAuthor().isBot())
            return;

        TextChannel channel = event.getChannel();
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
        //debug("Adding message: %s -> '%s'", messageTimestamp, event.getMessage().getContent());
        if(!hasUser(event.getAuthor()))
            users.add(new DatedUser(event.getAuthor(), messageTimestamp));
        messages.add(messageTimestamp);

        //Make sure the list is kept sorted
        messages.sort(Comparator.naturalOrder());

        checkData();

        dataChanged = true;
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

    public static void saveToFiles()
    {
        dataChanged = false;

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

        debug("Saved traffic data");
    }

    public static void readFromFiles()
    {
        //Read users
        users.clear();
        for(String line : fm_users.readFromFile())
            users.add(new DatedUser(line));
        debug("Read %s users from file", users.size());

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
                error("Couldn't parse to Date: " + line);
                e.printStackTrace();
            }
        }
        debug("Read %s messages from file", messages.size());

        //Read max ratio
        List<String> maxRatioFile = fm_maxratio.readFromFile();
        if(!maxRatioFile.isEmpty())
        {
            String[] maxRatioSplit = maxRatioFile.get(0).split(",");
            try
            {
                maxRatio = Float.parseFloat(maxRatioSplit[0]);
                maxRatioDateMin = new Date(Long.parseLong(maxRatioSplit[1]));
                maxRatioDateMax = new Date(Long.parseLong(maxRatioSplit[2]));
            }
            catch(NumberFormatException e)
            {
                e.printStackTrace();
            }
            debug("Read max ratio from file");
        }

        checkData();
    }

    private static float getRatio()
    {
        lastRatio = (float) messages.size() / (float) users.size();
        updateMaxRatio(lastRatio);
        return lastRatio;
    }

    private static void updateMaxRatio(float ratio)
    {
        if(ratio > maxRatio)
        {
            maxRatio = ratio;
            maxRatioDateMin = messages.get(0);
            maxRatioDateMax = messages.get(messages.size() - 1);
            String toFile = maxRatio + "," + maxRatioDateMin.getTime() + "," + maxRatioDateMax.getTime();
            fm_maxratio.writeToFile(toFile);
            debug("Updated max ratio to: %s, %s, %s", maxRatio, maxRatioDateMin, maxRatioDateMax);
        }
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

    private static void log(Level level, String message, Object... args)
    {
        log.log(level, String.format(message, args));
    }

    private static void info(String message, Object... args)
    {
        log(LogLevel.INFO.log4jLevel, message, args);
    }

    private static void debug(String message, Object... args)
    {
        log(LogLevel.DEBUG.log4jLevel, message, args);
    }

    private static void error(String message, Object... args)
    {
        log(LogLevel.ERROR.log4jLevel, message, args);
    }
}
