package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.listeners.TrafficManager;

public class CommandTraffic extends CommandBase
{
    public CommandTraffic()
    {
        super("traffic", "Returns an integer between 0 and 10 representing how busy the server has been over the last 24 hours");
        rolePermission = RolePermission.MODERATOR;
    }

    @Override
    protected void doCommand(CommandEvent event)
    {
        event.reply("Current traffic: " + TrafficManager.getTraffic() + "/10");
        if(event.getArgs().equalsIgnoreCase("details"))
        {
            String reply = String.format("Details:\n#Messages: %s\n#Users: %s\nCurrent ratio: %s\nMax ratio: %s",
                    TrafficManager.getNumMessages(), TrafficManager.getNumUsers(), TrafficManager.getLastRatio(), TrafficManager.getMaxRatio());
            event.reply(reply);
        }
    }
}
