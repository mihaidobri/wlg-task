package me.maxwu.wlg.models;

import java.util.concurrent.TimeUnit;
import me.maxwu.wlg.log.TimeStamp;
import me.maxwu.wlg.selenium.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import javax.imageio.ImageIO;
import java.io.File;

/**
 * Base type for page object model.
 * Page Base provides common browser URL and title check, as well as the supporting methods
 *     over web driver. The common supporting methods are screenshot, zooming and web driver
 *     creation/quit procedures.
 *
 * INFO: Specific page models shall derive from this type.
 * INFO: For redirected pages, it is suggested to pick URL check with fluent wait.
 * INFO: Fluent wait is only on URL check for now with PageBase sample implementation.
 */
public class PageBase implements ISnapable, IMoveIntoAndActable {
    protected WebDriver driver = null;
    private static Logger logger = LoggerFactory.getLogger(PageBase.class.getName());
    private int driverHashCode;
    // By default, check URL against HTTPs only.
    private String urlRegEx = "^https?:////";
    // No default title check with wildcard.
    private String titleRegEx = ".*";

    /**
     * The implementation on page screenshot based on web driver.
     * As for phantomjs, it is full page screenshot. However, customization and testing works
     *     are not completed on other browsers.
     * INFO: To update to a full page screenshot supports before official testing application.
     * @param caseName The test case name used in screenshot png filename.
     */
    public void saveScreenShot(String caseName) {
        String pathName = TimeStamp.getTs() + "_" + caseName + ".png";

        if ((driver==null) || (DriverFactory.hasQuit(driver))){
            logger.error("Error on saving snap with null or quit driver #" + (driver!=null? driver.hashCode(): "NULL"));
            return;
        }

        // Screenshot with web driver.
        File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
        try {
            FileUtils.copyFile(scrFile, new File(pathName));
        }catch (Exception e){
            logger.error("Error in saving snapshot with driver: " + e);
        }
    }

    public void saveScreenShot(String name, WebElement we) {
        String pathName = TimeStamp.getTs() + "_" + name + ".png";
        try {
            ImageIO.write(
                new AShot()
                    .takeScreenshot(driver, we).getImage(),
                "png",
                new File( pathName)
            );
        }catch (Exception e){
            logger.error("Error on saving snapshot for \"" + we + "\" on driver #"
                + driverHashCode + "with AShot: " + e);
        }
    }

    public boolean checkUrl(String urlPattern){
        String currentUrl = driver.getCurrentUrl();
        if (!currentUrl.matches(urlPattern)){
            saveScreenShot("checkUrl");
            throw new WrongPageException("Wrong URL with Pattern \"" + urlPattern + "\"",
                driver);
        }else{
            logger.debug("Driver #" + driverHashCode + " passed url check");
            return true;
        }

    }

    public boolean checkUrl(){
        return checkUrl(urlRegEx);
    }

    public boolean checkUrlWait(int seconds){
        return checkUrlWait(urlRegEx, seconds);
    }

    public boolean checkUrlWait(String urlPattern, int seconds){
        new FluentWait<WebDriver>(driver)
            .pollingEvery(250, TimeUnit.MILLISECONDS)
            .withTimeout(seconds, TimeUnit.SECONDS)
            .ignoring(WrongPageException.class)
            .until((dr) -> {
                String url = driver.getCurrentUrl();
                logger.debug("checking URL ");
                return (url.matches(urlPattern));
            });
        return true;
    }

    public boolean checkTitle(String titlePattern){
        String currentTitle = driver.getTitle();
        if (!currentTitle.matches(titlePattern)){
            saveScreenShot("checkTitle");
            throw new WrongPageException("Wrong Title against Pattern \"" + titlePattern + "\"",
                driver);
        }else{
            logger.debug("Driver #" + driverHashCode + " passed title check");
            return true;
        }
    }

    // In current test practices, title is checked after URL. However, it is not a design contract.
    // For redirected page jump and slow network, a title check with fluent wait can be grown here.
    public boolean checkTitle(){
        return checkTitle(titleRegEx);
    }

    public PageBase get(String url){
        logger.debug("Driver #" + driverHashCode + " visits \"" + url + "\"");
        driver.get(url);
        return this;
    }

    public void scrollIntoAndClick(WebElement we, String info){
        scrollIntoAndClick(driver, we);
        if ((info != null) && (!info.isEmpty())) {
            logger.debug("Scroll into and click on:" + info);
        }
    }

    public void moveToAndClick(WebElement we, String info){
        moveToAndClick(driver, we);
        if ((info != null) && (!info.isEmpty())) {
            logger.debug("Move to and click on:" + info);
        }
    }

    // Zoom out 10%.
    public PageBase zoomOut(){
        driver.findElement(By.tagName("html")).sendKeys(
            Keys.chord(DriverFactory.isMacOs()? Keys.COMMAND : Keys.CONTROL, Keys.SUBTRACT)
        );
        return this;
    }

    // Zoom in 10%.
    public PageBase zoomIn(){
        driver.findElement(By.tagName("html")).sendKeys(
            Keys.chord(DriverFactory.isMacOs()? Keys.COMMAND : Keys.CONTROL, Keys.ADD)
        );
        return this;
    }

    public PageBase(WebDriver dr, String urlPattern, String titlePattern){
        this(dr);
        urlRegEx = urlPattern;
        titleRegEx = titlePattern;
    }

    public PageBase(WebDriver driver) {
        logger.debug("PageBase initialized with Driver #" + driver.hashCode());
        this.driver = driver;
        this.driverHashCode = driver.hashCode();
    }

    /**
     * Use with caution on global effects. Pick up explicit wait on slow elements.
     * CAUTION: Only for debug using to quickly identify if reaction time matters in issues.
     * @param ms wait time in millis second.
     */
    public  void setImplicitWait(long ms){
        logger.warn("CAUTION: ImplicitWait set to " + ms + "ms.");
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
    }

    public void quit(){
        DriverFactory.quitDriver(driver);
    }

    @Override
    protected void finalize(){
        quit();
    }

}
