package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.MDCBot;
import net.dv8tion.jda.core.entities.User;

public class CommandGetUsersList extends CommandBase {

    public CommandGetUsersList(){
        super("memberslist", "Returns a list of all the members on the server.");
    }

    @Override
    protected void doCommand(CommandEvent commandEvent) {
        String list = "";
        for(User user : commandEvent.getJDA().getUsers()){
            list = list.concat((!list.equals("") ? ", " : "") + user.getName());
        }

        commandEvent.reply(list);
    }
}
