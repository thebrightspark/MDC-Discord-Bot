package mdcbot.points;

import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;

public class UserPoints {

    private static Map<User, Integer> userPoints = new HashMap<>();

    public void addOrSubPoints(User user, int points, boolean deduct){
        int priorPoints = userPoints.get(user);
        int newPoints = !deduct ? (priorPoints + points) : (priorPoints - points);
        userPoints.remove(user);
        userPoints.put(user, newPoints);
    }

    public int getUsersPoints(User user){
        return userPoints.get(user);
    }
}
