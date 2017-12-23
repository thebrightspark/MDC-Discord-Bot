package mdcbot.listeners;

import mdcbot.points.Player;
import mdcbot.points.PlayerKt;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;

import java.util.List;

import static mdcbot.points.PlayerKt.syncPointData;

public class UserJoinAndLeaveListener extends ListenerBase {
    public static List<User> users;

    @Override
    public void onReady(ReadyEvent event) {
        users = event.getJDA().getUsers();
        syncPointData();
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        users = event.getJDA().getUsers();
        for(User user : users){
            if(!PlayerKt.checkForPlayer(user)){
                Player player = new Player(user, 0);
            }
        }
    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        users = event.getJDA().getUsers();
    }
}
