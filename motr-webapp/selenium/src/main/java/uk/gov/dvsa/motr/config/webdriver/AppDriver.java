package uk.gov.dvsa.motr.config.webdriver;

import org.openqa.selenium.HasCapabilities;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.HasInputDevices;
import org.openqa.selenium.internal.FindsByClassName;
import org.openqa.selenium.internal.FindsByCssSelector;
import org.openqa.selenium.internal.FindsById;
import org.openqa.selenium.internal.FindsByLinkText;
import org.openqa.selenium.internal.FindsByName;
import org.openqa.selenium.internal.FindsByTagName;
import org.openqa.selenium.internal.FindsByXPath;

public interface AppDriver extends
        WebDriver, JavascriptExecutor, FindsById, FindsByClassName,
        FindsByLinkText, FindsByName, FindsByCssSelector, FindsByTagName,
        FindsByXPath, HasInputDevices, HasCapabilities, TakesScreenshot {
    String getPageSource();

    void takeScreenShot(String destinationPath);
}
