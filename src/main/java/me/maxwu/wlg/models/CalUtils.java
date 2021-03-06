package me.maxwu.wlg.models;

import java.text.NumberFormat;
import java.util.Locale;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class holds general and static utilities for calculator testing.
 */
public class CalUtils {
    private static Logger logger = LoggerFactory.getLogger(CalUtils.class.getName());


    /**
     * Return formatted string to present the digital value as displayed on Web based on UK locale.
     * @param num Number in int.
     * @return Formatted String value.
     */
    public static String formatNumber(int num){
        return NumberFormat.getNumberInstance(Locale.UK).format(num);
    }

    /**
     * Return Whether the element is visible within calculators.
     */
    public static boolean isVisibleOnCalculator(WebElement we){
        String style = we.getAttribute("style");

        boolean isVisibleStyle = (style == null)
            || (style.isEmpty())
            || (!style.matches("display\\s*:\\s*none[\\s|;]"));
        boolean isDisplayed = we.isDisplayed();

        logger.trace("Attribute style='" + style + "', isVisibleStyle=" + isVisibleStyle);
        return (isVisibleStyle && isDisplayed);
    }

    /**
     * CAUTION: The absolute wait action will sleep for interval unconditionally.
     *   It is not suggested to use in loop or periodical check. For debug using,
     *   it is recommended to replace with fluent or explicit wait before committing.
     * @param millis The internal to sleep.
     */
    public static void waitInMs(int millis){
        try{
            Thread.sleep(millis);
        }catch (Exception e){
            logger.error("Error in sleep:", e);
        }
    }

}
