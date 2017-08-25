package me.maxwu.wlg;

import java.util.Arrays;
import java.util.List;
import me.maxwu.wlg.models.RepayCal;
import me.maxwu.wlg.selenium.DriverFactory;
import org.junit.*;
import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Suite for Repayments Calculator.
 * General visibility and scenario adjustment tests are introduced.
 * Interest rates, length, other detail function tests and rainy day tests are not covered
 *     with current sample baseline.
 */
public class RepayCalTest {
    private static Logger logger = LoggerFactory.getLogger(RepayCalTest.class.getName());
    private static WebDriver driver = null;
    private static RepayCal repayCalPage = null;
    private static int SCENARIO_NUM = 3;

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
        repayCalPage = new RepayCal(driver);
        snapRule.setSnapable(repayCalPage);
        repayCalPage.get();
    }

    @Test
    public void scenarioNumTest() {
        Assert.assertEquals(SCENARIO_NUM, repayCalPage.getScenarios().size());
    }

    @Test
    public void scenario0TooltipTest(){
        Assert.assertFalse(repayCalPage.isTooltipVisibleForScenario(0));

        repayCalPage.clickTooltipForScenario(0);

        Assert.assertTrue(repayCalPage.isTooltipVisibleForScenario(0));
    }

    /**
     * Test implementation to calculate monthly rate on given scenario panel.
     * This method is picked up by test case methods to apply as a parameterized case.
     * For official application, interest rate shall be added to this method to take care.
     *
     * INFO: Candidate case to parametrise for default frequency of monthly.
     * @param index Index of scenario panel
     * @param amount Loan amount.
     * @param expectedRate The expected rate number.
     */
    public void monthlyCalForScenario(int index, int amount, String length, String expectedRate) {
        // Before calculation, the result panel and scenario-legend are invisible.
        Assert.assertFalse(repayCalPage.isResultsPanelVisibleForScenario(index));
        logger.debug("    Result Panel Visibility: False");

        Assert.assertFalse(repayCalPage.isScenarioLegendVisibleForScenario(index));
        logger.debug("    Scenario Legend Visibility: False");

        repayCalPage.setLoanAmountForScenario(index, Integer.toString(amount));
        logger.debug("set loan amount to:" + Integer.toString(amount));
        repayCalPage.setLoanLengthForScenarioByValue(index, length);
        // Map the length select value to option texts.
        logger.debug("set loan length to:" + length + " years");
        repayCalPage.calculateForScenario(index);

        String res = repayCalPage.getResultForScenario(index);
        logger.debug("    Result=" + res + " on scenario: " + index);

        String resLegend = repayCalPage.getResultLegendForScenario(index);
        logger.debug("    Legend=" + resLegend + " for scenario: " + index);

        Assert.assertEquals(res, resLegend);
        Assert.assertEquals(expectedRate, res);

        logger.debug("    Result Amount=" + repayCalPage.getResultAmountForScenario(index));

        List<String> totalInterests = repayCalPage.getTotalInterestForScenario(index);
        // Only one interest number is visible.
        Assert.assertEquals(1, totalInterests.size());
        logger.debug("    Total Interest=" + repayCalPage.getTotalInterestForScenario(index).get(0));

        logger.debug("    Total Cost=" + repayCalPage.getTotalCostForScenario(index).get(0));
    }

    @Test
    public void scenario0MonthlySmokeTest() {
        int amount = 300000;
        String length = "20";
        // The expected result for 1st scenario loan calculation.
        String expectedRate = "$2,113";
        int index = 0;

        monthlyCalForScenario(index, amount, length, expectedRate);
    }

    @Test
    public void resetNewCalSanityTest() {
        // Reset on a fresh new Repayments Calculator still shows a new empty calculator.
        repayCalPage.resetCal();
        // Fresh new page only shows scenario-0
        Assert.assertEquals("", repayCalPage.getLoanAmountForScenario(0));
    }

    @Test
    public void resetScenario0SmokeTest() {
        monthlyCalForScenario(0, 10000, "30", "$59");
        repayCalPage.resetCal();
        Assert.assertEquals("", repayCalPage.getLoanAmountForScenario(0));
    }

    @Test
    public void scenarioVisibilitiesAddThisAndHideTest() {
        // By default when the URL is open, only scenario[0] is visible.
        Assert.assertEquals(Arrays.asList(true, false, false), repayCalPage.getScenariosVisibilities());

        // Trigger a calculation on scenario[0]
        monthlyCalForScenario(0, 10000, "30", "$59");

        // Add this calculator as a scenario
        repayCalPage.addThisAsAScenario();
        // The 2nd scenarios shows up after action.
        Assert.assertEquals(Arrays.asList(true, true, false), repayCalPage.getScenariosVisibilities());
        // After adding current calculation as scenario,
        //     the two visible scenarios have same loan amount and interest rate.
        Assert.assertEquals(
            repayCalPage.getResultAmountForScenario(0),
            repayCalPage.getResultAmountForScenario(1)
        );
        Assert.assertEquals(
            repayCalPage.getInterestRateForScenario(0),
            repayCalPage.getInterestRateForScenario(1)
        );

        // Remove(Hide) the 2nd scenario panel.
        repayCalPage.removeScenario(1);
        Assert.assertEquals(Arrays.asList(true, false, false), repayCalPage.getScenariosVisibilities());
    }

    @Test
    public void scenarioVisibilitiesCreateNewAndHideTest() {
        // By default when the URL is open, only scenario[0] is visible.
        Assert.assertEquals(Arrays.asList(true, false, false), repayCalPage.getScenariosVisibilities());

        // Trigger a calculation on scenario[0]
        monthlyCalForScenario(0, 10000, "30","$59");

        // Add this calculator as a scenario, the 2nd scenarios shows up after action.
        repayCalPage.createANewScenario();
        Assert.assertEquals(Arrays.asList(true, true, false), repayCalPage.getScenariosVisibilities());

        // After creating a new scenario, the two visible scenarios have same loan amount and interest rate.
        Assert.assertEquals(
            repayCalPage.getResultAmountForScenario(0),
            repayCalPage.getResultAmountForScenario(1)
        );
        Assert.assertEquals(
            repayCalPage.getInterestRateForScenario(0),
            repayCalPage.getInterestRateForScenario(1)
        );

        // Remove(Hide) the 2nd scenario panel.
        repayCalPage.removeScenario(1);
        Assert.assertEquals(Arrays.asList(true, false, false), repayCalPage.getScenariosVisibilities());
    }

    /**
     *  As a sample suite, "Duplicate Scenario 2" button is not covered so far.
     */
    @Test
    public void scenarioVisibilityDuplicateScenario1Test(){
        Assert.assertEquals(Arrays.asList(true, false, false), repayCalPage.getScenariosVisibilities());

        monthlyCalForScenario(0, 300000, "30","$1,758");

        // When current scenario panel is added as a scenario, the 2nd scenario panel shall be visible
        // In addition, the interest rates shall equal.
        repayCalPage.addThisAsAScenario();
        Assert.assertEquals(Arrays.asList(true, true, false), repayCalPage.getScenariosVisibilities());
        Assert.assertEquals(
            repayCalPage.getInterestRateForScenario(0),
            repayCalPage.getInterestRateForScenario(1)
        );

        repayCalPage.duplicateScenario1();
        Assert.assertEquals(Arrays.asList(true, true, true), repayCalPage.getScenariosVisibilities());
        Assert.assertEquals(
            repayCalPage.getInterestRateForScenario(0),
            repayCalPage.getInterestRateForScenario(1)
        );
    }

    @AfterClass
    public static void afterClass() {
        repayCalPage.quit();
    }
}
