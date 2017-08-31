package mdcbot.command;

import com.jagrosh.jdautilities.commandclient.CommandEvent;
import mdcbot.MDCBot;
import mdcbot.Util;
import mdcbot.debug.EnumMacros;
import mdcbot.debug.IDebuggable;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.*;
import java.util.List;
import java.util.Set;

import static mdcbot.debug.EnumMacros.DEBUG_ALL;

public class CommandDebug extends CommandBase{
    public CommandDebug() {
        super("debug", "For developers only. Unlocks all the debug logging for various parts of the bot.");
    }

    @Override
    protected void doCommand(CommandEvent event) {
//        String[] args = Util.splitCommandArgs(event.getArgs());
        if(event.getArgs().isEmpty()){
            Reflections reflections = new Reflections("mdcbot");
            Set<Class<? extends IDebuggable>> debuggables = reflections.getSubTypesOf(IDebuggable.class);
            for(Class<? extends IDebuggable> debug : debuggables){
                try {
                    Method meth = debug.getMethod("debug", EnumMacros.class);
                    Class<?> clazz = meth.getDeclaringClass();
                    meth.setAccessible(true);
                    Object o = clazz.getDeclaredConstructor(File.class, File.class).newInstance(MDCBot.RULES_DIR, MDCBot.RULES_FILE);
                    meth.invoke(o, DEBUG_ALL);
                } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }catch(InstantiationException e){
                    e.printStackTrace();
                    error(event, e.getMessage());
                }
            }
        }
    }
}
