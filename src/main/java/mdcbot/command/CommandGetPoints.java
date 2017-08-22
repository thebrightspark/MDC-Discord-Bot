package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.MDCBot;
import mdcbot.Util;
import net.dv8tion.jda.core.entities.User;

public class CommandGetPoints extends CommandBase{

    public CommandGetPoints(){
        super("getpoints", "Displays the number of points a user has.");
    }

    @Override
    protected void doCommand(CommandEvent event) {
        String[] args = Util.splitCommandArgs(event.getArgs());
        for(User user : MDCBot.users){
            //Should we check id's or names?
        }
    }
}
