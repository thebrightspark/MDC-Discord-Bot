package mdcbot.io;

import mdcbot.MDCBot;
import mdcbot.Util;
import mdcbot.points.UserPoints;
import net.dv8tion.jda.core.entities.User;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserPointsIO {

    private static final FileManager fm = new FileManager(MDCBot.SAVES_DIR, MDCBot.USER_POINTS_FILE);

    public static void init(){
        String s = "";
        for(Map.Entry<User, Integer> entry : UserPoints.getUserPointsMap().entrySet()) {
            s = s.concat(entry.getKey().getName() + "(" + entry.getKey().getId() + ")" + ": " + entry.getValue() + "\n");
        }
        fm.writeToFile(s);
    }

    public static Map<User, Integer> load(){
        List<String> list = fm.readFromFile();
        Map<User, Integer> userPoints = new HashMap<>();
        for(String s : list){
            if(s.contains(":")){
                String name = s.substring(0, s.indexOf(":"));
                String spoints = s.substring(s.indexOf(":")+1, s.length());
                String sid = name.substring(name.indexOf("(")+1, name.indexOf(")")-1);
                Long id = Long.parseLong(sid);
                Integer points = Integer.parseInt(spoints.trim());
                User user = MDCBot.jda.getUserById(id);
                Util.debug("Loading points for: " + user.getName() + " - " + points);
                userPoints.put(user, points);
            }
        }
        return userPoints;
    }
}
