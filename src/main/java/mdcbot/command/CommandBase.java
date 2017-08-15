package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.Command;

public abstract class CommandBase extends Command
{
    public CommandBase(String name, String help)
    {
        this.name = name;
        this.help = help;
    }
}
