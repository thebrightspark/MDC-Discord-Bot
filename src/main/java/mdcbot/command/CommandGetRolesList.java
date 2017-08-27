package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import net.dv8tion.jda.core.entities.Role;

public class CommandGetRolesList extends CommandBase
{
    public CommandGetRolesList()
    {
        super("rolesList", "Returns a list of all the roles on the server.");
    }

    @Override
    protected void doCommand(CommandEvent event)
    {
        StringBuilder sb = new StringBuilder("Roles:");
        for(Role role : event.getJDA().getRoles())
        {
            sb.append("\n").append(role.getName()).append(" -> ").append(role.getId());
        }

        event.reply(sb.toString());
    }
}
