package mdcbot.utils;

import mdcbot.ILoggable;
import mdcbot.LogLevel;

public class StaticLoggerConverter implements ILoggable {
    private static String staticMessage = "";
    private static Object[] staticParams;
    private static LogLevel staticLevel;

    private String message = staticMessage;
    private Object[] params = staticParams;
    private LogLevel level = staticLevel;

    private StaticLoggerConverter(){
        logUnstatic();
    }

    public static void convertFromStatic(LogLevel level, String message, Object...params){
        staticMessage = message;
        staticParams = params;
        staticLevel = level;
    }

    private void logUnstatic(){
        log(this.level, this.message, this.params);
    }
}
