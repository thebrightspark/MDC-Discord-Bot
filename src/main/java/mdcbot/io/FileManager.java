package mdcbot.io;

import mdcbot.ILoggable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FileManager implements ILoggable
{
    private File file;
    private BufferedReader br;
    private BufferedWriter bw;

    public FileManager(File file){
        try {
            this.file = file;
            if (file.mkdir() || file.exists()) {
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        error("Could not create new file: " + file);
                    } else {
                        debug("Created new file: " + file);
                    }
                }
            } else {
                error("Cannot create parent directory.");
            }
        }catch(IOException e){
            error(e.getMessage());
        }
    }

    public <T> void writeToFile(T t){
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
        }else{
            error("Could not write empty string to file!");
        }
    }

    public List<String> readFromFile(){
        try{
            br = new BufferedReader(new FileReader(this.file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return br.lines().collect(Collectors.toList());
    }

    public File getFile(){
        return file;
    }
}
