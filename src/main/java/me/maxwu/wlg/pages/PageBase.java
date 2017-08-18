package me.maxwu.wlg.pages;


import me.maxwu.wlg.log.TimeStamp;
import me.maxwu.wlg.selenium.DriverFactory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;


import javax.imageio.ImageIO;
import java.io.File;

public class PageBase implements Snapable {
    static Logger logger = LoggerFactory.getLogger(PageBase.class.getName());
    protected WebDriver driver = null;
    // By default, check URL against HTTPs only.
    private String urlRegEx = "^https?:////";
    // No default title check with wildcard.
    private String titleRegEx = ".*";

    public void saveScreenShot(String caseName) {
        String ts = TimeStamp.getTs();
        //File scrFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        try {
            //FileUtils.copyFile(scrFile, new File( ts + "_" + caseName + ".png"));
            ImageIO.write(
                new AShot()
                .shootingStrategy(ShootingStrategies.viewportPasting(100))
                .takeScreenshot(driver).getImage(),
                "png",
                new File( ts + "_" + caseName + ".png")
            );

        }catch (Exception e){
            logger.error("Error on saving snap: " + e);
        }
    }

    public boolean checkUrl(String urlPattern){
        String currentUrl = driver.getCurrentUrl();
        if (!currentUrl.matches(urlPattern)){
            throw new WrongPageException("Wrong URL with Pattern \"" + urlPattern + "\"",
                driver);
        }else{
            return true;
        }

    }

    public boolean checkTitle(String titlePattern){
        String currentTitle = driver.getTitle();
        if (!currentTitle.matches(titlePattern)){
            throw new WrongPageException("Wrong Title against Pattern \"" + titlePattern + "\"",
                driver);
        }else{
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
        driver.get(url);
        return this;
    }

    public PageBase(WebDriver dr, String urlPattern, String titlePattern){
        this(dr);
        urlRegEx = urlPattern;
        titleRegEx = titlePattern;
    }

    public PageBase(WebDriver driver) {
        this.driver = driver;
    }

    public void maximize(){
        driver.manage().window().maximize();
    }

    public void quit(){
        DriverFactory.quitDriver(driver);
    }

    @Override
    protected void finalize(){
        quit();
    }

}
