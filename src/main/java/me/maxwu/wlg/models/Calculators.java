package me.maxwu.wlg.models;

import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.CacheLookup;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

/**
 * The Page Object Model to describe entrance page to mortgage calculators.
 * Link properties to three specific calculators and page transitioning to Repayments and Borrowing
 *     are included. For end-to-end integration scenario testing, this page could act as an entrance
 *     from ANZ mortgage page.
 *
 * There are three calculators accessible from this page:
 *   ✎ Repayments Calculator:
 *       The page model of borrowing is introduced in class "RepayCal".
 *   ✎ Borrowing Calculator:
 *       The page model of borrowing is introduced in class "BorrowCal".
 *   ✎ Moving House Renovating Calculator:
 *       It is not in current test scope. Only a hyperlink text check introduced.
 */
public class Calculators extends PageBase {
    static Logger logger = LoggerFactory.getLogger(Calculators.class.getName());
    private static final String baseUrl = "https://www.anz.co.nz/personal/home-loans-mortgages/mortgage-calculators/";
    static private String urlRegEx = "^https://www.anz.co.nz/personal/home-loans-mortgages/mortgage-calculators[/]$";
    static private String titleRegEx = "^\\s*Mortgage calculators \\| ANZ\\s*$";

    @FindBy(css="#leftnav a[href='/personal/home-loans-mortgages/mortgage-calculators/']")
    @CacheLookup
    WebElement aMortgageCals;

    // First row under calculators link holds the repayments calculator
    @FindBy(css="#leftnav a[href='/personal/home-loans-mortgages/mortgage-calculators/'] + ul > li:nth-child(1) > a:nth-child(2)")
    @CacheLookup
    WebElement aRepaymentsCal;

    // Second row under calculators link holds the borrowing calculator
    @FindBy(css="#leftnav a[href='/personal/home-loans-mortgages/mortgage-calculators/'] + ul > li:nth-child(2) > a:nth-child(2)")
    @CacheLookup
    WebElement aBorrowingCal;

    @FindBy(css="#leftnav a[href='/personal/home-loans-mortgages/mortgage-calculators/'] +ul a + a")
    @CacheLookup
    List<WebElement> aTagsUnderMortgageCals;

    @FindBy(css="div#contentWrapper div#contentstart + h1")
    @CacheLookup
    WebElement h1Mortgage;

    public String getMortgageCalsText(){
        return aMortgageCals.getText();
    }

    public String getRepaymentsCalText(){
        return aRepaymentsCal.getText();
    }

    public String getBorrowingCalText(){
        return aBorrowingCal.getText();
    }

    public List<String> getAtagTextUnderMortgageCals(){
        return aTagsUnderMortgageCals
            .stream()
            .map(WebElement::getText)
            .collect(Collectors.toList());
    }

    public String getH1Mortgage(){
        return h1Mortgage.getText();
    }

    public RepayCal getRepaymentsPage(){
        aRepaymentsCal.sendKeys(Keys.RETURN);
        return new RepayCal(driver);
    }

    public BorrowCal getBorrowingPage(){
        aBorrowingCal.sendKeys(Keys.RETURN);
        return new BorrowCal(driver);
    }

    public Calculators(WebDriver driver){
        super(driver, urlRegEx, titleRegEx);
        PageFactory.initElements(driver, this);
    }

    public Calculators get(){
        get(baseUrl);
        checkUrl();
        checkTitle();
        return this;
    }

}
