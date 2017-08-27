package mdcbot.listeners;

import mdcbot.MDCBot;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

import java.util.Random;

/**
 * Just a test listener which prints every message to the console
 */
public class MessageReceivedListener extends ListenerAdapter
{
    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        JDA jda = event.getJDA();
        //Amount of Discord events that JDA has received since the last reconnect
        long responsNo = event.getResponseNumber();

        //Event specific information

        User user = event.getAuthor();
        Message message = event.getMessage();
        MessageChannel channel = event.getChannel();    //Could be a TextChannel, PrivateChannel or Group

        String messageText = message.getContent();
        boolean isBot = user.isBot();

        //FYI in the API, Guilds are Servers
        ChannelType type = event.getChannelType();
        String guildName;
        String channelName = channel.getName();
        String userName = user.getName();

        if(type.isGuild())
        {
            guildName = event.getGuild().getName();
            Member member = event.getMember();

            //If this is a Webhook message then there is no Member associated with the User, so we use User for the name
            //If not a Webhook message, we'll use the Member's nickname if they have one. Otherwise we'll use their username
            userName = message.isWebhookMessage() ? user.getName() : member.getEffectiveName();
        }
        else
        {
            guildName = type.toString();
        }

        if(isBot) userName = "BOT:" + userName;

        MDCBot.LOG.info("(" + guildName + ")[" + channelName + "]<" + userName + ">: " + messageText);


        if(messageText.equals("hello"))
            channel.sendMessage("Hello " + user.getAsMention() + "!").queue();
        else if(messageText.equals("roll"))
        {
            int roll = new Random().nextInt(6) + 1;
            channel.sendMessage("Your roll: " + roll).queue(message1 ->
            {
                if(roll < 3)
                    channel.sendMessage("Ohh that was a bit of a low roll...").queue();
            });
        }
    }
}
