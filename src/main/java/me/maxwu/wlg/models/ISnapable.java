package me.maxwu.wlg.models;

import org.openqa.selenium.WebElement;

/**
 * Interface to describe screenshot contract.
 * A <b>full</b> page screenshot and a component screenshot signature are defined.
 */
public interface ISnapable {
    void saveScreenShot(String caseName);
    void saveScreenShot(String name, WebElement we);
}
