package mdcbot.listeners;

import mdcbot.Config;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

public class Listener extends ListenerAdapter
{
    @Override
    public void onShutdown(ShutdownEvent event)
    {
        Config.save();
    }
}
