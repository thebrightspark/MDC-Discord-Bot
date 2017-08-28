package mdcbot.io;

import mdcbot.ILoggable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager implements ILoggable
{
    private File file;
    private BufferedReader br;
    private BufferedWriter bw;

    public FileManager(File dir, File file) {
        try {
            this.file = file;
            if (dir.mkdir() || dir.exists()) {
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        error("Could not create new file: " + file);
                    } else {
                        debug("Created new file: " + file);
                    }
                }
            } else {
                error("Cannot create directory: " + dir);
            }
        }catch(IOException e){
            error(e.getMessage());
        }
    }

    public <T> boolean writeToFile(T t){
        String contents = String.valueOf(t);
        if(!contents.isEmpty()) {
            try {
                bw = new BufferedWriter(new FileWriter(file));
                this.bw.write(contents);
            }catch(IOException e){
                error(e.getMessage(), e.getCause());
            }finally{
                try{
                    this.bw.close();
                }catch(IOException e){
                    error(e.getMessage(), e.getCause());
                }
            }
            debug(this.file.getAbsolutePath() + " has been written to.");
            return true;
        }else{
            error("Could not write empty string to file!");
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
            error(e.getMessage(), e.getCause());
        }finally {
            try {
                this.br.close();
            }catch(IOException e){
                error(e.getMessage(), e.getCause());
            }
        }
        return sa;
    }
}
