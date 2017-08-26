package mdcbot.listeners;

import mdcbot.points.UserPoints;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.List;

public class UserJoinAndLeaveEvent extends ListenerAdapter {
    public static List<User> users;

    @Override
    public void onReady(ReadyEvent event) {
        users = event.getJDA().getUsers();
        UserPoints.init(event);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        users = event.getJDA().getUsers();
        UserPoints.addOrSubPoints(event.getUser(), 5, false);
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        users = event.getJDA().getUsers();
        UserPoints.addOrSubPoints(event.getUser(), UserPoints.getUsersPoints(event.getUser()), true);
    }
}
