package mdcbot;

import java.io.*;
import java.util.*;

public class Config
{
    private static final List<String> finalKeys = Arrays.asList("token", "ownerID");
    private static Map<String, String> config = new HashMap<>();

    public static boolean canModify(String key)
    {
        return !finalKeys.contains(key);
    }

    public static boolean needsRestart(String key)
    {
        return key.equals("prefix");
    }

    public static String set(String configKey, String configValue)
    {
        String output = config.put(configKey, configValue);
        save();
        return output;
    }

    public static String setInternal(String configKey, String configValue)
    {
        return config.put(configKey, configValue);
    }

    public static String get(String configKey)
    {
        return config.get(configKey);
    }

    public static boolean has(String configKey)
    {
        return config.containsKey(configKey);
    }

    public static void init()
    {
        MDCBot.LOG.info("Config: " + MDCBot.CONFIG_FILE.getAbsolutePath());

        if(!MDCBot.CONFIG_FILE.exists())
        {
            try
            {
                if(!MDCBot.CONFIG_FILE.createNewFile())
                    MDCBot.LOG.error("Couldn't create config.properties");
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
        }

        load();

        if(config.isEmpty())
        {
            //Init config
            setInternal("token", "");
            setInternal("ownerID", "");
            setInternal("prefix", "!");
            setInternal("logChannelName", "logs");

            save();
        }
    }

    /**
     * Reads in the config file
     */
    public static void load()
    {
        Properties properties = new Properties();
        InputStream input = null;

        try
        {
            input = new FileInputStream(MDCBot.CONFIG_FILE);
            //Load properties
            properties.load(input);
            properties.forEach((o, o2) ->
            {
                if(o instanceof String && o2 instanceof String)
                {
                    config.put((String) o, (String) o2);
                    MDCBot.LOG.debug("Loaded config -> K: " + o + ", V: " + o2);
                }
            });
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(input != null)
            {
                try
                {
                    input.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Saves the current values to the config file
     */
    public static void save()
    {
        Properties properties = new Properties();
        OutputStream output = null;

        try
        {
            output = new FileOutputStream(MDCBot.CONFIG_FILE);
            //Set properties
            config.forEach(properties::setProperty);
            //Save properties
            properties.store(output, null);
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(output != null)
            {
                try
                {
                    output.close();
                }
                catch(IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
