/*
 * The Junit Test Suit for Mortgage Calculators Page.
 * Testing against calculator measurements are on separate calculator test suites.
 */

package me.maxwu.wlg;

import me.maxwu.wlg.models.Calculators;
import me.maxwu.wlg.models.RepayCal;
import me.maxwu.wlg.selenium.DriverFactory;
import org.junit.*;
import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;

/**
 * Test Suite Definition for Mortgage Calculators Entrance Page.
 *
 * Time metrics during case development:
 * driver = DriverFactory.getFirefoxDriver(Headless.Enabled); 19.655s
 * driver = DriverFactory.getFirefoxDriver(Headless.Disabled); 19.313s
 * driver = DriverFactory.getChromeDriver(Headless.Enabled); 16.903s
 * driver = DriverFactory.getChromeDriver(Headless.Disabled); 19.255s
 */
public class CalculatorsTest {
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
        RepayCal repayCal = calPage.getRepaymentsPage();
        repayCal.checkUrl();
        repayCal.checkTitle();
    }

    // TODO: Click and Jump to Borrowing.

    @AfterClass
    public static void afterClass() {
        calPage.quit();
    }
}
