package me.maxwu.wlg.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.stream.Collectors;

public class Calculators extends PageBase {
    static Logger logger = LoggerFactory.getLogger(Calculators.class.getName());
    private static final String baseUrl = "https://www.anz.co.nz/personal/home-loans-mortgages/mortgage-calculators/";
    static private String urlRegEx = "https://www.anz.co.nz/personal/home-loans-mortgages/mortgage-calculators/";
    static private String titleRegEx = "^\\s*Mortgage calculators \\| ANZ\\s*$";

    @FindBy(how= How.CSS, using="#leftnav a[href='/personal/home-loans-mortgages/mortgage-calculators/']")
    WebElement aMortgageCals;

    // First row under calculators link holds the repayments calculator
    @FindBy(how= How.CSS, using="#leftnav a[href='/personal/home-loans-mortgages/mortgage-calculators/'] + ul > li:nth-child(1) > a:nth-child(2)")
    WebElement aRepaymentsCal;

    // Second row under calculators link holds the borrowing calculator
    @FindBy(how= How.CSS, using="#leftnav a[href='/personal/home-loans-mortgages/mortgage-calculators/'] + ul > li:nth-child(2) > a:nth-child(2)")
    WebElement aBorrowingCal;

    @FindBy(how=How.CSS, using="#leftnav a[href='/personal/home-loans-mortgages/mortgage-calculators/'] +ul a + a")
    List<WebElement> aTagsUnderMortgageCals;


    @FindBy(how=How.CSS, using="div#contentWrapper div#contentstart + h1")
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
        return aTagsUnderMortgageCals.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    public String getH1Mortgage(){
        return h1Mortgage.getText();
    }

    public Calculators(WebDriver driver){
        super(driver, urlRegEx, titleRegEx);
    }

    public Calculators get(){
        get(baseUrl);
        checkUrl();
        checkTitle();
        PageFactory.initElements(driver, this);
        return this;
    }

}
