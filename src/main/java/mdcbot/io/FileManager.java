package mdcbot.io;

import mdcbot.MDCBot;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private Logger log = Logger.getLogger(getClass().getSimpleName());
    private File file;
    private BufferedReader br;
    private BufferedWriter bw;

    public FileManager(File dir, File file) {
        try {
            this.file = file;
            if (dir.mkdir() || dir.exists()) {
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        log.error("Could not create new file: " + file);
                    } else {
                        log.debug("Created new file: " + file);
                    }
                }
            } else {
                log.error("Cannot create directory: " + dir);
            }
        }catch(IOException e){
            log.error(e.getMessage());
        }
    }

    public <T> boolean writeToFile(T t){
        String contents = String.valueOf(t);
        if(!contents.isEmpty()) {
            try {
                bw = new BufferedWriter(new FileWriter(file));
                this.bw.write(contents);
            }catch(IOException e){
                MDCBot.LOG.trace(e.getMessage(), e.getCause());
            }finally{
                try{
                    this.bw.close();
                }catch(IOException e){
                    log.error(e.getMessage(), e.getCause());
                }
            }
            log.debug(this.file.getAbsolutePath() + " has been written to.");
            return true;
        }else{
            log.error("Could not write empty string to file!");
            return false;
        }
    }

    public List<String> readFromFile(){
        List<String> sa = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = this.br.readLine()) != null) {
                sa.add(s);
            }
        }catch(IOException e){
            log.error(e.getMessage(), e.getCause());
        }finally {
            try {
                this.br.close();
            }catch(IOException e){
                log.error(e.getMessage(), e.getCause());
            }
        }
        return sa;
    }
}
