package me.maxwu.wlg.log;

/**
 *  Build colorful strings compatible to ANSI and Jenkins ANSI-Color plugin.
 */
public class ColorStr {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_BLUE = "\u001B[34m";

    public static String red(String st){
        return ANSI_RED + st + ANSI_RESET;
    }

    public static String green(String st){
        return ANSI_GREEN + st + ANSI_RESET;
    }

    public static String blue(String st){
        return ANSI_BLUE + st + ANSI_RESET;
    }
}
