package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.listeners.TrafficManager;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

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
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(event.getSelfMember().getColor());
        builder.setTitle("Server Traffic");
        builder.addField(new MessageEmbed.Field("Traffic", TrafficManager.getTraffic() + "/10", true));

        if(event.getArgs().equalsIgnoreCase("details"))
        {
            builder.addField(new MessageEmbed.Field("Current ratio", String.valueOf(TrafficManager.getLastRatio()), true));
            builder.addField(new MessageEmbed.Field("Max ratio", String.valueOf(TrafficManager.getMaxRatio()), true));
            builder.addField(new MessageEmbed.Field("#Messages", String.valueOf(TrafficManager.getNumMessages()), true));
            builder.addField(new MessageEmbed.Field("#Users", String.valueOf(TrafficManager.getNumUsers()), true));
        }

        reply(event, builder.build());
    }
}
