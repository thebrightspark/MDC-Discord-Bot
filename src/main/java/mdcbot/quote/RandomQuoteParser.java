package mdcbot.quote;

import mdcbot.MDCBot;
import mdcbot.Util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class RandomQuoteParser {
    private Map<Long, String> quotes = new HashMap<>();

    public RandomQuoteParser(String path){
        loadFile(path);
    }

    private void loadFile(String path) {
        File file = new File(MDCBot.RESOURCES_DIR + "/quotes/" + path + ".txt");
        Util.debug(getClass(), "Loaded random quote file from " + file.getAbsolutePath());
        try (FileReader fr = new FileReader(file); BufferedReader br = new BufferedReader(fr)) {
            String line, l = null;
            Util.debug(getClass(), "Reading file...");
            long num = 0;
            while ((line = br.readLine()) != null) {
                num++;
                if (line.startsWith("::")) {
                    String quote = line.substring(line.indexOf("::") + 2, line.lastIndexOf("::"));
                    Util.debug(getClass(), "Loading quote into quote map: " + quote);
                    quotes.put(num, quote);
                }
            }
        } catch (IOException e) {
            Util.error(getClass(), e.getMessage());
        }
    }

    List<Integer> intlist = new ArrayList<>();
    Random random = new Random();
    String q;
    public String getRandomQuote(){
        int randint;
        do randint = random.nextInt(this.quotes.size()-1);
        while(intlist.contains(randint));
        intlist.add(randint);
        if(intlist.size() > 3)
            intlist.remove(0);
        q = quotes.get((long)randint);
        return q.substring(0, q.indexOf("-")-1);
    }

    public String getAuthor(){
        return this.q.substring(q.indexOf("-")+1, q.length());
    }
}
