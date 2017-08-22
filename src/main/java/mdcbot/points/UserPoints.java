package mdcbot.points;

import net.dv8tion.jda.core.entities.User;

import java.util.HashMap;
import java.util.Map;

public class UserPoints {

    private static Map<User, Integer> userPoints = new HashMap<>();
    public int remainders;

    public void addOrSubPoints(User user, int points, boolean deduct){
        int priorPoints = userPoints.getOrDefault(user, 0);
        if(userPoints.containsKey(user)) {
            if (userPoints.get(user) >= 0) {
                int newPoints = !deduct ? priorPoints + points : (deduct ? (priorPoints - points) : 0);
                if (deduct && newPoints <= 0) {
                    newPoints = 0;
                }
                if(deduct && newPoints == 0) remainders = userPoints.get(user);
                else remainders = points;
                userPoints.remove(user);
                userPoints.put(user, newPoints);
                if(!deduct) remainders = points;
            }
        }else{
            userPoints.put(user, points);
        }
    }

    public int getUsersPoints(User user){
        return userPoints.get(user);
    }
}
