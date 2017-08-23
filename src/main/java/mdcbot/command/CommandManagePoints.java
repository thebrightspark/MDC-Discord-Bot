package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.MDCBot;
import mdcbot.Util;
import mdcbot.points.UserPoints;
import net.dv8tion.jda.core.entities.User;

public class CommandManagePoints extends CommandBase{
    public CommandManagePoints(){
        super("points", "Manage a user's points.");
        rolePermission = RolePermission.MODERATOR;
    }

    public void doCommand(CommandEvent event){
        String[] args = Util.splitCommandArgs(event.getArgs());
        for(User user : MDCBot.users) {
            if(args.length == 3){
                if(user.getId().equals(args[1])) {
                    int points = Integer.parseInt(args[2]);
                    UserPoints.addOrSubPoints(user, points, !args[0].equalsIgnoreCase("add") && args[0].equalsIgnoreCase("remove"));
                    event.reply(MDCBot.jda.getUserById(Long.parseLong(args[1])) + " had " + points + " points " +
                            (args[0].equalsIgnoreCase("add") ? "added" : (args[0].equalsIgnoreCase("remove") ? "removed" : null))
                    );
                    user.openPrivateChannel().queue((channel)->{
                        channel.sendMessage("Aye nigga, ya'll now have " + UserPoints.getUsersPoints(user) + " points. If ya'll tink dis is a prob, talk to yo admin, aight?").queue();
                    });
                }
            }else if(args.length == 2){
                if(args[0].equalsIgnoreCase("get")) {
                    if (user.getId().equals(args[1])) {
                        event.reply(user.getName() + " has " + UserPoints.getUsersPoints(user) + " points.");
                    }
                }
            }else{
                event.replyError("That doesn't tickle me jones. Please use points <get:add:remove> <user_id> [amount]");
            }
        }
    }
}
