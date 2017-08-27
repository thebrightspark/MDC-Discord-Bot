package mdcbot.listeners;

import mdcbot.MDCBot;
import mdcbot.io.FileManager;
import mdcbot.rule.ReadRules;
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
public class FileChangeListener {
    private File file;
    public long lastModified;

    public FileChangeListener(File file){
        this.file = file;
    }

    public void watchFileForChanges(){
        ScheduledExecutorService ses = new ScheduledThreadPoolExecutor(1);
        ses.scheduleAtFixedRate(()->{
            if(this.file.lastModified() != this.lastModified) {
                FileManager fm = new FileManager(MDCBot.RULES_DIR, this.file);
                for (User user : MDCBot.users) {
                    String contents = "";
                    for (String s : fm.readFromFile()) {
                        contents = contents.concat(s).concat("\n");
                    }
                    ReadRules.readRules(user, contents);
                }
            }
        }, 10, 30, TimeUnit.SECONDS);
    }
}
