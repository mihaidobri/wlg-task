/*
 * Junit Suite for Borrowing Calculator Sunny Day tests.
 */

package me.maxwu.wlg;

import me.maxwu.wlg.models.BorrowCal;
import me.maxwu.wlg.models.CalUtils;
import me.maxwu.wlg.models.Calculators;
import me.maxwu.wlg.models.RepayCal;
import me.maxwu.wlg.selenium.DriverFactory;
import org.junit.After;
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
 * Suite for Borrowing Calculator Sunny Day tests.
 *
 */
public class BorrowCalSunnyDayTest {
    private static Logger logger = LoggerFactory.getLogger(BorrowCalSunnyDayTest.class.getName());
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
    public void setUp(){
        if (DriverFactory.hasQuit(driver)){
            beforeClass();
        }
        borrowPage = new BorrowCal(driver);
        snapRule.setSnapable(borrowPage);
        borrowPage.get();
    }

    @Test
    public void borrowIndividualNoChildrenNoVehicleSmokeTest() {
        borrowPage.setIndividualType();
        borrowPage.setHouseIncome("150000");

        Assert.assertEquals("0", borrowPage.getChildrenNum());
        borrowPage.addChildrenNum();
        Assert.assertEquals("1", borrowPage.getChildrenNum());
        borrowPage.minusChildrenNum();
        Assert.assertEquals("0", borrowPage.getChildrenNum());

        Assert.assertEquals("0", borrowPage.getVehicleNum());
        borrowPage.setVehicleNum("1");
        borrowPage.minusVehicleNum();
        Assert.assertEquals("0", borrowPage.getVehicleNum());

        borrowPage.calculate();

        Assert.assertEquals("$1,142,000", borrowPage.getResult());
    }

    @Test
    public void borrowJointTwoChildrenOneVehicleSmokeTest() {
        borrowPage.setJointType();
        borrowPage.setHouseIncome("100000");

        Assert.assertEquals("0", borrowPage.getChildrenNum());
        borrowPage.setChildrenNum("1");
        borrowPage.addChildrenNum();
        Assert.assertEquals("2", borrowPage.getChildrenNum());
        borrowPage.addChildrenNum();
        Assert.assertEquals("3", borrowPage.getChildrenNum());
        borrowPage.minusChildrenNum();
        Assert.assertEquals("2", borrowPage.getChildrenNum());

        Assert.assertEquals("0", borrowPage.getVehicleNum());
        borrowPage.addVehicleNum();
        Assert.assertEquals("1", borrowPage.getVehicleNum());

        borrowPage.calculate();

        Assert.assertEquals("$498,000", borrowPage.getResult());
    }


    @After
    public void afterClass() {
        borrowPage.quit();
    }
}
