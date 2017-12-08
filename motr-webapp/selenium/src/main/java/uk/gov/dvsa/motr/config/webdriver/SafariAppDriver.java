package uk.gov.dvsa.motr.config.webdriver;

import org.openqa.selenium.By;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Keyboard;
import org.openqa.selenium.interactions.Mouse;
import org.openqa.selenium.safari.SafariDriver;

import java.util.List;
import java.util.Set;

public class SafariAppDriver extends BaseAppDriver {

    public SafariAppDriver(SafariDriver safariDriver){
        super(safariDriver);
    }
    @Override
    public WebElement findElementByClassName(String s) {
        return remoteWebDriver.findElementByClassName(s);
    }

    @Override
    public List<WebElement> findElementsByClassName(String s) {
        return remoteWebDriver.findElementsByClassName(s);
    }

    @Override
    public WebElement findElementByCssSelector(String s) {
        return remoteWebDriver.findElementByCssSelector(s);
    }

    @Override
    public List<WebElement> findElementsByCssSelector(String s) {
        return remoteWebDriver.findElementsByCssSelector(s);
    }

    @Override
    public WebElement findElementById(String s) {
        return remoteWebDriver.findElementById(s);
    }

    @Override
    public List<WebElement> findElementsById(String s) {
        return remoteWebDriver.findElementsById(s);
    }

    @Override
    public WebElement findElementByLinkText(String s) {
        return remoteWebDriver.findElementByLinkText(s);
    }

    @Override
    public List<WebElement> findElementsByLinkText(String s) {
        return remoteWebDriver.findElementsByLinkText(s);
    }

    @Override
    public WebElement findElementByPartialLinkText(String s) {
        return remoteWebDriver.findElementByPartialLinkText(s);
    }

    @Override
    public List<WebElement> findElementsByPartialLinkText(String s) {
        return remoteWebDriver.findElementsByPartialLinkText(s);
    }

    @Override
    public WebElement findElementByName(String s) {
        return remoteWebDriver.findElementByName(s);
    }

    @Override
    public List<WebElement> findElementsByName(String s) {
        return remoteWebDriver.findElementsByName(s);
    }

    @Override
    public WebElement findElementByTagName(String s) {
        return remoteWebDriver.findElementByTagName(s);
    }

    @Override
    public List<WebElement> findElementsByTagName(String s) {
        return remoteWebDriver.findElementsByTagName(s);
    }

    @Override
    public WebElement findElementByXPath(String s) {
        return remoteWebDriver.findElementByXPath(s);
    }

    @Override
    public List<WebElement> findElementsByXPath(String s) {
        return remoteWebDriver.findElementsByXPath(s);
    }

    @Override
    public Capabilities getCapabilities() {
        return remoteWebDriver.getCapabilities();
    }

    @Override
    public Keyboard getKeyboard() {
        return remoteWebDriver.getKeyboard();
    }

    @Override
    public Mouse getMouse() {
        return remoteWebDriver.getMouse();
    }

    @Override
    public Object executeScript(String s, Object... objects) {
        return remoteWebDriver.executeScript(s, objects);
    }

    @Override
    public Object executeAsyncScript(String s, Object... objects) {
        return remoteWebDriver.executeAsyncScript(s, objects);
    }

    @Override
    public <X> X getScreenshotAs(OutputType<X> outputType) throws WebDriverException {
        return remoteWebDriver.getScreenshotAs(outputType);
    }

    @Override
    public void get(String s) {
        remoteWebDriver.get(s);
    }

    @Override
    public String getCurrentUrl() {
        return remoteWebDriver.getCurrentUrl();
    }

    @Override
    public String getTitle() {
        return remoteWebDriver.getTitle();
    }

    @Override
    public List<WebElement> findElements(By by) {
        return remoteWebDriver.findElements(by);
    }

    @Override
    public WebElement findElement(By by) {
        return remoteWebDriver.findElement(by);
    }

    @Override
    public String getPageSource() {
        return remoteWebDriver.getPageSource();
    }

    @Override
    public void close() {
        remoteWebDriver.close();
    }

    @Override
    public void quit() {
        remoteWebDriver.quit();
    }

    @Override
    public Set<String> getWindowHandles() {
        return remoteWebDriver.getWindowHandles();
    }

    @Override
    public String getWindowHandle() {
        return remoteWebDriver.getWindowHandle();
    }

    @Override
    public TargetLocator switchTo() {
        return remoteWebDriver.switchTo();
    }

    @Override
    public Navigation navigate() {
        return remoteWebDriver.navigate();
    }

    @Override
    public Options manage() {
        return remoteWebDriver.manage();
    }
}
