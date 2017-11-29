package me.noahp78.efc.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by noahp78 on 29-11-2017.
 */
public class Console {
    private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
    private static void log(String message){
        System.out.println(message);
    }

    private static void printWithTime(String message){
        log("["+dateFormat.format(new Date()) + "] " + message);
    }
    public static void debug(String message){
        printWithTime("[DEBUG] "+ message);
    }
    public static void d(String message){
        debug(message);
    }
}
