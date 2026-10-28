package com.wealthverze.qa.pages;

import com.wealthverze.qa.core.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * "One more thing" step shown after sign-in when the account has only one method linked.
 */
public class AccountLinkPage {

    public static final By HEADING = By.cssSelector("[data-testid='link-heading']");
    public static final By TEXT = By.cssSelector("[data-testid='link-text']");
    public static final By LINK_GOOGLE = By.cssSelector("[data-testid='link-google-button']");
    public static final By NOT_NOW = By.cssSelector("[data-testid='link-not-now']");

    private final WebDriver driver;
    private final Waits waits;

    public AccountLinkPage(WebDriver driver) {
        this.driver = driver;
        this.waits = new Waits(driver);
    }

    public boolean isShown() {
        return new LoginPage(driver).isDisplayed(HEADING);
    }

    public String heading() {
        return waits.visible(HEADING).getText().trim();
    }

    public String text() {
        return waits.visible(TEXT).getText().trim();
    }

    public String linkGoogleLabel() {
        return waits.visible(LINK_GOOGLE).getText().trim();
    }

    public String notNowLabel() {
        return waits.visible(NOT_NOW).getText().trim();
    }

    public void clickNotNow() {
        waits.clickable(NOT_NOW).click();
    }
}
