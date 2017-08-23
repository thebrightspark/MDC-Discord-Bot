package mdcbot.points;

import mdcbot.MDCBot;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;

public class UserPoints {

    private static Map<User, Integer> userPoints = new HashMap<>();

    public static void addOrSubPoints(User user, int points, boolean deduct){
        int priorPoints = userPoints.getOrDefault(user, 0);
        if(userPoints.containsKey(user)) {
            int math = !deduct ? priorPoints + points : (deduct ? (priorPoints - points) : 0);
            int newPoints = Math.max(0, math);
            userPoints.remove(user);
            userPoints.put(user, newPoints);
        }else{
            userPoints.put(user, points);
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
                    channel.sendMessage("Thanks for helping me test this bot!").queue();
                    channel.sendMessage("Come on back in: https://discord.gg/2KYFect").queue();
                });
                g.getController().ban(user.getId(), 5, "Points reached 0.").queue();
                g.getController().unban(user.getId()).queue();
            }
        }
    }
}
