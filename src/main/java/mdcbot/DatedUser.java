package mdcbot;

import net.dv8tion.jda.core.entities.User;

import java.util.Date;

public class DatedUser
{
    private User user;
    private Date date;

    public DatedUser(String fromString)
    {
        String[] split = fromString.split(",");
        if(split.length != 2)
            throw new IllegalArgumentException("String must be two longs separated by a comma");
        long userL, dateL;
        try
        {
            userL = Long.parseLong(split[0]);
            dateL = Long.parseLong(split[1]);
        }
        catch(NumberFormatException e)
        {
            Util.error("Couldn't parse to DatedUser: " + fromString);
            e.printStackTrace();
            return;
        }
        user = MDCBot.jda.getUserById(userL);
        date = new Date(dateL);
    }

    public DatedUser(User user, Date date)
    {
        this.user = user;
        this.date = date;
    }

    public User getUser()
    {
        return user;
    }

    public Date getDate()
    {
        return date;
    }

    @Override
    public String toString()
    {
        return user.getIdLong() + "," + date.getTime();
    }
}
