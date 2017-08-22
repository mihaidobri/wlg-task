package me.maxwu.wlg;

import me.maxwu.wlg.models.RepaymentsCal;
import me.maxwu.wlg.selenium.DriverFactory;
import org.junit.*;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepaymentsCalTest {
    static Logger logger = LoggerFactory.getLogger(RepaymentsCalTest.class.getName());
    private static WebDriver driver = null;
    private static RepaymentsCal repayCalPage = null;

    @Rule
    public SnapRule snapRule = new SnapRule();

    @BeforeClass
    public static void beforeClass() {
        // Time metrics during case development:
        // driver = DriverFactory.getFirefoxDriver(true); 19.655s
        // driver = DriverFactory.getFirefoxDriver(false); 19.313s
        // driver = DriverFactory.getChromeDriver(true); 16.903s
        // driver = DriverFactory.getChromeDriver(false); 19.255s
        // driver = DriverFactory.getDriver();
        driver = DriverFactory.getChromeDriver(false);
        repayCalPage = new RepaymentsCal(driver);
    }

    @Before
    public void setUp() {
        snapRule.setSnapable(repayCalPage);
        repayCalPage.get();

    }

    @Test
    public void scenario1MonthlySmokeTest() {
        int amount = 10000;
        int expectedRate = 59;
        int index = 0;

        repayCalPage.setLoadAmountForScenario(Integer.toString(amount), index);
        repayCalPage.calculateForScenario(index);

        String res = repayCalPage.getResultForScenario(index);
        logger.debug("Result = " + res + " for scenario: " + index);

        String resLegend = repayCalPage.getResultLegendForScenario(index);
        logger.debug("Legend = " + resLegend + " for scenario: " + index);

        Assert.assertEquals(res, resLegend);
        Assert.assertEquals("$" + Integer.toString(expectedRate), res);
    }

    @Test
    public void scenarioNumTest() {
        Assert.assertEquals(3, repayCalPage.getScenarios().size());
    }


    @AfterClass
    public static void afterClass() {
        repayCalPage.quit();
    }
}
