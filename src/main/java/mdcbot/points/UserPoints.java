package mdcbot.points;

import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;

public class UserPoints {

    private static Map<User, Integer> userPoints = new HashMap<>();

    public static void addOrSubPoints(User user, int points, boolean deduct){
        int priorPoints = userPoints.getOrDefault(user, 0);
        if(userPoints.containsKey(user)) {
            int newPoints = !deduct ? priorPoints + points : (deduct ? (priorPoints - points) : 0);
            userPoints.remove(user);
            userPoints.put(user, newPoints);
        }else{
            userPoints.put(user, points);
        }
    }

    public static int getUsersPoints(User user){
        return userPoints.get(user);
    }
}
