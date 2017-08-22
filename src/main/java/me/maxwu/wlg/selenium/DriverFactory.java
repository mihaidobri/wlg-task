package me.maxwu.wlg.selenium;

import io.github.bonigarcia.wdm.ChromeDriverManager;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Factory to launch web driver instances.
 *
 * INFO: For future migration to Selenium Grid or cloud web driver, this factory is the main
 *       checkpoint to evaluate.
 */
public class DriverFactory {
    static ChromeOptions options = null;
    static Logger logger = LoggerFactory.getLogger(DriverFactory.class.getName());
    // Environment variable key for browser type.
    static String ENV_BROWSER = "browser";
    // Browser types in lower case.
    static List<String> BROWSER_TYPE_CHROME = Arrays.asList("chrome", "chromium");
    static List<String> BROWSER_TYPE_FIREFOX = Arrays.asList("ff", "firefox");
    static List<String> BROWSER_TYPE_PHANTOMJS = Arrays.asList("phantomjs");
    // PhantomJS is eclipsed by headless mode with FF>=v56 and Chrome>=v61.
    // Review whether it is still an option.
    // Default browser type.
    static String DEF_BROWSER = BROWSER_TYPE_CHROME.get(0);

    // Environment variable key on headless mode
    static String ENV_HEADLESS = "headless";
    // Values to enable headless mode in lower case.
    static List<String> HEADLESS_ENABLED = Arrays.asList("true", "1", "t");
    public static enum Headless{
        Disabled, Enabled;
    }
    // By default, disable headless mode.
    static String DEF_HEADLESS = "0";


    /**
     * Process Headless Flag then call getDriver(Headless headless) to launch web driver
     *   on specific browser type.
     */
    public static WebDriver getDriver() {
        WebDriver dr;
        String headless = Optional.ofNullable(System.getenv(ENV_HEADLESS)).orElse(DEF_HEADLESS);
        logger.debug("Browser Headless Mode Flag is '" + headless + "'");

        headless = headless.toLowerCase();
        if (HEADLESS_ENABLED.contains(headless)) {
            dr = getDriver(Headless.Enabled);
        }else{
            dr = getDriver(Headless.Disabled);
        }
        return dr;
    }

    // Detect browser type from environment variable to support travis-CI and Jenkins adjustment.
    public static WebDriver getDriver(Headless headless){
        WebDriver driver = null;

        // If environment is not set, just run with default browser.
        String browser = Optional.ofNullable(System.getenv(ENV_BROWSER)).orElse(DEF_BROWSER);

        logger.debug("Browser Type Flag is '" + browser + "'");
        browser = browser.toLowerCase();

        if (BROWSER_TYPE_CHROME.contains(browser)) {
            driver = getChromeDriver(headless);
            logger.info("**** Created Chrome Web Driver #" + driver.hashCode() + "****");
            return driver;
        }
        if (BROWSER_TYPE_FIREFOX.contains(browser)) {
            driver = getFirefoxDriver(headless);
            logger.info("**** Created Firefox Web Driver #" + driver.hashCode() + "****");
            return driver;
        }
        throw new IllegalArgumentException("Browser type is not supported: " + browser);
    }

    public static boolean isMacOs(){
        boolean isOnMac = System.getProperty("os.name", "UNKNOWN").toLowerCase().contains("mac");
        if (isOnMac){
            logger.trace("\uF8FF detected");
        }
        return isOnMac;
    }

    @NotNull
    public static WebDriver getFirefoxDriver(Headless headless){
        FirefoxOptions options = new FirefoxOptions();

        // To enable headless mode with FF>=56 on Mac/Windows or FF>=55 on Linux,
        // config environment MOZ_HEADLESS (e.g. to "1"), or, add args "-headless" now.
        // Still waiting for the official interface in FF stable branch.

        if (headless == Headless.Enabled){
            options.addArguments("-headless");
        }
        // To disable all plugins, set safe-mode in cli.
        options.addArguments("-safe-mode");
        options.addArguments("-height 1000 -width 1200");

        // Check current environment is CI or Dev. Modify binary path property for Gecko on Dev.
        if (isMacOs()) {
            // binary
            System.setProperty("webdriver.firefox.bin", "/Applications/FirefoxDeveloperEdition.app/Contents/MacOS/firefox");
        }

        FirefoxDriverManager.getInstance().setup();
        WebDriver wd = new FirefoxDriver(options);
        wd.manage().window().maximize();
        return wd;
    }

    static void setUpPropertiesFor32bit() {
        logger.info("setup 32bit chromium options..");
        // Take Chromium-browser instead of google-chrome since 32bit chrome is EOS.
        System.setProperty("wdm.override", "true");
        System.setProperty("timeout", "30");
        System.setProperty("wdm.forceCache", "true");
        options.setBinary(new File("/usr/bin/chromium-browser"));
    }

    @NotNull
    public static WebDriver getChromeDriver(Headless headless){
        options = new ChromeOptions();

        // For the ist of options, refer to http://peter.sh/experiments/chromium-command-line-switches/
        options.addArguments("start-maximized");
        options.addArguments("dns-prefetch-disable");
        options.addArguments("test-type");
        options.addArguments("disable-plugins");
        options.addArguments("disable-extensions");
        options.addArguments("ignore-urlfetcher-cert-requests");
        options.addArguments("--incognito");

        String arch = System.getProperty("sun.arch.data.model");
        logger.info("Arch=" + arch);

        if (arch.contains("32")) {
            // For 32bit CentOS VPS with Jenkins, utilize chromium.
            setUpPropertiesFor32bit();
        }else {
            // For 64bit platform, test with recent Chrome versions.
            if (headless == Headless.Enabled) {
                options.addArguments("--headless");
            }
        }

        // Most recent chrome driver release is v2.31 (for chrome v58-60) by Aug 2017.
        // Dev env is driver v2.31 + chrome v62 on Mac.
        // Release notes: https://chromedriver.storage.googleapis.com/2.31/notes.txt
        // Use ChromeDriverManager.getInstance().version("$version").setup() to specify a cached version with wdm.
        ChromeDriverManager.getInstance().setup();

        return (null==options)? new ChromeDriver(): new ChromeDriver(options);
    }

    @Contract("null -> false")
    public static boolean hasQuit(WebDriver driver) {
        return ((driver != null) && (((RemoteWebDriver) driver).getSessionId() == null));
    }

    public static void quitDriver(WebDriver driver){
        if ((driver != null) &&(!hasQuit(driver))){
            logger.info("*** Destroy driver #" + driver.hashCode() +" ***");
            driver.quit();
        }else{
            logger.info("*** Destroy a null or quit driver #" + (driver!=null? driver.hashCode(): "NULL") + " ***");
        }
    }

}

