package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.Util;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.Member;

public class CommandAnnounce extends CommandBase
{
    public CommandAnnounce()
    {
        super("announce", "The bot reposts the message as an embedded message for better visibility");
        rolePermission = RolePermission.MODERATOR;
        removeSentMessage = true;
        aliases = new String[] {"announcement"};
    }

    @Override
    protected void doCommand(CommandEvent event)
    {
        Member member = event.getMember();
        if(member != null && !event.getArgs().isEmpty())
        {
            EmbedBuilder builder = new EmbedBuilder();
            builder.setAuthor(Util.getFullUser(member.getUser()), null, member.getUser().getEffectiveAvatarUrl());
            builder.setTitle("Announcement");
            builder.setDescription(event.getArgs());
            builder.setColor(member.getColor());
            event.reply(builder.build());
        }
    }
}
