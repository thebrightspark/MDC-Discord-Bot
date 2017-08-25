package mdcbot.io;

import mdcbot.MDCBot;
import mdcbot.Util;

import java.io.*;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private File file;
    private File dir;
    private FileReader fr;
    private BufferedReader br;
    private FileWriter fw;
    private BufferedWriter bw;

    public FileManager(File dir, File file) {
        try {
            this.file = file;
            this.dir = dir;
            if (dir.mkdir() || dir.isDirectory()) {
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
                fw = new FileWriter(file);
                bw = new BufferedWriter(fw);
                this.bw.write(contents);
            }catch(IOException e){
                MDCBot.LOG.trace(e.getMessage(), e.getCause());
            }finally{
                try{
                    this.bw.close();
                    this.fw.close();
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
            fr = new FileReader(file);
            br = new BufferedReader(fr);
            while (this.br.readLine() != null) {
                String s = this.br.readLine();
                sa.add(s);
            }
        }catch(IOException e){
            Util.error(e.getMessage(), e.getCause());
        }finally {
            try {
                this.fr.close();
                this.br.close();
            }catch(IOException e){
                Util.error(e.getMessage(), e.getCause());
            }
        }
        return sa;
    }
}
