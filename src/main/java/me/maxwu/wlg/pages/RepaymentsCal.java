package me.maxwu.wlg.pages;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RepaymentsCal extends PageBase {
    static Logger logger = LoggerFactory.getLogger(Calculators.class.getName());
    private static final String baseUrl = "https://www.anz.co.nz/personal/home-loans-mortgages/mortgage-calculators/";
    static private String urlRegEx = "https://www.anz.co.nz/personal/home-loans-mortgages/mortgage-calculators/";
    static private String titleRegEx = "^\\s*Mortgage calculators \\| ANZ\\s*$";

    public RepaymentsCal(WebDriver driver){
        super(driver, urlRegEx, titleRegEx);
    }
}
