package mdcbot.debug;

import mdcbot.ILoggable;

public interface IDebuggable extends ILoggable{
    void debug(EnumMacros debugMode);
}
