package mdcbot.quote;

import mdcbot.MDCBot;
import mdcbot.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomQuoteParser {
    private List<String> quotes = new ArrayList<>();

    public RandomQuoteParser(String path){
        loadFile(path);
    }

    private void loadFile(String path) {
        File file = new File(MDCBot.RESOURCES_DIR + "/quotes/" + path + ".txt");
        Util.debug("Loaded random quote file from " + file.getAbsolutePath());
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            String line, l = null;
            Util.debug("Reading file...");
            while ((line = br.readLine()) != null) {
                if (line.startsWith("::")) {
                    String quote = line.substring(line.indexOf("::") + 2, line.lastIndexOf("::"));
                    Util.debug("Loading quote into quote list: " + quote);
                    this.quotes.add(quote);
                }
            }
        } catch (IOException e) {
            Util.error(e.getMessage());
        }
    }

    List<Integer> intlist = new ArrayList<>(3);
    Random random = new Random();
    public String getRandomQuote(){
        int randint = random.nextInt(this.quotes.size()-1);
        if(intlist.contains(randint)){
            randint = random.nextInt(this.quotes.size()-1);
        }
        intlist.add(randint);
        return this.quotes.get(randint);
    }
}
