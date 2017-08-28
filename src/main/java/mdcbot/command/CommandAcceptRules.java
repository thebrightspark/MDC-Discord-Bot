package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.MDCBot;
import mdcbot.Util;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.List;

public class CommandAcceptRules extends CommandBase
{
    public CommandAcceptRules()
    {
        super("acceptRules", "Used for a new member to accept the rules in a DM to the bot");
        guildOnly = false;
    }

    @Override
    protected void doCommand(CommandEvent event)
    {
        if(event.getChannelType() != ChannelType.PRIVATE)
            return;

        User user = event.getAuthor();
        List<Guild> mutualGuilds = user.getMutualGuilds();
        for(Guild guild : mutualGuilds)
        {
            Member member = guild.getMember(user);
            if(member != null && member.getRoles().isEmpty())
            {
                GuildController gc = guild.getController();
                gc.addSingleRoleToMember(member, MDCBot.newMemberRole).queue();
                Util.info(getClass(), "New member %s given the role %s in guild %s", member.getEffectiveName(), MDCBot.newMemberRole.getName(), guild.getName());
            }
        }
    }
}
