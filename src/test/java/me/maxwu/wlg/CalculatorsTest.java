/*
 * The JUnit Test Suite for Mortgage Calculators Entrance Page.
 * Testing against calculators are on separate calculator test suites.
 */
package me.maxwu.wlg;

import java.util.Arrays;
import me.maxwu.wlg.models.BorrowCal;
import me.maxwu.wlg.models.Calculators;
import me.maxwu.wlg.models.RepayCal;
import me.maxwu.wlg.selenium.DriverFactory;
import me.maxwu.wlg.selenium.DriverFactory.Headless;
import org.junit.*;
import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Suite Definition for Mortgage Calculators Entrance Page.
 * The suite includes basic hyper link check and page transition test.
 *
 * Rough time metrics during case development:
 * driver = DriverFactory.getFirefoxDriver(Headless.Enabled); 28.954s
 * driver = DriverFactory.getFirefoxDriver(Headless.Disabled); 34.255s
 * driver = DriverFactory.getChromeDriver(Headless.Enabled); 30.1s
 * driver = DriverFactory.getChromeDriver(Headless.Disabled); 31.508s
 * Dev environment: MBP
 */
public class CalculatorsTest {
    private static Logger logger = LoggerFactory.getLogger(CalculatorsTest.class.getName());
    private static WebDriver driver = null;
    private static Calculators calPage = null;
    private static String[] expectedTexts = {
        "Repayments calculator",
        "Borrowing calculator",
        "Moving house and renovating calculator"
    };

    @Rule
    public SnapRule snapRule = new SnapRule();
    @Rule
    public TestRule watch = new ResultWatcher();

    @BeforeClass
    public static void beforeClass() {
        driver = DriverFactory.getDriver();
    }

    @Before
    public void setUp() {
        if (DriverFactory.hasQuit(driver)){
            beforeClass();
        }
        calPage = new Calculators(driver);
        snapRule.setSnapable(calPage);
        calPage.get();
    }

    @Test
    public void mortgageCalsAtagTextTest() {
        Assert.assertEquals("Mortgage calculators", calPage.getMortgageCalsText());
    }

    @Test
    public void repaymentsCalAtagTextTest() {
        Assert.assertEquals(expectedTexts[0], calPage.getRepaymentsCalText());
    }

    @Test
    public void borrowingCalAtagTextTest() {
        Assert.assertEquals(expectedTexts[1], calPage.getBorrowingCalText());
    }

    /**
     * Verify there are exactly 3 calculators under mortgage calculators panel,
     *   including visible one and invisible ones.
     */
    @Test
    public void aTagsUnderMortgageCalsTest() {
        Assert.assertArrayEquals(calPage.getAtagTextUnderMortgageCals().toArray(), expectedTexts);
    }

    @Test
    public void h1MortgageTextTest(){
        Assert.assertEquals(calPage.getH1Mortgage(), "Mortgage calculators");
    }

    @Test
    public void clickRepaymentsTest(){
        RepayCal repayPage = calPage.getRepaymentsPage();
        repayPage.checkUrlWait(2);
        repayPage.checkTitle();
        Assert.assertEquals(Arrays.asList(true, false, false), repayPage.getScenariosVisibilities());
    }

    @Test
    public void clickBorrowingTest(){
        BorrowCal borrowPage = calPage.getBorrowingPage();
        borrowPage.checkUrlWait(2);
        borrowPage.checkTitle();
    }

    @AfterClass
    public static void afterClass() {
        calPage.quit();
    }
}
