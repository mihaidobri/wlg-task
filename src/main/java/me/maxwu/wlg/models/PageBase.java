package me.maxwu.wlg.pages;


import java.util.concurrent.TimeUnit;
import me.maxwu.wlg.log.TimeStamp;
import me.maxwu.wlg.selenium.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;


import javax.imageio.ImageIO;
import java.io.File;

public class PageBase implements Snapable {
    WebDriver driver = null;
    private static Logger logger = LoggerFactory.getLogger(PageBase.class.getName());
    private int driverHashCode;
    // By default, check URL against HTTPs only.
    private String urlRegEx = "^https?:////";
    // No default title check with wildcard.
    private String titleRegEx = ".*";

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

        // AShot screenshot on full page.
       /* try {
            ImageIO.write(
                new AShot()
                .shootingStrategy(ShootingStrategies.viewportPasting(1000))
                .takeScreenshot(driver).getImage(),
                "png",
                new File( pathName)
            );
        }catch (Exception e){
            logger.error("Error on saving snapshot with AShot: " + e);
        }*/
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

    public boolean checkUrl(){
        return checkUrl(urlRegEx);
    }

    public boolean checkTitle(){
        return checkTitle(titleRegEx);
    }

    public PageBase get(String url){
        logger.debug("Driver #" + driverHashCode + " visits \"" + url + "\"");
        driver.get(url);
        return this;
    }

    // Refactored from RepaymentsCalTest.
    // Zoom out 10%.
    public void zoomOut(){
        driver.findElement(By.tagName("html")).sendKeys(
            Keys.chord(DriverFactory.isMacOs()? Keys.COMMAND : Keys.CONTROL, Keys.SUBTRACT)
        );
    }

    // Zoom in 10%.
    public void zoomIn(){
        driver.findElement(By.tagName("html")).sendKeys(
            Keys.chord(DriverFactory.isMacOs()? Keys.COMMAND : Keys.CONTROL, Keys.ADD)
        );
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

    // Use with caution on global effects. Pick up explicit wait on slow elements.
    public  void setImplicitWait(long ms){
        driver.manage().timeouts().implicitlyWait(1000, TimeUnit.MILLISECONDS);
        return;
    }

    public void quit(){
        DriverFactory.quitDriver(driver);
    }

    @Override
    protected void finalize(){
        quit();
    }

}
