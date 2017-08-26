package me.maxwu.wlg.models;


import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Page Object Model for Repayments Calculator.
 * This model has a main functioning panel(div) called outer calculator.
 *
 * Outer Calculator Panel Structure:
 * +-> Scenario * 3
 *     # by default only 1st scenario is visible
 *     # following scenarios can be added thru controls on the right adjustment panel
 *     # following scenarios can be "removed" back to invisible state
 *     # each scenario has its own calculation button, result panel, tooltip and comparison tip.
 *     +-> loan amount, tooltip, frequency, interest rate & rate selection, length
 *     +-> (from 2nd on) control to remove current scenario
 *     +-> button to calculate
 *     +-> control to clear values
 *         # clear control is only visible on following scenarios, not for the 1st
 *     +-> legends on top position of inner calculator panel
 *         +-> scenario plus id
 *         +-> calculated result
 *             # if clear button is visible, clearing action will purge legend result
 *     +-> calculated result
 *         +-> rate in number
 *         +-> frequency
 *         +-> summary
 *             +-> amount, interest, total cost
 *     +-> comparison tooltip
 *
 * +-> Adjustment Panel
 *     +-> minimum repayments per selected ferquency
 *     +-> a slider if it is the only scenario
 *         # if there is only 1 scenario, it must be the 1st one
 *     # linkage between slider, saved money tips, and, results in bottom list.
 *
 *     +-> buttons to add/duplicate scenarios
 *         # (1) if only 1st scenario is visible, only button is "add this as scenario"
 *         # (2) with two scenarios, "duplicates"(on 1st and 2nd) and "add a blank scenario"
 *         #     are visible.
 *         +-> button(s) to duplicate visible scenario(s)
 *         +-> button to add new blank scenario
 *
 * +-> Common Controls
 *     +-> reset, print for repayments
 *         # reset triggers a modal dialog to confirm
 *
 * Page model design describes the above structure and attached functions.
 * The test design includes:
 *  ✎ functional testing on panels,                        => In progress
 *    +-> element functioning
 *    +-> interactive Web GUI, visibility, event binding
 *
 *  ✎ the integrated calculation story test, and,          => Not started
 *    +->
 *
 *  ✎ the rainy day tests.                                 => Not started
 *    +-> protective inputs/click
 *    +-> error tips
 *    +-> edges or known limit
 *
 * INFO: As a sample suite, it generates sanity stories on general visibilities check
 *     and functional test. However, the detailed components and event/linkage are
 *     not fully covered. Here is a list of not covered areas for further backlog analysis.
 *
 *   ✍ Comparison and comparison tooltips are not covered by current baseline.
 *   ✍ Clearing values on 2nd and 3rd scenario panels are not covered neither.
 *   ✍ Panel removing buttons are not covered.
 *   ✍ Reset and print button is not in this suite.
 *   ✍ Rainy day test sample on Repayments Calculator is not planned with current delivery.
 *     Sample rainy day test is introduced on Borrowing Calculator.
 */
public class RepayCal extends PageBase {
    private static Logger logger = LoggerFactory.getLogger(RepayCal.class.getName());
    private static final String baseUrl = "https://tools.anz.co.nz/home-loans/repayments-calculator/";
    private static String urlRegEx = "^https://tools.anz.co.nz/home-loans/repayments-calculator/?$";
    private static String titleRegEx = "^\\s*Repayments Calculator \\| What will my home loan repayments be\\? \\| ANZ Store\\s*$";
    private static int pollingIntervalMillis = 250;
    private static int pollingTimeOutSecond = 5;

    //******** Calculator Outer Panel: ********
    @FindBy(css="div#RepaymentCalculatorOuter")
    private WebElement outerCalPanel;

    @FindBy(css="i.icon-reset")
    @CacheLookup
    private WebElement btnReset;

    @FindBy(css="button.resetButton")
    private WebElement btnResetConfirm;

    @FindBy(css="button.cancelButton")
    private WebElement btnResetCancel;

    // The original static method was refactored to CalUtils class.
    public static boolean isVisibleOnCalculator(WebElement we){
        return CalUtils.isVisibleOnCalculator(we);
    }

    public void resetCal(){
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnReset);
        btnReset.click();
        btnResetConfirm.click();
    }

    // ******** Panels of scenarios and adjustment: ********
    //
    @FindBy(css="div#js-adjustRepayment")
    private List<WebElement> adjustRepayment;

    private By scenariosCss = By.cssSelector("div[id^='Scenario']");

    public List<WebElement> getScenarios() {
        List<WebElement> scenarios;
        new FluentWait<>(driver)
            .pollingEvery(pollingIntervalMillis, TimeUnit.MILLISECONDS)
            .withTimeout(pollingTimeOutSecond, TimeUnit.SECONDS)
            .ignoring(NoSuchElementException.class)
            .ignoring(StaleElementReferenceException.class)
            .until((driver) -> {
                logger.debug("checking scenario panels");
                return (0 < driver.findElements(scenariosCss).size());
            });
        scenarios =  driver.findElements(scenariosCss);
        logger.debug("Scenario panel number=" + scenarios.size());
        return scenarios;
    }

    public WebElement getScenario(int index) {
        return getScenarios().get(index);
    }

    public List<Boolean> getScenariosVisibilities(){
        return getScenarios().stream().map(RepayCal::isVisibleOnCalculator).collect(Collectors.toList());
    }

    // ******** Elements in scenario panel: ********
    //
    // Backlog to 21/08 sprint: extract and decorate a Scenario type
    //     to handle this block, offer grouped interfaces and cache elements.
    //     In addition, consider about extract result panel in next review.

    // LoanAmount: Number between 5,000 and 5,000,000, max length is 10.
    private By loanAmountCss = By.cssSelector("input#LoanAmount");

    private By tooltipCss = By.cssSelector("input#LoanAmount + span.tooltipIcon");
    private By tooltipTextCss = By.cssSelector("div#repaymentLoanTooltip");
    private By interestRateCss = By.cssSelector("input[name='InterestRate']");
    private By viewRatesCss = By.cssSelector("div#dd div.selector");
    // In total 9 values in dropdown list to support above selector.
    private By viewRatesValuesCss = By.cssSelector("div#dd div.dropdown ul.dropdownvalues li");

    private By loanLengthCss = By.cssSelector("select#LoanLength");
    private By btnCalculateCss = By.cssSelector("input[type='submit'][value='Calculate']");
    // The result number on legend label of scenario penal
    private By resultLegendCss = By.cssSelector("div.scenarioLegend span#js-repayment");
    // The panel at bottom of scenario panel to show calculated results.
    private By resultsPanelCss = By.cssSelector("div.resultsPanel");
    // The remove button of current panel. Only working in 2nd and 3rd scenarios.
    // WARN: If it is visible on scenario[0], which indicates a defect.
    private By btnRemoveCss = By.cssSelector("span#js-remove-scenario-btn");

    public boolean isBtnRemoveVisibleForScenario(int index){
        return isVisibleOnCalculator(getScenario(index).findElement(btnRemoveCss));
    }

    /**
     * Hide the corresponding scenario panel from outer calculator div.
     * @param index Scenario id
     */
    public void removeScenario(int index){
        getScenario(index).findElement(btnRemoveCss).click();
    }

    private Select getSelectLoanLengthForScenario(int index){
        Select select = new Select(getScenario(index).findElement(loanLengthCss));
        return select;
    }

    public void setLoanLengthForScenarioByValue(int index, String value){
        getSelectLoanLengthForScenario(index).selectByValue(value);
    }

    public List<String> getLoanLengthForScenarioSelected(int index){
        return getSelectLoanLengthForScenario(index)
            .getAllSelectedOptions()
            .stream().map(WebElement::getText)
            .collect(Collectors.toList());
    }

    private WebElement getInterestRateElementForScenario(int index){
        return getScenario(index).findElement(interestRateCss);
    }

    public String getInterestRateForScenario(int index){
        return getInterestRateElementForScenario(index).getText();
    }

    public void setInterestRateForScenario(int index, String interestRate){
        getInterestRateElementForScenario(index).sendKeys(interestRate);
    }

    public void setLoanAmountForScenario(int index, String txtAmount){
        WebElement inputAmount = getScenario(index).findElement(loanAmountCss);
        logger.debug("Current amount of scenario " + index + " = '" + inputAmount.getText() +"'");
        inputAmount.clear();
        logger.debug("Setting load amount of scenario " + index + " to " + txtAmount);
        // Introduce action chain to adapt FF v62 + Gecko v0.18.
        // Original straight way: inputAmount.sendKeys(txtAmount);
        new Actions(driver).moveToElement(inputAmount).sendKeys(txtAmount).build().perform();
    }

    public String getLoanAmountForScenario(int index){
        WebElement scenario = getScenario(index);
        WebElement inputLoanAmount = new FluentWait<>(driver)
            .pollingEvery(pollingIntervalMillis, TimeUnit.MILLISECONDS)
            .withTimeout(pollingTimeOutSecond, TimeUnit.SECONDS)
            .ignoring(NoSuchElementException.class)
            .ignoring(StaleElementReferenceException.class)
            .until((driver) -> {
                logger.debug("checking loan amount input for scenario: " + index);
                return (scenario.findElement(loanAmountCss));
            });
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", inputLoanAmount);
        return inputLoanAmount.getAttribute("value");
    }

    public boolean isResultsPanelVisibleForScenario(int index){
        return isVisibleOnCalculator(getResultPanelForScenario(index));
    }

    public void calculateForScenario(int index){
        getScenario(index).findElement(btnCalculateCss).sendKeys(Keys.RETURN);
        logger.debug("Calculate button clicked on scenario:" + index);
    }

    public void clickTooltipForScenario(int index){
        new Actions(driver).moveToElement(getScenario(index).findElement(tooltipCss))
            .click().build().perform();
    }

    public boolean isTooltipVisibleForScenario(int index){
        return isVisibleOnCalculator(getScenario(index).findElement(tooltipTextCss));
    }

    public boolean isScenarioLegendVisibleForScenario(int index){
        return isVisibleOnCalculator(getScenario(index).findElement(scenarioLegendCss));
    }
    public String getScenarioLegendForScenario(int index){
        return getScenario(index).findElement(scenarioLegendCss).getText();
    }

    // ******** Elements in results panel: ********
    //
    private By resultPtagCss = By.cssSelector("div.result p.js-repaymentCalcResult");
    private By scenarioLegendCss = By.cssSelector("legend.scenarioLegend");
    private By resultAmountCss = By.cssSelector("div.resultsPanel label[for='LoanAmount'] + p");

    /**
     * Get loan amount for a given scenario panel.
     * For new added scenario, the field take a fade-in style to become accessible.
     * A fluent wait is introduced to hold a limited try-wait on element.
     *
     * @param index Scenario Panel id
     * @return The string value of loan amount on given scenario panel
     */
    public String getResultAmountForScenario(int index){
        new FluentWait<WebDriver>(driver)
            .pollingEvery(pollingTimeOutSecond, TimeUnit.MILLISECONDS)
            .withTimeout(pollingTimeOutSecond, TimeUnit.SECONDS)
            .ignoring(NoSuchElementException.class)
            .until((dr) -> {
                logger.debug("checking result loan amount for scenario: " + index);
                return (getScenario(index).findElement(resultAmountCss).getText() != null);
            });

        return getScenario(index).findElement(resultAmountCss).getText();
    }

    // Interests amounts are calculated in monthly, fortnightly and weekly by design.
    // So total costs are also offered in monthly, fortnightly and weekly by design.
    // One among them is visible to end users.
    private By resultTotalInterestItems = By.cssSelector(
        "label[for='ScenarioDatas_monthly__TotalInterest'] ~ p"
    );
    private By resultTotalCostItems = By.cssSelector(
        "label[for='ScenarioDatas_monthly__TotalCost'] ~ p"
    );

    private WebElement getResultPanelForScenario(int index){
        return getScenario(index).findElement(resultsPanelCss);
    }

    /**
     * Pre-analysis check shows that monthly, fortnightly and weekly interests are all
     *     calculated and only one of them is visible. So does total cost.
     * Return visible total interests values in List of String.
     * WARN: The caller shall check that only one element is returned in List.
     *       If there are multiple visible values returned, it indicates a defect.
     */
    public List<String> getTotalInterestForScenario(int index){
        return getResultPanelForScenario(index)
            .findElements(resultTotalInterestItems)
            .stream()
            .filter(RepayCal::isVisibleOnCalculator)
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }

    /**
     * Return visible total cost values in List of String.
     * WARN: The caller shall check that only one element is returned in List.
     *       If there are multiple visible values returned, it indicates a defect.
     */
    public List<String> getTotalCostForScenario(int index){
        return getResultPanelForScenario(index)
            .findElements(resultTotalCostItems)
            .stream()
            .filter(RepayCal::isVisibleOnCalculator)
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }

    /**
     * As the computation density task to get result on given scenario panel,
     *     current method getResultForScenario() has carried a fluent wait up to 5s
     *     and 250ms (as defined in pollingIntervalMillis and pollingTimeOutSecond )
     *     check interval to adapt slow reacting situations.
     *     However, it is recommended to figure out performance criteria with BA
     *     or through test statistics over public network (e.g. 3 sigma tolerance
     *     from middle response time on cloud based testing).
     * INFO: With the refactoring task to abstract Scenario as a type, the attribute
     *      getter with fluent wait shall be extracted to CalUtils.
     * @return: The string value on GUI with all CR chars removed.
     */
    public String getResultForScenario(int index){
        new FluentWait<WebDriver>(driver)
            .pollingEvery(pollingIntervalMillis, TimeUnit.MILLISECONDS)
            .withTimeout(pollingTimeOutSecond, TimeUnit.SECONDS)
            .ignoring(NoSuchElementException.class)
            .until((dr) -> {
                logger.debug("checking results visibilities for scenario: " + index);
                return (isResultsPanelVisibleForScenario(index) && isScenarioLegendVisibleForScenario(index));
            });

        String res = getResultPanelForScenario(index).findElement(resultPtagCss).getText();
        // Remove CR in this string value.
        return res.replaceAll("(?:\\n|\\r)", "");
    }

    public String getResultLegendForScenario(int index){
        return getScenario(index).findElement(resultLegendCss).getText();
    }

    // ******** Elements in adjustment panel: ********
    //
    @FindBy(css="input[name='repaymentAdjustment']")
    private WebElement inputAdjustment;

    @FindBy(css="div#js-repaymentSlider div")
    private WebElement sliderAdjustment;

    // 2 Scenario adding buttons when only first scenario is visible
    @FindBy(css="span.scenario.btn.add#js-add-as-scenario i")
    @CacheLookup
    private WebElement btnAddThisAsAScenario;
    @FindBy(css="span.scenario.btn.add.createScenario#js-add-as-scenario i")
    @CacheLookup
    private WebElement btnCreateANewScenario;

    // 3 Scenario adding buttons when first and second scenarios are visible
    @FindBy(css="div#js-add-another-scenario-panel span#js-duplicate-scenario-1 i")
    @CacheLookup
    private WebElement btnDuplicateScenario1;
    @FindBy(css="div#js-add-another-scenario-panel span#js-duplicate-scenario-2 i")
    @CacheLookup
    private WebElement btnDuplicateScenario2;
    @FindBy(css="div#js-add-another-scenario-panel span#js-new-scenario")
    @CacheLookup
    private WebElement btnAddABlankScenario;

    /**
     * Click the button on adjustment panel when only 1st scenario is visible.
     * The 2nd scenario panel will be visible after the click to show a candidate calculator GUI.
     */
    public void addThisAsAScenario(){
        new Actions(driver).moveToElement(btnAddThisAsAScenario).click().build().perform();
    }

    /**
     * Click the button on adjustment panel when only 1st scenario is visible.
     * The 2nd scenario panel will be visible after the click to show a candidate calculator GUI.
     */
    public void createANewScenario(){
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnCreateANewScenario);
        btnCreateANewScenario.click();
    }

    /**
     * Duplicating buttons are only visible when there are two scenarios to choose.
     */
    public void duplicateScenario1(){
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnDuplicateScenario1);
        btnDuplicateScenario1.click();
    }

    public void duplicateScenario2(){
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", btnDuplicateScenario2);
        btnDuplicateScenario2.click();
    }

    //******* General Test Supports: ********
    //
    public void saveCalculatorScreenshot(){
        saveScreenShot("RepaymentsCalculator", outerCalPanel);
    }

    public RepayCal(WebDriver driver){
        super(driver, urlRegEx, titleRegEx);
        PageFactory.initElements(driver, this);
    }

    public RepayCal get(){
        get(baseUrl);
        checkUrl();
        checkTitle();
        return this;
    }
}
