package me.maxwu.wlg.log;

import java.io.PrintStream;

public class ColorPrint {
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    public static void println(String str, String col){
        System.out.println(col + str + ANSI_RESET);
    }

    public static String red(String st){
        return ANSI_RED + st + ANSI_RESET;
    }
    public static void println_red(PrintStream ps, String st){
        ps.println(red(st));
        ps.flush();
    }
    public static void println_red(String st){
        println_red(System.out, st);
    }

    public static String green(String st){
        return ANSI_GREEN + st + ANSI_RESET;
    }
    public static void println_green(PrintStream ps, String st){
        ps.println(green(st ));
        ps.flush();
    }
    public static void println_green(String st){
        println_green(System.out, st);
    }

    public static String blue(String st){
        return ANSI_BLUE + st + ANSI_RESET;
    }
    public static void println_blue(PrintStream ps, String st){
        ps.println(blue(st));
        ps.flush();
    }
    public static void println_blue(String st){
        println_blue(System.out, st);
    }
}
