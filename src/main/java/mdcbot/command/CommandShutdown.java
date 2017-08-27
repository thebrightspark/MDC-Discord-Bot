package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;

public class CommandShutdown extends CommandBase
{
    public CommandShutdown()
    {
        super("shutdown", "Safely shuts off the bot");
        rolePermission = RolePermission.ADMIN;
    }

    @Override
    protected void doCommand(CommandEvent event)
    {
        event.reactWarning();
        event.getJDA().shutdown();
    }
}
