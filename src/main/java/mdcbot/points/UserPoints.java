package mdcbot.points;

import mdcbot.MDCBot;
import mdcbot.io.UserPointsIO;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.ReadyEvent;

import java.util.HashMap;
import java.util.Map;

public class UserPoints {

    private static Map<User, Integer> userPoints;

    public static void init(ReadyEvent event){
        userPoints = UserPointsIO.load(event);
    }

    public static void addOrSubPoints(User user, int points, boolean deduct){
        int priorPoints = userPoints.getOrDefault(user, 0);
        if(user.isBot())
            return;
        if(userPoints.containsKey(user)) {
            int math = !deduct ? priorPoints + points : priorPoints - points;
            int newPoints = Math.max(0, math);
            userPoints.remove(user);
            userPoints.put(user, newPoints);
            UserPointsIO.init();
        }else{
            userPoints.put(user, points);
            UserPointsIO.init();
        }
        checkIfZero(user);
    }

    public static int getUsersPoints(User user){
        return userPoints.get(user);
    }

    private static void checkIfZero(User user){
        if(userPoints.get(user) == 0){
            for(Guild g : MDCBot.jda.getGuilds()){
                user.openPrivateChannel().queue((channel)->{
                    channel.sendMessage("Thanks for helping me test this bot!\nCome on back in: https://discord.gg/xBtUWWh").queue();
                });
                g.getController().ban(user, 5, "Points reached 0.").queue();
                g.getController().unban(user).queue();
            }
        }
    }

    public static Map<User, Integer> getUserPointsMap(){
        return userPoints;
    }
}
