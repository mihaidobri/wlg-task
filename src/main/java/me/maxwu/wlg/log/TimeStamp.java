package me.maxwu.wlg.log;

/**
 * Formatter Utility to provide timestamp.
 * It is introduced to support screenshot naming.
 */
public class TimeStamp {
    public static String getTs(){
        return new java.text.SimpleDateFormat(
            "yyyy-MM-dd-HH_mm_ss").format(new java.util.Date()
        );
    }
}
