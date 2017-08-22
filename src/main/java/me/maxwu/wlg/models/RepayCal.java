package me.maxwu.wlg.models;


import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// Outer Calculator Panel
// +-> Scenario * 3
//     +-> loan amount, tooltip, frequency, rate
//     +-> (from 2nd on) control to remove current scenario
//     +-> button to calculate
//     +-> calculated result
// +-> Adjustment Panel
//     +-> buttons to add/duplicate scenarios
// +-> Common Controls
//     +-> reset, print for repayments

public class RepaymentsCal extends PageBase {
    static Logger logger = LoggerFactory.getLogger(RepaymentsCal.class.getName());
    private static final String baseUrl = "https://tools.anz.co.nz/home-loans/repayments-calculator/";
    static private String urlRegEx = "^https://tools.anz.co.nz/home-loans/repayments-calculator[/]$";
    static private String titleRegEx = "^\\s*Repayments Calculator \\| What will my home loan repayments be\\? \\| ANZ Store\\s*$";

    //******** Calculator Outer Panel: ********
    @FindBy(css="div#RepaymentCalculatorOuter")
    WebElement outerCalPanel;

    @FindBy(css="i.icon-reset")
    WebElement btnReset;

    @FindBy(css="button.resetButton")
    WebElement btnResetConfirm;

    @FindBy(css="button.cancelButton")
    WebElement btnResetCancel;

    // Whether the element is visible within RepaymentsCalcOuter div.
    public static boolean isVisibleInOuterPanel(WebElement we){
        String style = we.getAttribute("style");
        logger.trace("Attribute style='" + style + "'");

        boolean isVisibleStyle = (style == null)
            || (style.isEmpty())
            || (!style.matches("display\\s*:\\s*none[;]"));
        boolean isDisplayed = we.isDisplayed();

        return (isVisibleStyle && isDisplayed);
    }

    //******** Panels of scenarios and adjustment: ********
    @FindBy(css="div.onescenario[id^='Scenario']")
    @CacheLookup
    List<WebElement> scenarios;

    public List<WebElement> getScenarios() {
        return scenarios;
    }

    public WebElement getScenario(int index) {
        return getScenarios().get(index);
    }

    public List<Boolean> getScenariosVisibilities(){
        return getScenarios().stream().map(RepaymentsCal::isVisibleInOuterPanel).collect(Collectors.toList());
    }

    @FindBy(css="div#js-adjustRepayment")
    List<WebElement> adjustRepayment;

    //******** Elements in scenario panel: ********
    // Number between 5,000 and 5,000,000, max length is 10.
    By loanAmountCss = By.cssSelector("input#LoanAmount");
    By tooltipCss = By.cssSelector("input#LoanAmount + span.tooltipIcon");
    By tooltipTextCss = By.cssSelector("div#repaymentLoanTooltip p");
    By btnCalculateCss = By.cssSelector("input[type='submit'][value='Calculate']");
    // The result number on legend label of scenario penal
    By resultLegendCss = By.cssSelector("div.scenarioLegend span#js-repayment");
    // The panel at bottom of scenario panel to show calculated results.
    By resultsPanelCss = By.cssSelector("div.resultsPanel");
    By resultPtagCss = By.cssSelector("div.result p.js-repaymentCalcResult");

    public void setLoadAmountForScenario(String txtAmount, int index){
        WebElement inputAmount = getScenario(index).findElement(loanAmountCss);
        logger.debug("Current amount of scenario " + index + " = " + inputAmount.getText());
        inputAmount.clear();
        logger.debug("Setting load amount of scenario " + index + " to " + txtAmount);
        inputAmount.sendKeys(txtAmount);
    }

    public boolean getResultsPanelVisibilityForScenario(int index){
        return isVisibleInOuterPanel(getScenario(index).findElement(resultsPanelCss));
    }

    public void calculateForScenario(int index){
        getScenario(index).findElement(btnCalculateCss).sendKeys(Keys.RETURN);
        logger.debug("Calculate button clicked on scenario:" + index);
    }

    public String getResultForScenario(int index){
        new FluentWait<WebDriver>(driver)
            .pollingEvery(500, TimeUnit.MILLISECONDS)
            .withTimeout(5, TimeUnit.SECONDS)
            .ignoring(NoSuchElementException.class)
            .until((dr) -> {
                logger.debug("checking calculated rate for scenario: " + index);
                return getResultsPanelVisibilityForScenario(index);
            });

        String res = getScenario(index).findElement(resultsPanelCss).findElement(resultPtagCss).getText();
        // Replace all CR in this value.
        return res.replaceAll("(?:\\n|\\r)", "");
    }

    public String getResultLegendForScenario(int index){
        return getScenario(index).findElement(resultLegendCss).getText();
    }

    //******* Testing Supporting Methods: ********
    public void saveCalculatorScreenshot(){
        saveScreenShot("RepaymentsCalculator", outerCalPanel);
    }

    public RepaymentsCal(WebDriver driver){
        super(driver, urlRegEx, titleRegEx);
    }

    public RepaymentsCal get(){
        get(baseUrl);
        checkUrl();
        checkTitle();
        PageFactory.initElements(driver, this);
        return this;
    }
}
