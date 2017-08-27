package mdcbot.rule;

import mdcbot.MDCBot;
import net.dv8tion.jda.core.entities.User;

public class ReadRules {

    public static void readRules(User user, String contents){
        user.openPrivateChannel().queue((privateChannel -> privateChannel.sendMessage(contents).queue()));
    }
}
