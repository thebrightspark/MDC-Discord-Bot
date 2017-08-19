package mdcbot;

import org.apache.log4j.Level;

import java.awt.Color;

public enum LogLevel
{
    INFO(Level.INFO, Color.CYAN),
    ERROR(Level.ERROR, Color.RED),
    WARN(Level.WARN, Color.ORANGE),
    DEBUG(Level.DEBUG, Color.YELLOW);

    public Level log4jLevel;
    public Color colour;

    LogLevel(Level log4jLevel, Color colour)
    {
        this.log4jLevel = log4jLevel;
        this.colour = colour;
    }

    @Override
    public String toString()
    {
        String name = name().toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }
}
