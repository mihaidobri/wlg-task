package me.maxwu.wlg.models;

import org.openqa.selenium.WebElement;

public interface Snapable {
    void saveScreenShot(String caseName);
    void saveScreenShot(String name, WebElement we);
}
