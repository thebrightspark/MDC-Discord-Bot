package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.EConfigs;
import mdcbot.MDCBot;
import mdcbot.MuteHandler;
import mdcbot.utils.Util;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;

public class CommandUnmute extends CommandBase
{
    public CommandUnmute()
    {
        super("unmute", "Unmute a member that's already muted");
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

        Guild guild = event.getGuild();
        Member member = getMemberFromString(event, guild, args);

        if(member == null) return;
        if(member.getRoles().contains(MDCBot.mutedRole))
        {
            if(MuteHandler.unmuteMember(member))
            {
                //Unmuted member
                info(event, "Member '%s' has been unmuted", member.getEffectiveName());
                event.reply(Util.createBotMessage(guild, "Member '%s' (%s) has been unmuted", member.getEffectiveName(), member.getRoles().get(0).getName()));
            }
            else
            {
                //Member is not muted
                warn(event, "Member '%s' is not muted", member.getEffectiveName());
                event.reply(Util.createBotMessage(guild, "Member '%s' (%s) is not muted", member.getEffectiveName(), member.getRoles().get(0).getName()));
            }
        }
    }
}
