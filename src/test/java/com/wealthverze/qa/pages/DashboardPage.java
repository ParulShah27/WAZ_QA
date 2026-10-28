package com.wealthverze.qa.pages;

import com.wealthverze.qa.core.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * Authenticated shell. APP_SHELL is present on every signed-in page, including
 * deep links such as /holdings; ROOT is only on the dashboard itself.
 */
public class DashboardPage {

    public static final By APP_SHELL = By.cssSelector("[data-testid='app-shell']");
    public static final By ROOT = By.cssSelector("[data-testid='dashboard-root']");
    public static final By FAMILY_SWITCH = By.cssSelector("[data-testid='family-switch']");
    public static final By GREETING = By.cssSelector("[data-testid='user-greeting']");
    public static final By PROFILE_TRIGGER = By.cssSelector("[data-testid='profile-trigger']");
    public static final By SIGN_OUT = By.cssSelector("[data-testid='sign-out']");
    public static final By NOT_FOUND = By.cssSelector("[data-testid='page-not-found']");

    private final WebDriver driver;
    private final Waits waits;

    public DashboardPage(WebDriver driver) {
        this.driver = driver;
        this.waits = new Waits(driver);
    }

    public void waitUntilSignedIn() {
        waits.visible(APP_SHELL);
    }

    public void waitUntilLoaded() {
        waits.visible(ROOT);
    }

    public boolean isLoaded() {
        return new LoginPage(driver).isDisplayed(ROOT);
    }

    public boolean isSignedIn() {
        return new LoginPage(driver).isDisplayed(APP_SHELL);
    }

    public boolean hasFamilySwitch() {
        return new LoginPage(driver).isDisplayed(FAMILY_SWITCH);
    }

    public boolean isNotFoundShown() {
        return new LoginPage(driver).isDisplayed(NOT_FOUND);
    }

    public String greeting() {
        return waits.visible(GREETING).getText().trim();
    }

    public void signOut() {
        waits.clickable(PROFILE_TRIGGER).click();
        waits.clickable(SIGN_OUT).click();
    }
}
