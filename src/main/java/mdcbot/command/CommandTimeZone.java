package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.utils.Util;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;

public class CommandTimeZone extends CommandBase
{
    private static final ZoneId UTC = ZoneId.of("UTC");
    private static final String ZONE_IDS;

    private static final DateTimeFormatter timeFormat12 = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(ChronoField.HOUR_OF_AMPM)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendLiteral(' ')
            .optionalEnd()
            .optionalStart()
            .appendText(ChronoField.AMPM_OF_DAY)
            .optionalStart()
            .appendLiteral(" [")
            .appendZoneRegionId()
            .appendLiteral(']')
            .toFormatter();

    private static final DateTimeFormatter timeFormat24 = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .appendValue(ChronoField.HOUR_OF_DAY)
            .appendLiteral(':')
            .appendValue(ChronoField.MINUTE_OF_HOUR, 2)
            .optionalStart()
            .appendLiteral(':')
            .appendValue(ChronoField.SECOND_OF_MINUTE, 2)
            .optionalStart()
            .appendLiteral(" [")
            .appendZoneRegionId()
            .appendLiteral(']')
            .toFormatter();

    static
    {
        List<String> zones = new ArrayList<>(ZoneId.SHORT_IDS.size());
        zones.addAll(ZoneId.SHORT_IDS.keySet());
        zones.add("UTC");
        zones.add("GMT");
        zones.sort(String::compareToIgnoreCase);
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < zones.size(); i++)
        {
            if(i > 0) sb.append(", ");
            sb.append(zones.get(i));
        }
        ZONE_IDS = sb.toString();
    }

    public CommandTimeZone()
    {
        super("timeZone", "Convert from one time zone to another",
                "Current UTC time to zone: <zoneTo>",
                        "Current time from zone to zone: <zoneFrom> <zoneTo>",
                        "Time from UTC to zone: <time> <zoneTo>",
                        "Time from zone to zone: <time> <zoneFrom> <zoneTo>");
        aliases = new String[] {"tz"};
    }

    private ZoneId toTimeZone(String timeZone)
    {
        try
        {
            return ZoneId.of(timeZone);
        }
        catch(DateTimeException e)
        {
            return null;
        }
    }

    private ZonedDateTime convertTime(ZoneId zoneTo)
    {
        return convertTime(UTC, zoneTo);
    }

    private ZonedDateTime convertTime(ZoneId zoneFrom, ZoneId zoneTo)
    {
        return convertTime(LocalDateTime.now(), zoneFrom, zoneTo);
    }

    private ZonedDateTime convertTime(LocalDateTime timeFrom, ZoneId zoneTo)
    {
        return convertTime(timeFrom, UTC, zoneTo);
    }

    private ZonedDateTime convertTime(LocalDateTime time, ZoneId zoneFrom, ZoneId zoneTo)
    {
        ZonedDateTime timeFrom = ZonedDateTime.of(time, zoneFrom);
        return timeFrom.withZoneSameInstant(zoneTo);
    }

    @Override
    protected void doCommand(CommandEvent event)
    {
        String[] args = Util.splitCommandArgs(event.getArgs());
        LocalDateTime timeFrom = null;
        ZonedDateTime timeResult = null;
        ZoneId zoneFrom = null, zoneTo = null;
        switch(args.length)
        {
            case 0:
                fail(event, getUsageEmbed(event.getGuild()));
                break;
            case 1:
                if(args[0].equalsIgnoreCase("zones"))
                {
                    //zones
                    reply(event, "Valid time zones:", ZONE_IDS);
                }
                else
                {
                    //<zoneTo>
                    zoneTo = toTimeZone(args[0]);
                    if(zoneTo == null)
                    {
                        fail(event, "'%s' is not a valid time zone", args[0]);
                        break;
                    }

                    timeResult = convertTime(zoneTo);
                }
                break;
            case 2:
                //Try get the time from arg 0
                try
                {
                    if(args[0].toLowerCase().endsWith("am") || args[0].toLowerCase().endsWith("pm"))
                        timeFrom = LocalDateTime.from(timeFormat12.parse(args[0]));
                    else
                        timeFrom = LocalDateTime.from(timeFormat24.parse(args[0]));
                }
                catch(DateTimeParseException e)
                {
                    //Time can't be parsed, so is either a time zone or a badly formatted time
                }

                if(timeFrom == null)
                {
                    //<zoneFrom> <zoneTo>
                    zoneFrom = toTimeZone(args[0]);
                    if(zoneFrom == null)
                    {
                        fail(event, "'%s' is not a valid time zone or a time matching the pattern '%s'", args[0], timeFormat24.toString());
                        return;
                    }
                    zoneTo = toTimeZone(args[1]);
                    if(zoneTo == null)
                    {
                        fail(event, "'%s' is not a valid time zone", args[1]);
                        return;
                    }

                    timeResult = convertTime(zoneFrom, zoneTo);
                }
                else
                {
                    //<time> <zoneTo>
                    zoneTo = toTimeZone(args[1]);
                    if(zoneTo == null)
                    {
                        fail(event, "'%s' is not a valid time zone", args[1]);
                        break;
                    }

                    timeResult = convertTime(timeFrom, zoneTo);
                }
                break;
            case 3:
                //Try get the time from arg 0
                try
                {
                    boolean arg1IsAmPm = args[1].toLowerCase().equalsIgnoreCase("am") || args[1].toLowerCase().equalsIgnoreCase("pm");
                    if(arg1IsAmPm)
                        args[0] += args[1];

                    if(args[0].toLowerCase().endsWith("am") || args[0].toLowerCase().endsWith("pm"))
                        timeFrom = LocalDateTime.from(timeFormat12.parse(args[0]));
                    else
                        timeFrom = LocalDateTime.from(timeFormat24.parse(args[0]));

                    if(arg1IsAmPm)
                    {
                        //Remove the argument with just "am" or "pm"
                        int index = 0;
                        String[] newArgs = new String[args.length - 1];
                        for(int i = 0; i < args.length; i++)
                            if(i != 1)
                                newArgs[index++] = args[i];
                        args = newArgs;
                    }
                }
                catch(DateTimeParseException e)
                {
                    //Time can't be parsed
                    fail(event, "The time '%s' can't be parsed using the format '%s'", args[0], timeFormat24.toString());
                    return;
                }

                //<zoneFrom> <zoneTo>
                //or
                //<time> <zoneTo>
                //or
                //<time> <zoneFrom> <zoneTo>

                boolean is2ParamsNow = args.length == 2;
                int zoneFromIndex = is2ParamsNow ? 0 : 1;
                int zoneToIndex = is2ParamsNow ? 1 : 2;

                if(timeFrom == null || !is2ParamsNow)
                {
                    zoneFrom = toTimeZone(args[zoneFromIndex]);
                    if(zoneFrom == null)
                    {
                        fail(event, "'%s' is not a valid time zone", args[zoneFromIndex]);
                        return;
                    }
                }
                zoneTo = toTimeZone(args[zoneToIndex]);
                if(zoneTo == null)
                {
                    fail(event, "'%s' is not a valid time zone", args[zoneFromIndex]);
                    return;
                }

                if(is2ParamsNow)
                {
                    if(timeFrom == null)
                        //<zoneFrom> <zoneTo>
                        timeResult = convertTime(zoneFrom, zoneTo);
                    else
                        //<time> <zoneTo>
                        timeResult = convertTime(timeFrom, zoneTo);
                }
                else
                    //<time> <zoneFrom> <zoneTo>
                    timeResult = convertTime(timeFrom, zoneFrom, zoneTo);
                break;
            case 4:
                if(!args[1].toLowerCase().equalsIgnoreCase("am") && !args[1].toLowerCase().equalsIgnoreCase("pm"))
                {
                    fail(event, "The time '%s' can't be parsed using the format '%s'", args[0] + " " + args[0], timeFormat24.toString());
                    return;
                }
                else
                {
                    //Try get the time from arg 0
                    try
                    {
                        args[0] += args[1];
                        //Remove the argument with just "am" or "pm"
                        int index = 0;
                        String[] newArgs = new String[args.length - 1];
                        for(int i = 0; i < args.length; i++)
                            if(i != 1)
                                newArgs[index++] = args[i];
                        args = newArgs;

                        timeFrom = LocalDateTime.from(timeFormat12.parse(args[0]));
                    }
                    catch(DateTimeParseException e)
                    {
                        //Time can't be parsed
                        fail(event, "The time '%s' can't be parsed using the format '%s'", args[0], timeFormat24.toString());
                        return;
                    }
                }

                //<time> <zoneFrom> <zoneTo>

                zoneFrom = toTimeZone(args[1]);
                if(zoneFrom == null)
                {
                    fail(event, "'%s' is not a valid time zone or a time matching the pattern '%s'", args[0], timeFormat24.toString());
                    return;
                }
                zoneTo = toTimeZone(args[2]);
                if(zoneTo == null)
                {
                    fail(event, "'%s' is not a valid time zone", args[1]);
                    return;
                }

                timeResult = convertTime(timeFrom, zoneFrom, zoneTo);
            default:
                //Invalid input

        }
        if(timeResult != null)
            reply(event, timeFormat12.format(timeResult));
    }
}
