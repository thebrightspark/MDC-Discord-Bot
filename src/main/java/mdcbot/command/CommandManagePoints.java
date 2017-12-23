package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.points.Player;
import mdcbot.points.PlayerKt;
import mdcbot.utils.Util;
import net.dv8tion.jda.core.entities.User;

import java.util.Objects;

public class CommandManagePoints extends CommandBase{
    public CommandManagePoints(){
        super("points", "Manage a user's points.");
        rolePermission = RolePermission.MODERATOR;
    }

    public void doCommand(CommandEvent event){
        String[] args = (String[])Util.splitCommandArgs(event.getArgs()).toArray();
        if(args[1].equals(event.getAuthor().getId())){
            event.reply("You cannot edit your own points!");
        }else {
            Player player = PlayerKt.readPlayerFromJson(args[1]);
            if(player != null) {
                if(player.getUser() != null) {
                    if (player.getUser().getName().equals(args[1])) {
                        int points = Integer.parseInt(args[2]);
                        switch (args[0]) {
                            case "add":
                                player.setPoints(player.getPoints() + points);
                                break;
                            case "remove":
                                player.setPoints(player.getPoints() - points);
                                break;
                            case "set":
                                player.setPoints(points);
                                break;
                        }
                    }
                }
            }
        }
    }
}
