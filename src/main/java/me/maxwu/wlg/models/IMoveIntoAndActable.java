package me.maxwu.wlg.models;

import java.util.concurrent.TimeUnit;
import org.openqa.selenium.ElementNotVisibleException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The interface with default implementations to move focus or scroll browser to the
 *     given element and perform action on it in following.
 */
public interface IMoveIntoAndActable {
    Logger logger = LoggerFactory.getLogger(IMoveIntoAndActable.class.getName());
    int pollingCheckVisibilityMillis = 500;
    int pollingTimeOutSecond = 2;

    default void fluentWaitUntilVisible(WebDriver driver, WebElement we){
        new FluentWait<>(driver)
            .pollingEvery(pollingCheckVisibilityMillis, TimeUnit.MILLISECONDS)
            .withTimeout(pollingTimeOutSecond, TimeUnit.SECONDS)
            .ignoring(NoSuchElementException.class)
            .ignoring(ElementNotVisibleException.class)
            .until((dr) -> {
                logger.debug("checking visibility..");
                return CalUtils.isVisibleOnCalculator(we);
            });
    }

    default void scrollIntoAndClick(WebDriver driver, WebElement we){
        fluentWaitUntilVisible(driver, we);
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", we);
        // Originally the action is : we.click();
        // With ChromeDriver there is a pre-check issue on if the element is clickable.
        // Therefore, JSexecutor is introduced to work around this check.
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", we);
    }

    default void moveToAndClick(WebDriver driver, WebElement we){
        fluentWaitUntilVisible(driver, we);
        new Actions(driver).moveToElement(we).click().build().perform();
        we.click();
    }

}
