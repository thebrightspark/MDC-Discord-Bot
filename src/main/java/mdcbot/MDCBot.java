package mdcbot;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import mdcbot.command.*;
import mdcbot.listeners.Listener;
import mdcbot.points.UserPoints;
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

        My Token: MzQ2NzQ0MDY2OTg3MjYxOTYy.DHOTrg.LlAqQw3ZdC8LT4fNhREcp-Ae0gM
        My User ID: 106346651777507328
    */

    public static final String NAME = "MDCBot";
    public static final String CLIENT_ID = "346744066987261962";
    public static final String AUTH_URL = "https://discordapp.com/oauth2/authorize?&client_id=" + CLIENT_ID + "&scope=bot";

    public static final File RESOURCES_DIR = new File("src/main/resources");
    public static final File LOG4J_PROPERTIES_FILE = new File(RESOURCES_DIR, "log4j.properties");
    public static final File CONFIG_FILE = Paths.get("config.properties").toFile();

    public static JDA jda;
    public static Logger LOG = Logger.getLogger(NAME);
    public static String PREFIX;
    public static TextChannel logChannel;
    public static EventWaiter waiter = new EventWaiter();

    public static List<Command> commands = new ArrayList<>();
    public static List<User> users;
    public static List<String> adminRoles = new ArrayList<>();
    public static List<String> moderatorRoles = new ArrayList<>();

    private static List<String> disabledCommands = new ArrayList<>();

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
            LOG.warn("Tried to add duplicate command! Command not added: " + command.getName());
        else
            LOG.info("Added command '" + command.getName() + "'");
    }

    public static void initCommands()
    {
        //addCommand(new PingCommand());
        addCommand(new CommandHello());
        addCommand(new CommandConfig());
        addCommand(new CommandRandQuote());
        addCommand(new CommandManagePoints());
    }

    public static void main(String... args)
    {
        LOG.info("Initialising bot...");

        Config.init();
        if(!Config.hasValue(EConfigs.OWNER_ID))
        {
            LOG.error("Config value '" + EConfigs.OWNER_ID + "' has not been set!");
            System.exit(0);
        }
        if(!Config.hasValue(EConfigs.TOKEN))
        {
            LOG.error("Config value '" + EConfigs.TOKEN + "' has not been set!");
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
                            new Listener()) //,
                            //new MessageReceivedListener())
                    .buildBlocking();
        }
        catch(LoginException | InterruptedException | RateLimitedException e)
        {
            e.printStackTrace();
        }

        //Find logs channel
        List<TextChannel> logChannels = jda.getTextChannelsByName(Config.get(EConfigs.LOG_CHANNEL_NAME), false);
        if(! logChannels.isEmpty()) logChannel = logChannels.get(0);

        users = jda.getUsers();
        for(User user : users)
            UserPoints.addOrSubPoints(user, 5, false);

        Config.save();

        LOG.info("Initialisation complete");
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

    public static User getUserByID(long userId)
    {
        for(User user : users)
            if(user.getIdLong() == userId)
                return user;
        return null;
    }

    public static boolean isMemberBotAdmin(Member member)
    {
        for(Role role : member.getRoles())
            for(String modRole : adminRoles)
                if(modRole.equalsIgnoreCase(role.getName()))
                    return true;
        return false;
    }

    public static boolean isMemberBotModerator(Member member)
    {
        for(Role role : member.getRoles())
            for(String modRole : moderatorRoles)
                if(modRole.equalsIgnoreCase(role.getName()))
                return true;
        return isMemberBotAdmin(member);
    }
}
