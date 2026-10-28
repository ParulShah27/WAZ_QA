package com.wealthverze.qa.pages;

import com.wealthverze.qa.core.Config;
import com.wealthverze.qa.core.Inputs;
import com.wealthverze.qa.core.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Phone-entry sign-in screen.
 *
 * Locator contract: the frontend must expose these data-testid values. They are
 * placeholders until the DOM of the Vite build on localhost:5173 is confirmed;
 * change them here only, never in steps or feature files.
 */
public class LoginPage {

    public static final By BRAND_PANEL = By.cssSelector("[data-testid='brand-panel']");
    public static final By LOGO = By.cssSelector("[data-testid='brand-logo']");
    public static final By HEADING = By.cssSelector("[data-testid='signin-heading']");
    public static final By SUBTITLE = By.cssSelector("[data-testid='signin-subtitle']");
    public static final By MOBILE_INPUT = By.cssSelector("[data-testid='mobile-input']");
    public static final By MOBILE_LABEL = By.cssSelector("[data-testid='mobile-label']");
    public static final By COUNTRY_CODE = By.cssSelector("[data-testid='country-code']");
    public static final By SEND_CODE = By.cssSelector("[data-testid='send-code']");
    public static final By SENDING_SPINNER = By.cssSelector("[data-testid='send-code-spinner']");
    public static final By GOOGLE_BUTTON = By.cssSelector("[data-testid='google-signin']");
    public static final By TERMS_LINK = By.cssSelector("[data-testid='terms-link']");
    public static final By PRIVACY_LINK = By.cssSelector("[data-testid='privacy-link']");
    public static final By THEME_TOGGLE = By.cssSelector("[data-testid='theme-toggle']");
    public static final By RECAPTCHA = By.cssSelector("[data-testid='recaptcha-widget']");
    public static final By DISCLAIMER = By.cssSelector("[data-testid='signin-disclaimer']");
    /** Shared by the phone and OTP screens, which both announce errors with role="alert". */
    public static final By AUTH_ERROR = By.cssSelector("[data-testid='auth-error']");

    private final WebDriver driver;
    private final Waits waits;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.waits = new Waits(driver);
    }

    public void open() {
        driver.get(Config.appUrl());
        waits.visible(MOBILE_INPUT);
    }

    public WebElement mobileInput() {
        return waits.visible(MOBILE_INPUT);
    }

    public void enterMobile(String value) {
        Inputs.replace(mobileInput(), value);
    }

    public void pasteMobile(String value) {
        Inputs.paste(driver, mobileInput(), value);
    }

    public void typeKeyByKey(String value) {
        Inputs.clear(mobileInput());
        Inputs.typeKeyByKey(mobileInput(), value);
    }

    public void typeAtEnd(String value) {
        mobileInput().sendKeys(value);
    }

    public void deleteLastDigit() {
        mobileInput().sendKeys(Keys.BACK_SPACE);
    }

    public void pressEnterInMobile() {
        mobileInput().sendKeys(Keys.ENTER);
    }

    public String mobileValue() {
        return mobileInput().getAttribute("value");
    }

    public boolean isMobileFocused() {
        return driver.switchTo().activeElement().equals(mobileInput());
    }

    public boolean isMobileEnabled() {
        return mobileInput().isEnabled();
    }

    public String mobileInputMode() {
        return mobileInput().getAttribute("inputmode");
    }

    public String mobileType() {
        return mobileInput().getAttribute("type");
    }

    public String mobilePlaceholder() {
        return mobileInput().getAttribute("placeholder");
    }

    public String mobileLabel() {
        return waits.visible(MOBILE_LABEL).getText().trim();
    }

    public boolean isSendCodeEnabled() {
        return waits.visible(SEND_CODE).isEnabled();
    }

    public void clickSendCode() {
        waits.clickable(SEND_CODE).click();
    }

    /** Used where the spec says the control should be disabled: a click on a disabled button is a no-op. */
    public void clickSendCodeIfEnabled() {
        WebElement button = waits.visible(SEND_CODE);
        if (button.isEnabled()) {
            button.click();
        }
    }

    /** Attempts other controls while a request is in flight. Failures are expected and ignored. */
    public void tryOtherControlsWhileSending() {
        try {
            typeAtEnd("1");
        } catch (RuntimeException ignored) {
            // field disabled while sending
        }
        try {
            googleButton().click();
        } catch (RuntimeException ignored) {
            // button disabled while sending
        }
        clickSendCodeIfEnabled();
    }

    public boolean isSendCodeLoading() {
        return isDisplayed(SENDING_SPINNER) || "true".equals(waits.visible(SEND_CODE).getAttribute("aria-busy"));
    }

    public WebElement googleButton() {
        return waits.visible(GOOGLE_BUTTON);
    }

    public boolean isGoogleEnabled() {
        return googleButton().isEnabled();
    }

    public void clickGoogle() {
        waits.clickable(GOOGLE_BUTTON).click();
    }

    public boolean isGoogleShown() {
        return isDisplayed(GOOGLE_BUTTON);
    }

    public void clickLink(By link) {
        waits.clickable(link).click();
    }

    public void toggleTheme() {
        waits.clickable(THEME_TOGGLE).click();
    }

    public String theme() {
        return (String) ((JavascriptExecutor) driver).executeScript(
                "return document.documentElement.getAttribute('data-theme')"
                        + " || (document.documentElement.classList.contains('dark') ? 'dark' : 'light');");
    }

    public String text(By locator) {
        return waits.visible(locator).getText().trim();
    }

    public boolean isErrorShown() {
        return isDisplayed(AUTH_ERROR);
    }

    public String errorText() {
        return text(AUTH_ERROR);
    }

    public String errorRole() {
        return waits.visible(AUTH_ERROR).getAttribute("role");
    }

    public boolean isDisplayed(By locator) {
        return driver.findElements(locator).stream().anyMatch(WebElement::isDisplayed);
    }

    public boolean isOnPhoneScreen() {
        return isDisplayed(MOBILE_INPUT);
    }
}
