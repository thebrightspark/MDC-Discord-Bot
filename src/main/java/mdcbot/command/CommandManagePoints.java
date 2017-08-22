package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.MDCBot;
import mdcbot.Util;
import net.dv8tion.jda.core.entities.User;

public class CommandManagePoints extends CommandBase{
    public CommandManagePoints(){
        super("points", "Manage a user's points.");
    }

    public void doCommand(CommandEvent event){
        String[] args = Util.splitCommandArgs(event.getArgs());
        for(User user : MDCBot.users) {
            if(args.length == 3){
                if(user.getId().equals(args[1])) {
                    int points = Integer.parseInt(args[2]);
                    MDCBot.points.addOrSubPoints(user, points, !args[0].equalsIgnoreCase("add") && args[0].equalsIgnoreCase("remove"));
                    int r = MDCBot.points.remainders;
                    event.reply(MDCBot.jda.getUserById(Long.parseLong(args[1])) + " had " + r + " points " +
                            (args[0].equalsIgnoreCase("add") ? "added" : (args[0].equalsIgnoreCase("remove") ? "removed" : null))
                    );
                }
            }else if(args.length == 2){
                if(args[0].equalsIgnoreCase("get")) {
                    if (user.getId().equals(args[1])) {
                        event.reply(user.getName() + " has " + MDCBot.points.getUsersPoints(user) + " points.");
                    }
                }
            }else{
                event.replyError("That doesn't tickle me jones. Please use points <get:add:remove> <user_id> [amount]");
            }
        }
    }
}
