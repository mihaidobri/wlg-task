package me.maxwu.wlg;

import me.maxwu.wlg.pages.Calculators;
import me.maxwu.wlg.selenium.DriverFactory;
import org.junit.*;
import org.openqa.selenium.WebDriver;

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

    @BeforeClass
    public static void beforeClass() {
        // Time metrics during case development:
        // driver = DriverFactory.getFirefoxDriver(true); 19.655s
        // driver = DriverFactory.getFirefoxDriver(false); 19.313s
        // driver = DriverFactory.getChromeDriver(true); 16.903s
        // driver = DriverFactory.getChromeDriver(false); 19.255s

        driver = DriverFactory.getDriver();
        calPage = new Calculators(driver);
    }

    @Before
    public void setUp() {
        ((SnapRule) snapRule).setSnapable(calPage);
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

    // Verify there are exactly 3 calculators under mortgage calculators panel.
    @Test
    public void aTagsUnderMortgageCalsTest() {
        Assert.assertArrayEquals(calPage.getAtagTextUnderMortgageCals().toArray(), expectedTexts);
    }

    @Test
    public void h1MortgageTextTest(){
        Assert.assertEquals(calPage.getH1Mortgage(), "Mortgage calculators");
    }

    @AfterClass
    public static void afterClass() {
        calPage.quit();
    }
}
