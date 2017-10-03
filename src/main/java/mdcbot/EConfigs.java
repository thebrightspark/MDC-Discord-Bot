package mdcbot;

public enum EConfigs
{
    TOKEN("", false, false, false),
    OWNER_ID("", false, false, false),
    PREFIX("!", true, false, true),
    LOG_LEVEL("debug", true, false, false),
    LOG_CHANNEL_NAME("logs", true, false, false),
    DISABLED_COMMANDS("", true, true, false),
    ADMIN_ROLES("Admin", true, true, false),
    MOD_ROLES("Moderator", true, true, false),
    NEW_MEMBER_ROLE("New Recruit", true, false, false),
    MUTED_ROLE("Muted", true, false, false),
    DEFAULT_MUTE_TIME("10", true, false, false),
    CHANNEL_LINK_BLACKLIST("", true, true, false),
    CHANNEL_FILE_BLACKLIST("", true, true, false),
    CHANNEL_SPAM_CHARACTERS_BLACKLIST("", true, true, false),
    CHANNEL_SPAM_MESSAGES_BLACKLIST("", true, true, false),
    CHANNEL_PROFANITY_BLACKLIST("", true, true, false);

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
