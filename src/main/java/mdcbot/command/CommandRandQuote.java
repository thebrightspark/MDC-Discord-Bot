package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.MDCBot;
import mdcbot.quote.RandomQuoteParser;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Message;

public class CommandRandQuote extends CommandBase{

    private RandomQuoteParser parser;

    public CommandRandQuote() {
        super("randquote", "Gives you a random quote from a random file on a random server made by a random guy in a random room of a random house.");
        parser = new RandomQuoteParser("quotes");
    }

    @Override
    protected void doCommand(CommandEvent event) {
        MessageBuilder message = new MessageBuilder();
        event.reply(message.appendCodeBlock(parser.getRandomQuote(), "").getStringBuilder().toString() + "\n-" + parser.getAuthor());
        message.build();
    }
}
