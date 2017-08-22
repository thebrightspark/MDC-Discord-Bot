package mdcbot;

public enum EConfigs
{
    TOKEN("", false, false, false),
    OWNER_ID("", false, false, false),
    PREFIX("!", true, false, true),
    LOG_CHANNEL_NAME("logs", true, false, false),
    DISABLED_COMMANDS("", true, true, false);

    public final String defaultValue;
    public final boolean canCommandModify, canHaveMultipleValues, needsRestart;

    EConfigs(String defaultValue, boolean canCommandModify, boolean canHaveMultipleValues, boolean needsRestart)
    {
        this.defaultValue = defaultValue;
        this.canCommandModify = canCommandModify;
        this.canHaveMultipleValues = canHaveMultipleValues;
        this.needsRestart = needsRestart;
    }

    @Override
    public String toString()
    {
        return super.toString().toLowerCase();
    }

    public static EConfigs getByName(String name)
    {
        for(EConfigs config : values())
            if(config.toString().equalsIgnoreCase(name))
                return config;
        return null;
    }
}
