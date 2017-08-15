package mdcbot;

import mdcbot.listeners.MessageReceivedListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import org.apache.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.io.File;
import java.net.MalformedURLException;

public class MDCBot
{
    //JDA: https://github.com/DV8FromTheWorld/JDA
    //Example: https://github.com/DV8FromTheWorld/JDA/blob/master/src/examples/java/MessageListenerExample.java

    public static final String NAME = "MDCBot";
    public static final String CLIENT_ID = "346744066987261962";
    public static final String TOKEN = "MzQ2NzQ0MDY2OTg3MjYxOTYy.DHOTrg.LlAqQw3ZdC8LT4fNhREcp-Ae0gM";
    public static final String AUTH_URL = "https://discordapp.com/oauth2/authorize?&client_id=" + CLIENT_ID + "&scope=bot";

    public static final File RESOURCES_DIR = new File("src/main/resources");

    public static JDA jda;

    public static Logger LOG = Logger.getLogger(NAME);

    static
    {
        //Set log4j configuration file
        try
        {
            System.setProperty("log4j.configuration", new File(RESOURCES_DIR, "log4j.properties").toURI().toURL().toString());
        }
        catch(MalformedURLException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String... args)
    {
        LOG.info("Initialising bot...");

        try
        {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(TOKEN)
                    .addEventListener(new MessageReceivedListener())
                    .buildBlocking();
        }
        catch(LoginException | InterruptedException | RateLimitedException e)
        {
            e.printStackTrace();
        }

        LOG.info("Initialisation complete");
    }
}
