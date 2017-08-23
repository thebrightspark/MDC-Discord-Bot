package mdcbot;

import java.io.*;
import java.util.*;
import java.util.function.Consumer;

public class Config
{
    private static Map<String, String> config = new HashMap<>();

    public static String set(EConfigs configKey, String configValue)
    {
        String output = setInternal(configKey, configValue);
        save();
        if(configKey == EConfigs.DISABLED_COMMANDS)
            MDCBot.reloadDisabledCommands();
        return output;
    }

    /**
     * Sets the value to the config key
     * @return the old value
     */
    public static String setInternal(EConfigs configKey, String configValue)
    {
        String output;
        if(configKey.canHaveMultipleValues)
        {
            //If the config can have multiple values
            String existing = getInternal(configKey);
            if(existing == null || existing.trim().isEmpty())
                //If nothing set, then just set to the value
                output = config.put(configKey.toString(), configValue);
            else
            {
                ArrayList<String> existingValues = new ArrayList<>(Arrays.asList(existing.split(",")));
                if(existingValues.contains(configValue))
                {
                    //If the value exists, then remove it
                    existingValues.remove(configValue);
                    if(existingValues.isEmpty())
                        output = config.put(configKey.toString(), "");
                    else
                    {
                        StringBuilder sb = new StringBuilder(existingValues.get(0));
                        for(int i = 1; i < existingValues.size(); i++)
                            sb.append(existingValues.get(i));
                        output = config.put(configKey.toString(), sb.toString());
                    }
                }
                else
                    //If the value doesn't exist, then add it
                    output = config.put(configKey.toString(), existing + "," + configValue);
            }
        }
        else
            //If the config doesn't have multiple values, then just set it normally
            output = config.put(configKey.toString(), configValue);
        return output;
    }

    public static String get(EConfigs configKey)
    {
        String value = getInternal(configKey);
        if(value == null)
        {
            config.put(configKey.toString(), configKey.defaultValue);
            value = configKey.defaultValue;
        }
        return value;
    }

    public static String getInternal(EConfigs configKey)
    {
        return config.get(configKey.toString());
    }

    public static boolean has(EConfigs configKey)
    {
        return config.containsKey(configKey.toString());
    }

    public static boolean hasValue(EConfigs configKey)
    {
        return has(configKey) && !config.get(configKey.toString()).trim().isEmpty();
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
                MDCBot.LOG.info("Created new config.properties");
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
            for(EConfigs c : EConfigs.values())
                setInternal(c, c.defaultValue);

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

            List<Map.Entry<String, String>> entries = new ArrayList<>(config.entrySet());

            //The following was my attempt at trying to sort the configs by key, but you can't sort the Properties :(
            //Sort the properties alphabetically by key
            //entries.sort((o1, o2) -> o1.getKey().compareToIgnoreCase(o2.getKey()));
            //Set the properties
            //entries.forEach(entry -> properties.setProperty(entry.getKey(), entry.getValue()));

            //Set the properties
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
