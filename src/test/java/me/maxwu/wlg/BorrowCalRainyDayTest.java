/*
 * Junit Suite for Borrowing Calculator Rainy Day tests.
 */

package me.maxwu.wlg;

import me.maxwu.wlg.models.BorrowCal;
import me.maxwu.wlg.selenium.DriverFactory;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Test Suite as Borrowing Calculator Rainy Day Sample.
 * It offers test cases concerning the visibility of error message on house income.
 * INFO: Rainy day case with house income <5,000 has checkpoint on result panel and it is not included.
 *       Result accessibility/visibility are not included so far.
 */
public class BorrowCalRainyDayTest {
    private static Logger logger = LoggerFactory.getLogger(BorrowCalRainyDayTest.class.getName());
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

    /*
     * This method tests borrowing calculator against empty input.
     * For the story with digital input values less than 5000, it is not covered in current case.
     */
    @Test
    public void houseIncomeEmptyErrorTest() {
        Assert.assertFalse(borrowPage.isHouseIncomeErrtipVisible());
        // Intend to leave the input empty before triggering the calculation.
        borrowPage.clearHouseIncome();
        borrowPage.calculate();
        Assert.assertTrue(borrowPage.isHouseIncomeErrtipVisible());
    }

    @Test
    public void houseIncomeNonNumErrorTest() {
        Assert.assertFalse(borrowPage.isHouseIncomeErrtipVisible());
        borrowPage.setHouseIncome("Not a number");
        borrowPage.calculate();
        Assert.assertTrue(borrowPage.isHouseIncomeErrtipVisible());
    }

    @Test
    public void houseIncomeTooLongErrorTest() {
        Assert.assertFalse(borrowPage.isHouseIncomeErrtipVisible());
        // Valid inputs are expected not longer than 15 digits.
        borrowPage.setHouseIncome("01234567890123456");
        borrowPage.calculate();
        Assert.assertTrue(borrowPage.isHouseIncomeErrtipVisible());
        // Error message body is not verified so far.
    }

    @AfterClass
    public static void afterClass() {
        borrowPage.quit();
    }
}
