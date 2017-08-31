package mdcbot.io;

import mdcbot.debug.DebugBools;
import mdcbot.debug.DebugStrings;
import mdcbot.debug.EnumMacros;
import mdcbot.debug.IDebuggable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static mdcbot.debug.EnumMacros.DEBUG_ALL;

public class FileManager implements IDebuggable
{
    private File file;
    private BufferedReader br;
    private BufferedWriter bw;

    public FileManager(File dir, File file) {
        try {
            this.file = file;
            if (dir.mkdir() || dir.exists()) {
                DebugBools.dirCreateStatus = true;
                DebugStrings.dirBeingCreated = dir.getAbsolutePath();
                if (!file.exists()) {
                    if (!file.createNewFile()) {
                        DebugBools.fileCreateStatus = true;
                        error("Could not create new file: " + file);
                    } else {
                        DebugBools.dirCreateStatus = false;
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
        DebugStrings.fileBeingProcessed = this.file.getName();
        if(!contents.isEmpty()) {
            try {
                DebugBools.fileWrite = true;
                bw = new BufferedWriter(new FileWriter(file));
                this.bw.write(contents);
            }catch(IOException e){
                error(e.getMessage(), e.getCause());
                DebugBools.fileWrite = false;
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
        DebugStrings.fileBeingProcessed = this.file.getName();
        try {
            DebugBools.fileRead = true;
            br = new BufferedReader(new FileReader(file));
            String s;
            while ((s = this.br.readLine()) != null) {
                sa.add(s);
            }
        }catch(IOException e){
            DebugBools.fileRead = false;
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

    @Override
    public void debug(EnumMacros debugMode) {
        debug("Debugging with " + (debugMode == DEBUG_ALL ? "debug_all" : "no") + " mode");
        if(debugMode == EnumMacros.DEBUG_FILES || debugMode == DEBUG_ALL){
            debug(
                    DebugBools.dirCreateStatus ?
                        DebugStrings.dirBeingCreated + " has been created, or already exists." :
                            DebugStrings.dirBeingCreated + " could not be created."
            );
            debug(
                    DebugBools.fileCreateStatus ?
                        this.file.getAbsolutePath() + " has been created or already exists." :
                            this.file.getAbsolutePath() + " could not be created."
            );
            debug(
                    DebugBools.fileRead ?
                        DebugStrings.fileBeingProcessed + " is being read." :
                            DebugBools.fileWrite ?
                                DebugStrings.fileBeingProcessed + " is being written to." :
                                    "Could not read nor write from file: " + DebugStrings.fileBeingProcessed
            );
        }
    }
}
