package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.MDCBot;
import mdcbot.debug.EnumMacros;
import mdcbot.debug.IDebuggable;
import mdcbot.utils.ReflectUtils;
import net.dv8tion.jda.core.events.Event;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.Method;
import java.util.Set;

public class CommandDebug extends CommandBase{
    public CommandDebug() {
        super("debug", "For developers only. Unlocks all the debug logging for various parts of the bot.");
    }

    @Override
    protected void doCommand(CommandEvent event) {
        if(event.getArgs().isEmpty()){
            reflectDebugMethod(event, EnumMacros.DEBUG_ALL);
        }else if(event.getArgs().equals("files")){
            reflectDebugMethod(event, EnumMacros.DEBUG_FILES);
        }else if(event.getArgs().equals("commands")){
            reflectDebugMethod(event, EnumMacros.DEBUG_COMMANDS);
        }else if(event.getArgs().equals("listeners")){
            reflectDebugMethod(event, EnumMacros.DEBUG_LISTENERS);
        }else if(event.getArgs().equals("messages")){
            reflectDebugMethod(event, EnumMacros.DEBUG_MESSAGES);
        }
    }

    private void reflectDebugMethod(CommandEvent event, EnumMacros debugMode){
        Reflections reflections = new Reflections("mdcbot");
        Set<Class<? extends IDebuggable>> debuggables = reflections.getSubTypesOf(IDebuggable.class);
        for(Class<? extends IDebuggable> debuggable : debuggables) {
            try {
                Method meth = debuggable.getMethod("debug", EnumMacros.class);
                Class<?> declaringClass = meth.getDeclaringClass();
                if(declaringClass.getName().equals("FileManager")){
                    ReflectUtils.getDebugMethods(meth, debugMode, declaringClass, new Class[]{File.class, File.class}, new Object[]{MDCBot.RULES_DIR, MDCBot.RULES_FILE});
                }
            }catch(NoSuchMethodException e){
                error(event, e.getMessage());
            }
        }
    }
}
