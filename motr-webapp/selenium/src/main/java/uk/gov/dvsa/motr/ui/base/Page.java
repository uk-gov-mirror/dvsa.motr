package uk.gov.dvsa.motr.ui.base;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;

import uk.gov.dvsa.motr.WebDriverConfiguratorRegistry;
import uk.gov.dvsa.motr.config.webdriver.BaseAppDriver;
import uk.gov.dvsa.motr.elements.DvsaElementLocatorFactory;
import uk.gov.dvsa.motr.elements.DvsaWebElement;
import uk.gov.dvsa.motr.elements.FindElementLocator;

public abstract class Page {

    protected BaseAppDriver driver;

    @FindBy(tagName = "h1")
    protected WebElement title;

    @FindBy(id = "cookiesLink")
    protected WebElement cookiesLink;

    @FindBy(id = "terms-and-conditions-link")
    protected WebElement termsAndConditionsLink;

    @FindBy(id = "privacy-policy-link")
    protected WebElement privacyPolicyLink;

    public Page() {

        this.driver = WebDriverConfiguratorRegistry.get().getDriver();
        DvsaElementLocatorFactory factory = new DvsaElementLocatorFactory(driver);
        PageFactory.initElements(factory, this);
        selfVerify();
    }

    public String getTitle() {

        return title.getText();
    }

    protected void selfVerify() {

        if (!getTitle().contains(getContentHeader()) || !this.driver.getTitle().equals(getPageTitle())) {

            throw new PageIdentityVerificationException("Page identity verification failed: " +
                    String.format("\n Expected: %s page, \n Found: %s page, \n with expected page " +
                                    "title of: %s \n and actual page title of: %s",
                    getContentHeader(), getTitle(), getPageTitle(), this.driver.getTitle())
            );
        }
    }

    protected abstract String getPageTitle();

    protected abstract String getContentHeader();

    @Override
    public final String toString() {

        return "Page: " + getTitle();
    }


    protected String getElementText(By selector) {

        try {
            return driver.findElement(selector).getText();
        } catch (StaleElementReferenceException ex) {
            return getElementText(selector);
        }
    }

    protected Boolean isElementVisible(By selector) {

        try {
            return driver.findElement(selector).isDisplayed();
        } catch (StaleElementReferenceException ex) {
            return isElementVisible(selector);
        }
    }

    protected void clickElement(By selector) {

        try {
            driver.findElement(selector).click();
        } catch (StaleElementReferenceException ex) {
            clickElement(selector);
        }
    }

    protected WebElement getElement(By selector) {

        try {
            return DvsaWebElement.wrap(driver.findElement(selector), new FindElementLocator(driver, selector));
        } catch (StaleElementReferenceException ex) {
            return getElement(selector);
        }
    }
}
