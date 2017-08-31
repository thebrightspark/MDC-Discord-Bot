package mdcbot.listeners;

import mdcbot.MDCBot;
import mdcbot.io.FileManager;
import net.dv8tion.jda.core.entities.User;

import java.io.File;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * This is not a JDA listener, but a listener to listen for a file that is changed.
 * Right now it's just used for watching the rule.txt file for changes
 * But eventually we can adjust it to watch any file
 */
public class FileChangeListener implements Runnable
{
    private File file;
    private long lastModified;
    private FileManager fm;

    public static void watchFileForChanges(File fileToWatch){
        ScheduledExecutorService ses = new ScheduledThreadPoolExecutor(1);
        ses.scheduleAtFixedRate(new FileChangeListener(fileToWatch), 10, 30, TimeUnit.SECONDS);
    }

    public FileChangeListener(File fileToWatch){
        file = fileToWatch;
        lastModified = fileToWatch.lastModified();
        fm = new FileManager(MDCBot.RULES_DIR, file);
    }

    @Override
    public void run(){
        if(this.file.lastModified() != this.lastModified) {
            StringBuilder sb = new StringBuilder();
            fm.readFromFile().forEach(sb::append);
            String contents = sb.toString();
            for (User user : MDCBot.users) {
                user.openPrivateChannel().queue((privateChannel -> privateChannel.sendMessage(contents).queue()));
            }
        }
    }
}
