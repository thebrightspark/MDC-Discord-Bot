package mdcbot;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import mdcbot.command.*;
import mdcbot.listeners.*;
import mdcbot.utils.Util;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

public class MDCBot
{
    /*
        JDA: https://github.com/DV8FromTheWorld/JDA
        Example: https://github.com/DV8FromTheWorld/JDA/blob/master/src/examples/java/MessageListenerExample.java
    */

    public static final String NAME = "MDCBot";
    public static final String CLIENT_ID = "346744066987261962";
    public static final String AUTH_URL = "https://discordapp.com/oauth2/authorize?&client_id=" + CLIENT_ID + "&scope=bot";

    public static final File RESOURCES_DIR = new File("src/main/resources");
    public static final File SAVES_DIR = new File("saves");
    public static final File RULES_DIR = new File("rules");
    public static final File LOG4J_PROPERTIES_FILE = new File(RESOURCES_DIR, "log4j.properties");
    public static final File CONFIG_FILE = Paths.get("config.properties").toFile();
    public static final File USER_POINTS_FILE = new File(SAVES_DIR, "user_points.txt");
    public static final File TRAFFIC_USERS_FILE = new File(SAVES_DIR, "traffic_users.txt");
    public static final File TRAFFIC_MESSAGES_FILE = new File(SAVES_DIR, "traffic_messages.txt");
    public static final File TRAFFIC_MAXRATIO_FILE = new File(SAVES_DIR, "traffic_maxratio.txt");
    public static final File RULES_FILE = new File(RULES_DIR,"rules.txt");

    public static JDA jda;
    public static String PREFIX;
    public static TextChannel logChannel;
    public static Role newMemberRole;
    public static Role mutedRole;
    public static EventWaiter waiter = new EventWaiter();

    private static Logger log = Util.getLogger(MDCBot.class);
    private static List<Command> commands = new ArrayList<>();
    private static List<String> disabledCommands = new ArrayList<>();
    private static List<String> adminRoles = new ArrayList<>();
    private static List<String> moderatorRoles = new ArrayList<>();

    public static List<User> users;

    static
    {
        //Set log4j configuration file
        try
        {
            System.setProperty("log4j.configuration", LOG4J_PROPERTIES_FILE.toURI().toURL().toString());
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    private static void addCommand(Command command)
    {
        if(!commands.add(command))
            log.warn("Tried to add duplicate command! Command not added: " + command.getName());
        else
            log.info("Added command '" + command.getName() + "'");
    }

    public static void initCommands()
    {
        //addCommand(new PingCommand());
        addCommand(new CommandHello());
        addCommand(new CommandConfig());
        addCommand(new CommandUsage());
        addCommand(new CommandRandQuote());
        addCommand(new CommandManagePoints());
        addCommand(new CommandGetUsersList());
        addCommand(new CommandTraffic());
        addCommand(new CommandAcceptRules());
        addCommand(new CommandGetRolesList());
        addCommand(new CommandMute());
        addCommand(new CommandUnmute());
        addCommand(new CommandAnnounce());
        addCommand(new CommandTimeZone());
        addCommand(new CommandShutdown());
        addCommand(new CommandDebug());
    }

    public static void main(String... args)
    {
        log.info("\n=============================" +
                 "\n====== Initialising Bot =====" +
                 "\n=============================");

        Config.init();
        if(!Config.hasValue(EConfigs.OWNER_ID))
        {
            log.error("Config value '" + EConfigs.OWNER_ID + "' has not been set!");
            System.exit(0);
        }
        if(!Config.hasValue(EConfigs.TOKEN))
        {
            log.error("Config value '" + EConfigs.TOKEN + "' has not been set!");
            System.exit(0);
        }

        PREFIX = Config.get(EConfigs.PREFIX);

        //Populate disabledCommands list from config
        reloadDisabledCommands();

        //Get admin and moderator roles
        adminRoles.addAll(Arrays.asList(Config.get(EConfigs.ADMIN_ROLES).split(",")));
        moderatorRoles.addAll(Arrays.asList(Config.get(EConfigs.MOD_ROLES).split(",")));

        initCommands();

        CommandClientBuilder client = new CommandClientBuilder();
        client.useDefaultGame();
        client.setOwnerId(Config.get(EConfigs.OWNER_ID));
        client.setPrefix(PREFIX);
        client.addCommands(commands.toArray(new Command[commands.size()]));

        try
        {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(Config.get(EConfigs.TOKEN))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setGame(Game.of("Loading..."))
                    .addEventListener(
                            client.build(),
                            waiter,
                            new TrafficManager(),
                            new UserJoinAndLeaveListener(),
                            new MutedListener(),
                            new AutomodListener()
                    ).buildBlocking();
        }
        catch(LoginException | InterruptedException | RateLimitedException e)
        {
            e.printStackTrace();
        }

        users = UserJoinAndLeaveListener.users;

        /*users = jda.getUsers();

        for(User user : users){
            UserPoints.addOrSubPoints(user, 5, false);
        }*/

        //Find logs channel
        String logChannelConfig = Config.get(EConfigs.LOG_CHANNEL_NAME);
        if(!logChannelConfig.isEmpty())
            logChannel = findTextChannel(logChannelConfig, "log");
        else
            log.warn("No log channel set in config!");

        //Find new member role
        String newMemberRoleConfig = Config.get(EConfigs.NEW_MEMBER_ROLE);
        if(!newMemberRoleConfig.isEmpty())
            newMemberRole = findRole(newMemberRoleConfig, "new member");
        else
            log.warn("No new member role set in the config!");

        //Find muted role
        String mutedRoleConfig = Config.get(EConfigs.MUTED_ROLE);
        if(!mutedRoleConfig.isEmpty())
            mutedRole = findRole(mutedRoleConfig, "muted");
        else
            log.warn("No muted role set in the config!");

        //Get the owner
        User owner = jda.getUserById(Config.get(EConfigs.OWNER_ID));
        if(owner != null)
            log.info("The owner of this bot is " + owner.getName() + "#" + owner.getDiscriminator());
        else
            log.warn("The owner with ID " + Config.get(EConfigs.OWNER_ID) + " does not exist!");

        Config.save();

        TrafficManager.init();
        MuteHandler.init();

        log.info("\n=============================" +
                 "\n== Initialisation Complete ==" +
                 "\n=============================");

        FileChangeListener.watchFileForChanges(RULES_FILE);
    }

    /**
     * Finds a channel using an ID or name
     */
    private static TextChannel findTextChannel(String channel, String channelDescription)
    {
        TextChannel channelFound = null;
        try
        {
            long channelId = Long.parseLong(channel);
            channelFound = jda.getTextChannelById(channelId);
            if(channelFound == null) log.warn("No " + channelDescription + " channel found with ID " + channel);
        }
        catch(NumberFormatException e)
        {
            List<TextChannel> channels = jda.getTextChannelsByName(channel, false);
            if(!channels.isEmpty()) channelFound = channels.get(0);
            else log.warn("No " + channelDescription + " channels found for '" + channel + "'");
        }
        return channelFound;
    }

    /**
     * Finds a role using an ID or name
     */
    private static Role findRole(String role, String roleDescription)
    {
        Role roleFound = null;
        try
        {
            long roleId = Long.parseLong(role);
            roleFound = jda.getRoleById(roleId);
            if(roleFound == null) log.warn("No " + roleDescription + " role found with ID " + role);
        }
        catch(NumberFormatException e)
        {
            List<Role> roles = jda.getRolesByName(role, false);
            if(!roles.isEmpty()) roleFound = roles.get(0);
            else log.warn("No " + roleDescription + " roles found for '" + role + "'");
        }
        return roleFound;
    }

    public static void reloadDisabledCommands()
    {
        String dCommandsConfig = Config.get(EConfigs.DISABLED_COMMANDS);
        disabledCommands.clear();
        if(dCommandsConfig != null && !dCommandsConfig.trim().isEmpty())
        {
            String[] dCommands = dCommandsConfig.split(",");
            for(String dc : dCommands)
                disabledCommands.add(dc.trim());
        }
    }

    public static boolean isCommandDisabled(String name)
    {
        return disabledCommands.contains(name);
    }

    public static boolean doesCommandExist(String name)
    {
        for(Command command : commands)
            if(command.getName().equalsIgnoreCase(name))
                return true;
        return false;
    }

    /**
     * Gets the usage message if it exists
     */
    public static MessageEmbed getCommandUsage(Guild guild, String name)
    {
        for(Command command : commands)
            if(command.getName().equalsIgnoreCase(name))
                return command instanceof CommandBase ? ((CommandBase) command).getUsageEmbed(guild) : null;
        return null;
    }

    public static User getUserByID(long userId)
    {
        for(User user : users)
            if(user.getIdLong() == userId)
                return user;
        return null;
    }

    public static boolean isMemberBotAdmin(Member member)
    {
        if(member == null) return false;
        for(Role role : member.getRoles())
            for(String modRole : adminRoles)
                if(modRole.equalsIgnoreCase(role.getName()))
                    return true;
        return false;
    }

    public static boolean isMemberBotModerator(Member member)
    {
        if(member == null) return false;
        for(Role role : member.getRoles())
            for(String modRole : moderatorRoles)
                if(modRole.equalsIgnoreCase(role.getName()))
                    return true;
        return isMemberBotAdmin(member);
    }

    public static boolean isMemberMuted(Member member)
    {
        if(member == null) return false;
        for(Role role : member.getRoles())
            if(role.getName().equals(mutedRole.getName()))
                return true;
        return false;
    }
}
