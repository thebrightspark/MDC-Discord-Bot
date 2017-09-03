package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.EConfigs;
import mdcbot.MDCBot;
import mdcbot.utils.Util;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

import java.util.List;

public class CommandMute extends CommandBase
{
    public CommandMute()
    {
        super("mute", "Mutes a member by giving them the configured muted role");
        removeSentMessage = true;
    }

    @Override
    protected void doCommand(CommandEvent event)
    {
        if(MDCBot.mutedRole == null)
        {
            fail(event, "No muted role. Set one using the command:\n" +
                    MDCBot.PREFIX + "config " + EConfigs.MUTED_ROLE + " <role name>");
        }

        String args = event.getArgs();
        if(args.isEmpty())
        {
            fail(event, "Must provide a member to mute");
            return;
        }

        Guild guild = event.getGuild();
        Member member = null;

        //If the user is tagged, extract just the user ID
        if(args.startsWith("<@"))
            args = args.substring(2, args.length() - 1);

        try
        {
            //Try parse argument as a user ID
            member = guild.getMemberById(args);
        }
        catch(NumberFormatException e)
        {
            //Try parse argument as a member name
            List<Member> members = guild.getMembersByEffectiveName(args, true);
            if(!members.isEmpty())
            {
                if(members.size() > 1)
                {
                    fail(event, "Found " + members.size() + " with the name '" + args + "'.\n" +
                            "Please use `@` mention the user with this command instead.");
                    return;
                }
                member = members.get(0);
            }
        }

        if(member == null)
        {
            fail(event, "Couldn't find member '" + args + "'");
        }
        else if(member.getRoles().contains(MDCBot.mutedRole))
        {
            fail(event, "Member '" + member.getEffectiveName() + "' is already muted");
        }
        else
        {
            info(event, "Member %s has been muted", member.getEffectiveName());
            event.reply(Util.createBotMessage(guild, "Member %s (%s) has been muted", member.getEffectiveName(), member.getRoles().get(0).getName()));
            guild.getController().addSingleRoleToMember(member, MDCBot.mutedRole).queue();
        }
    }
}
