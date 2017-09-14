package mdcbot;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.managers.GuildController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MuteHandler
{
    private static Map<User, List<Long>> mutedMembers = new HashMap<>();
    private static Map<User, Long> unmuteTimes = new HashMap<>();

    public static void init()
    {
        ScheduledExecutorService service = new ScheduledThreadPoolExecutor(1);
        service.scheduleAtFixedRate(MuteHandler::checkMutedMembers, 10, 30, TimeUnit.SECONDS);
    }

    public static boolean muteMember(Member member)
    {
        return muteMember(member, Config.getInt(EConfigs.DEFAULT_MUTE_TIME));
    }

    public static boolean muteMember(Member member, int muteTime)
    {
        return muteMember(member, muteTime, TimeUnit.MINUTES);
    }

    /**
     * Adds the muted role to the member and caches all of their roles before removing them from the member
     * This presumes that the muted role has been set in the configs
     */
    public static boolean muteMember(Member member, int muteTime, TimeUnit timeUnit)
    {
        User user = member.getUser();
        if(mutedMembers.containsKey(user))
            return false;

        //Cache member's roles
        List<Long> roleIds = new ArrayList<>();
        member.getRoles().forEach(role -> roleIds.add(role.getIdLong()));
        mutedMembers.put(user, roleIds);
        unmuteTimes.put(user, System.currentTimeMillis() + timeUnit.toMillis(muteTime));

        GuildController gc = member.getGuild().getController();

        //Remove all member's roles
        gc.removeRolesFromMember(member, member.getRoles()).queue();

        //Add muted role to member
        gc.addSingleRoleToMember(member, MDCBot.mutedRole).queue();

        return true;
    }

    /**
     * Removes the muted role from the member and adds back their cached roles they used to have before being muted
     * This presumes that the muted role has been set in the configs
     */
    public static boolean unmuteMember(Member member)
    {
        User user = member.getUser();
        if(mutedMembers.containsKey(user))
            return false;

        //Get cached roles for member
        List<Role> rolesToAdd = new ArrayList<>();
        Guild guild = member.getGuild();
        for(long roleId : mutedMembers.get(user))
        {
            Role role = guild.getRoleById(roleId);
            if(role != null) rolesToAdd.add(role);
        }

        GuildController gc = guild.getController();

        //Add roles back to member
        gc.addRolesToMember(member, rolesToAdd).queue();

        //Remove muted role from member
        if(MDCBot.mutedRole != null)
            gc.removeSingleRoleFromMember(member, MDCBot.mutedRole).queue();

        mutedMembers.remove(user);
        unmuteTimes.remove(user);

        return true;
    }

    public static long getMutedTimeLeftMins(Member member)
    {
        long timeLeft = getMutedTimeLeftMillis(member);
        return timeLeft > 0 ? TimeUnit.MINUTES.convert(timeLeft, TimeUnit.MILLISECONDS) : 0;
    }

    public static long getMutedTimeLeftMillis(Member member)
    {
        return Math.max(0, unmuteTimes.get(member.getUser()) - System.currentTimeMillis());
    }

    /**
     * Check through muted members and unmute any which have passed their unmute time
     */
    private static void checkMutedMembers()
    {
        long currentTime = System.currentTimeMillis();
        for(Map.Entry<User, Long> timeEntry : unmuteTimes.entrySet())
        {
            if(timeEntry.getValue() <= currentTime)
            {
                User user = timeEntry.getKey();
                for(Guild guild : user.getMutualGuilds())
                {
                    Member member = guild.getMember(user);
                    if(member != null)
                        unmuteMember(member);
                }
            }
        }
    }
}
