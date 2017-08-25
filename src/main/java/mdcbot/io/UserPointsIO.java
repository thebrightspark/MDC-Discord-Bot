package mdcbot.io;

import mdcbot.MDCBot;
import mdcbot.points.UserPoints;
import net.dv8tion.jda.core.entities.User;

import java.io.IOException;
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

    /*public static List<String> load(){
        List<String> list = fm.readFromFile();
        for(String s : list){
            if(s.contains("=")){
                continue;
            }
            if(s.contains(":")){
                String name = s.substring(0, s.indexOf(":"));
                String points = s.substring(s.indexOf(":")+1, s.length());

            }
        }
    }*/
}
