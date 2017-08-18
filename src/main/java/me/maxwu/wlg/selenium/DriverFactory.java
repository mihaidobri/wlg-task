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
import java.util.logging.Level;

public class DriverFactory {
    static ChromeOptions options = null;
    static Logger logger = LoggerFactory.getLogger(DriverFactory.class.getName());
    // Environment variable key for browser type.
    static String ENV_BROWSER = "browser";
    // Browser types in lower case.
    static List<String> BROWSER_TYPE_CHROME = Arrays.asList("chrome", "chromium");
    static List<String> BROWSER_TYPE_FIREFOX = Arrays.asList("ff", "firefox");
    static List<String> BROWSER_TYPE_PHANTOMJS = Arrays.asList("phamtomjs");
    // PhantomJS is eclipsed by headless mode with FF>=v56 and Chrome>=v61.
    // Review whether it is still an option.

    // Default browser type.
    static String DEF_BROWSER = BROWSER_TYPE_FIREFOX.get(0);




    // Intend not to enable headless by default.
    public static WebDriver getDriver() {
        return getDriver(false);
    }

    // Detect browser type from environment variable to support travis-CI and Jenkins adjustment.
    public static WebDriver getDriver(boolean headless){
        WebDriver driver = null;

        // If environment is not set, just run with default browser.
        // TODO: add headless mode for FF>=v56 and Chrome>=v61.
        String browser = Optional.ofNullable(System.getenv(ENV_BROWSER)).orElse(DEF_BROWSER);

        logger.debug("Browser Option is " + browser);

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

    @NotNull
    public static WebDriver getFirefoxDriver(boolean headless){
        FirefoxOptions options = new FirefoxOptions();

        // To enable headless mode with FF>=56 on Mac/Windows or FF>=55 on Linux,
        // config environment MOZ_HEADLESS (e.g. to "1"), or, add args "-headless" now.
        // Still waiting for the official interface in FF stable branch.

        if (headless){
            options.addArguments("-headless");
        }
        options.addArguments("-safe-mode");

        // Check current environment is CI or Dev. Modify binary path property for Gecko on Dev.
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("mac")) {
            // binary
            System.setProperty("webdriver.firefox.bin", "/Applications/FirefoxDeveloperEdition.app/Contents/MacOS/firefox");
        }

        FirefoxDriverManager.getInstance().setup();
        return new FirefoxDriver(options);
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
    public static WebDriver getChromeDriver(boolean headless){
        options = new ChromeOptions();

        // For the ist of options, refer to http://peter.sh/experiments/chromium-command-line-switches/
        options.addArguments("start-maximized");
        options.addArguments("dns-prefetch-disable");
        options.addArguments("test-type");
        options.addArguments("disable-plugins");
        options.addArguments("disable-extensions");
        options.addArguments("--loglevel 0");
        options.addArguments("ignore-urlfetcher-cert-requests");

        String arch = System.getProperty("sun.arch.data.model");
        logger.info("Arch=" + arch);

        if (arch.contains("32")) {
            // For 32bit CentOS VPS with Jenkins, utilize chromium.
            setUpPropertiesFor32bit();
        }else {
            // For 64bit platform, test with recent Chrome versions.
            if (headless) {
                options.addArguments("--headless");
            }
        }

        ChromeDriverManager.getInstance().setup();

        return (null==options)? new ChromeDriver(): new ChromeDriver(options);
    }

    @Contract("null -> false")
    public static boolean hasQuit(WebDriver driver) {
        return ((driver != null) && (((RemoteWebDriver) driver).getSessionId() == null));
    }

    public static void quitDriver(WebDriver driver){
        if ((driver != null) &&(!hasQuit(driver))){
            logger.debug("**** Destroy Web Driver #" + driver.hashCode() +"****");
            driver.quit();
        }else{
            logger.warn("Destroy a null or quit driver #" + (driver!=null? driver.hashCode(): "None"));
        }
    }

}

