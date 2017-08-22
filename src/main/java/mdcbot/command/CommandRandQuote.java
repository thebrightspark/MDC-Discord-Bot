package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.quote.RandomQuoteParser;

public class CommandRandQuote extends CommandBase{

    private RandomQuoteParser parser;

    public CommandRandQuote() {
        super("randquote", "Gives you a random quote from a random file on a random server made by a random guy in a random room of a random house.");
        parser = new RandomQuoteParser("quotes");
    }

    @Override
    protected void doCommand(CommandEvent event) {
        event.reply(parser.getRandomQuote());
    }
}
