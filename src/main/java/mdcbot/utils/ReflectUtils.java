package mdcbot.utils;

import mdcbot.ILoggable;
import mdcbot.LogLevel;
import mdcbot.MDCBot;
import mdcbot.debug.EnumMacros;
import mdcbot.debug.IDebuggable;
import org.reflections.Reflections;

import java.io.File;
import java.lang.reflect.*;
import java.util.Set;

import static mdcbot.debug.EnumMacros.DEBUG_ALL;

public class ReflectUtils implements ILoggable{
    public static <T> Constructor<T> getConstructor(Class<T> declaringClass, Class<?>...types){
        try {
            return declaringClass.getDeclaredConstructor(types);
        }catch(NoSuchMethodException e){
            e.printStackTrace();
        }
        return null;
    }

    public static Class<?> getDeclaringClass(Object o){
        if(o instanceof Method) {
            Method meth = (Method) o;
            return meth.getDeclaringClass();
        }else if(o instanceof Field){
            Field f = (Field) o;
            return f.getDeclaringClass();
        }else if(o instanceof Constructor){
            Constructor c = (Constructor)o;
            return c.getDeclaringClass();
        }else{
            throw new IllegalArgumentException("Object is not a method, constructor, or field object.");
        }
    }

    public static void getDebugMethods(Method methodToInvoke, EnumMacros debugMode, Class<?> declaringClass, Class[] conParamType, Object[] instanceParams){
        try {
            Object o = declaringClass.getDeclaredConstructor(conParamType).newInstance(instanceParams);
            methodToInvoke.invoke(o, debugMode);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }catch(InstantiationException e){
            e.printStackTrace();
            StaticLoggerConverter.convertFromStatic(LogLevel.ERROR, e.getMessage());
        }
    }
}
