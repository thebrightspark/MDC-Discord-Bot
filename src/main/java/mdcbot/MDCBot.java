package mdcbot;

import mdcbot.listeners.MessageReceivedListener;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.EventListener;
import org.apache.log4j.Logger;

import javax.security.auth.login.LoginException;
import java.util.ArrayList;
import java.util.List;

public class MDCBot
{
    //JDA: https://github.com/DV8FromTheWorld/JDA

    public static final String NAME = "MDCBot";
    public static final String TOKEN = "MzQ2NzQ0MDY2OTg3MjYxOTYy.DHOTrg.LlAqQw3ZdC8LT4fNhREcp-Ae0gM";
    public static final String AUTH_URL = "https://discordapp.com/oauth2/authorize?&client_id=346744066987261962&scope=bot";

    public static final Logger LOG = Logger.getLogger(NAME);

    public static JDA jda;

    private static List<EventListener> listeners = new ArrayList<>();

    public static void main(String... args)
    {
        init();

        try
        {
            jda = new JDABuilder(AccountType.BOT)
                    .setToken(TOKEN)
                    .addEventListener(listeners)
                    .buildBlocking();
        }
        catch(LoginException | InterruptedException | RateLimitedException e)
        {
            e.printStackTrace();
        }
    }

    public static void init()
    {
        regListener(new MessageReceivedListener());
    }

    private static void regListener(EventListener listener)
    {
        listeners.add(listener);
    }
}
