package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.Config;
import mdcbot.EConfigs;
import mdcbot.MDCBot;
import mdcbot.MuteHandler;
import mdcbot.utils.Util;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class CommandMute extends CommandBase
{
    public CommandMute()
    {
        super("mute", "Mutes a member by giving them the configured muted role");
        removeSentMessage = true;
        rolePermission = RolePermission.MODERATOR;
    }

    @Override
    protected void doCommand(CommandEvent event)
    {
        if(MDCBot.mutedRole == null)
        {
            fail(event, "No muted role. Set one using the command:\n" +
                    "%s config %s <role name>", MDCBot.PREFIX, EConfigs.MUTED_ROLE);
            return;
        }

        String args = event.getArgs();
        if(args.isEmpty())
        {
            fail(event, "Must provide a member to mute");
            return;
        }

        int time = Config.getInt(EConfigs.DEFAULT_MUTE_TIME);

        //Try get the time from the first argument
        String[] argSplit = Util.splitCommandArgs(args);
        boolean gotTimeFromArgs = false;
        if(argSplit.length > 1)
        {
            try
            {
                time = Integer.parseInt(argSplit[0]);
                gotTimeFromArgs = true;
            }
            catch(NumberFormatException e)
            {
                //Do nothing
            }
        }

        if(gotTimeFromArgs)
        {
            //Get member after time argument
            StringBuilder sb = new StringBuilder();
            for(int i = 1; i < argSplit.length; i++)
            {
                if(i > 1) sb.append(" ");
                sb.append(argSplit[i]);
            }
            args = sb.toString();

            if(args.isEmpty())
            {
                fail(event, "Must provide a member to mute");
                return;
            }
        }

        Guild guild = event.getGuild();
        Member member = getMemberFromString(event, guild, args);

        if(member == null) return;
        if(member.getRoles().contains(MDCBot.mutedRole))
        {
            //Member already muted
            long timeLeft = MuteHandler.getMutedTimeLeftMins(member);
            fail(event, "%s is already muted for %s more minutes", member.getEffectiveName(), timeLeft);
        }
        else
        {
            String memberMainRole = member.getRoles().get(0).getName();
            if(MuteHandler.muteMember(member, time))
            {
                //Member muted
                info(event, "%s has been muted for %s mins", member.getEffectiveName(), time);
                reply(event, "%s (%s) has been muted for %s mins", member.getEffectiveName(), memberMainRole, time);
            }
            else
            {
                //Failed to mute member
                long timeLeft = MuteHandler.getMutedTimeLeftMins(member);
                warn(event, "%s is already muted for %s more minutes", member.getEffectiveName(), timeLeft);
                reply(event, "Failed to mute member '%s' (%s)", member.getEffectiveName(), memberMainRole);
            }
        }
    }
}
