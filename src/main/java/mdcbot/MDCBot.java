package mdcbot;

import com.jagrosh.jdautilities.commandclient.Command;
import com.jagrosh.jdautilities.commandclient.CommandClientBuilder;
import com.jagrosh.jdautilities.commandclient.examples.PingCommand;
import com.jagrosh.jdautilities.waiter.EventWaiter;
import mdcbot.command.CommandHello;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.TextChannel;
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

    public static List<Command> commands = new ArrayList<>();

    private static void addCommand(Command command)
    {
        if(!commands.add(command))
            LOG.warn("Tried to add duplicate command! Command not added: " + command.getName());
        else
            LOG.info("Added command '" + command.getName() + "'");
    }

    public static void initCommands()
    {
        addCommand(new CommandHello());
        addCommand(new PingCommand());
    }

    public static void main(String... args)
    {
        LOG.info("Initialising bot...");

        Config.init();
        if(!Config.has("ownerID"))
        {
            LOG.error("Config value 'ownerID' has not been set!");
            System.exit(0);
        }
        if(!Config.has("token"))
        {
            LOG.error("Config value 'token' has not been set!");
            System.exit(0);
        }

        PREFIX = Config.get("prefix");

        initCommands();

        CommandClientBuilder client = new CommandClientBuilder();
        client.useDefaultGame();
        client.setOwnerId(Config.get("ownerID"));
        client.setPrefix(PREFIX);
        client.addCommands(commands.toArray(new Command[commands.size()]));

        try
        {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(Config.get("token"))
                    .setStatus(OnlineStatus.DO_NOT_DISTURB)
                    .setGame(Game.of("Loading..."))
                    .addEventListener(
                            client.build(),
                            waiter) //,
                            //new MessageReceivedListener())
                    .buildBlocking();
        }
        catch(LoginException | InterruptedException | RateLimitedException e)
        {
            e.printStackTrace();
        }

        //Find logs channel
        List<TextChannel> logChannels = jda.getTextChannelsByName(Config.get("logChannelName"), false);
        if(! logChannels.isEmpty()) logChannel = logChannels.get(0);

        LOG.info("Initialisation complete");
    }
}
