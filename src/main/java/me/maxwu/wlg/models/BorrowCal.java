package me.maxwu.wlg.models;

import java.util.List;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BorrowCal extends PageBase {
    static Logger logger = LoggerFactory.getLogger(RepayCal.class.getName());
    private static final String baseUrl = "https://tools.anz.co.nz/home-loans/borrowing-calculator/";
    static private String urlRegEx = "^https://tools.anz.co.nz/home-loans/borrowing-calculator/?$";
    static private String titleRegEx = "^\\s*Borrowing Calculator\\s+\\|\\s+How much can I borrow\\?\\s+\\|\\s+ANZ Store\\s*$";

    @FindBy(css="a.reset.js-resetButton")
    @CacheLookup
    WebElement btnReset;

    @FindBy(css="label#simpleIndividualType[for='IndividualApplication']")
    @CacheLookup
    WebElement radioIndividual;

    @FindBy(css="label#simpleJointType[for='JointApplication']")
    @CacheLookup
    WebElement radioJoint;

    By houseIncomeErrtipCss = By.cssSelector("div#js-simpleFormWrapper span.field-validation-error[data-valmsg-for='Income.AnnualHousehold']");

    @FindBy(css="div#js-simpleFormWrapper input#Income_AnnualHousehold[name='Income.AnnualHousehold']")
    @CacheLookup
    WebElement houseIncome;

    public void clearHouseIncome(){
        houseIncome.clear();
    }

    public void setHouseIncome(String income){
        if ((income == null) || (income.isEmpty())){
            throw new IllegalArgumentException("Please turn to clear() method to clear income field");
        }
        clearHouseIncome();
        new Actions(driver).moveToElement(houseIncome).sendKeys(income).build().perform();
    }

    public void setIndividualType(){
        radioIndividual.click();
    }

    public void setJointType(){
        radioJoint.click();
    }

    /**
     * Return if the error tooltip is visible to end user.
     * INFO: Refactor candidate to extract boolean method isLocatable() to CalUtils class.
     */
    public boolean isHouseIncomeErrtipVisible(){
        List<WebElement> houseIncomeErrtips = driver.findElements(houseIncomeErrtipCss);

        return ((houseIncomeErrtips != null)
            && (houseIncomeErrtips.size() == 1)
            && CalUtils.isVisibleOnCalculator(houseIncomeErrtips.get(0))
        );
    }

    @FindBy(css="div#js-simpleFormWrapper input[type='button'][value='-'][data-btnplusminfor='LoanDetails_Dependants']")
    @CacheLookup
    WebElement btnDecreaseChildrenNum;

    @FindBy(css="div#js-simpleFormWrapper input[type='button'][value='+'][data-btnplusminfor='LoanDetails_Dependants']")
    @CacheLookup
    WebElement btnIncreaseChildrenNum;

    @FindBy(css="div#js-simpleFormWrapper input#LoanDetails_Dependants")
    @CacheLookup
    WebElement childrenNum;

    public String getChildrenNum(){
        return childrenNum.getAttribute("value");
    }

    public void setChildrenNum(String numText){
        childrenNum.clear();
        childrenNum.sendKeys(numText);
    }

    public void addChildrenNum(){
        btnIncreaseChildrenNum.click();
    }

    public void minusChildrenNum(){
        btnDecreaseChildrenNum.click();
    }

    @FindBy(css="div#js-simpleFormWrapper input[type='button'][value='-'][data-btnplusminfor='LoanDetails_Vehicles']")
    @CacheLookup
    WebElement btnDecreaseVehicleNum;

    @FindBy(css="div#js-simpleFormWrapper input[type='button'][value='+'][data-btnplusminfor='LoanDetails_Vehicles']")
    @CacheLookup
    WebElement btnIncreaseVehicleNum;

    @FindBy(css="div#js-simpleFormWrapper input#LoanDetails_Vehicles")
    @CacheLookup
    WebElement vehicleNum;

    public String getVehicleNum(){
        return vehicleNum.getAttribute("value");
    }

    public void setVehicleNum(String numText){
        vehicleNum.clear();
        vehicleNum.sendKeys(numText);
    }

    public void addVehicleNum(){
        btnIncreaseVehicleNum.sendKeys(Keys.RETURN);
    }

    public void minusVehicleNum(){
        btnDecreaseVehicleNum.click();
    }

    @FindBy(css="div#js-simpleFormWrapper input[type='button'][value='Calculate']")
    WebElement btnCalculate;

    public void calculate(){
        btnCalculate.sendKeys(Keys.RETURN);
    }

    @FindBy(css="div.calculatedResults p#amountAbleToBorrow")
    WebElement amountAbleToBorrow;

    /**
     * As the major calculation density task to get result with given conditions,
     *     current method getResult() has planted a <p>Fluent Wait</p> up to 5s and
     *     250ms check interval to adapt slow reacting situations.
     *     However, it is recommended to figure out performance criteria with BA
     *     or through test statistics over public network (e.g. 3 sigma tolerance
     *     from middle response time on cloud based testing).
     *
     * @return The string value of result presentation on Web GUI.
     */
    public String getResult(){
        new FluentWait<WebDriver>(driver)
            .pollingEvery(250, TimeUnit.MILLISECONDS)
            .withTimeout(5, TimeUnit.SECONDS)
            .ignoring(NoSuchElementException.class)
            .until((dr) -> {
                logger.debug("checking results visibilities for borrowing calculator..");
                return (CalUtils.isVisibleOnCalculator(amountAbleToBorrow));
            });

        return amountAbleToBorrow.getText();
    }

    public boolean isResultVisible(){
        return CalUtils.isVisibleOnCalculator(amountAbleToBorrow);
    }

    //******* General Test Supports: ********
    //
    public BorrowCal(WebDriver driver){
        super(driver, urlRegEx, titleRegEx);
        PageFactory.initElements(driver, this);
    }

    public BorrowCal get(){
        get(baseUrl);
        checkUrl();
        checkTitle();
        return this;
    }

}