package mdcbot.io;

import mdcbot.MDCBot;
import mdcbot.Util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private File file;
    private BufferedReader br;
    private BufferedWriter bw;

    public FileManager(File dir, File file) {
        try {
            this.file = file;
            if (dir.mkdir()) {
                if (!file.exists()) {
                    if (!file.createNewFile())
                        Util.error("Could not create new file: " + file);
                    else
                        Util.debug("Created new file: " + file);
                }
            } else {
                Util.error("Cannot create directory: " + dir);
            }
        }catch(IOException e){
            Util.error(e.getMessage());
        }
    }

    public boolean writeToFile(String contents){
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
                    Util.error(e.getMessage(), e.getCause());
                }
            }
            Util.debug(this.file.getAbsolutePath() + " has been written to.");
            return true;
        }else{
            Util.error("Could not write empty string to file!");
            return false;
        }
    }

    public List<String> readFromFile(){
        List<String> sa = new ArrayList<>();
        try {
            br = new BufferedReader(new FileReader(file));
            while (this.br.readLine() != null) {
                String s = this.br.readLine();
                sa.add(s);
            }
        }catch(IOException e){
            Util.error(e.getMessage(), e.getCause());
        }finally {
            try {
                this.br.close();
            }catch(IOException e){
                Util.error(e.getMessage(), e.getCause());
            }
        }
        return sa;
    }
}
