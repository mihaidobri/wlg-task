/*
 * The Junit Test Suit for Mortgage Calculators Page.
 * Testing against calculator measurements are on separate calculator test suites.
 */

package me.maxwu.wlg;

import me.maxwu.wlg.models.BorrowCal;
import me.maxwu.wlg.models.Calculators;
import me.maxwu.wlg.models.RepayCal;
import me.maxwu.wlg.selenium.DriverFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;

/**
 * Test Suite for Borrowing Calculator.
 *
 */
public class BorrowCalSunnyDayTest {
    private static WebDriver driver = null;
    private static BorrowCal borrowPage = null;

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
        borrowPage = new BorrowCal(driver);
        snapRule.setSnapable(borrowPage);
        borrowPage.get();
    }

    @Test
    public void borrowingSmokeTest() {

    }


    @AfterClass
    public static void afterClass() {
        borrowPage.quit();
    }
}
