package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;

public class CommandHello extends CommandBase
{
    public CommandHello()
    {
        super("hello", "Says hello to the sender");
    }

    @Override
    protected void doCommand(CommandEvent event)
    {
        event.reply("Hello " + event.getAuthor().getAsMention() + "!");
    }
}
