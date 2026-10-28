package com.wealthverze.qa.pages;

import com.wealthverze.qa.core.Config;
import com.wealthverze.qa.core.Inputs;
import com.wealthverze.qa.core.Waits;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;

import java.time.Duration;

/**
 * OTP entry screen shown after a code has been requested.
 */
public class OtpPage {

    public static final By HEADING = By.cssSelector("[data-testid='otp-heading']");
    public static final By SENT_TO = By.cssSelector("[data-testid='otp-sent-to']");
    public static final By OTP_INPUT = By.cssSelector("[data-testid='otp-input']");
    public static final By VERIFY = By.cssSelector("[data-testid='verify-sign-in']");
    public static final By RESEND = By.cssSelector("[data-testid='resend-code']");
    public static final By CHANGE_NUMBER = By.cssSelector("[data-testid='change-number']");

    private final WebDriver driver;
    private final Waits waits;

    public OtpPage(WebDriver driver) {
        this.driver = driver;
        this.waits = new Waits(driver);
    }

    /** The screen appears once Firebase answers the send, which is a round trip to Google. */
    public void waitUntilShown() {
        new Waits(driver, Config.intValue("otp.send.wait.seconds", 45)).visible(OTP_INPUT);
    }

    public boolean isShown() {
        return new LoginPage(driver).isDisplayed(OTP_INPUT);
    }

    public String heading() {
        return waits.visible(HEADING).getText().trim();
    }

    public String sentTo() {
        return waits.visible(SENT_TO).getText().trim();
    }

    public WebElement otpInput() {
        return waits.visible(OTP_INPUT);
    }

    public String otpValue() {
        return otpInput().getAttribute("value");
    }

    public void enterOtp(String value) {
        Inputs.replace(otpInput(), value);
    }

    public void pasteOtp(String value) {
        Inputs.paste(driver, otpInput(), value);
    }

    /** Types into whatever element currently has focus, without clicking first. */
    public void typeWithoutClicking(String value) {
        driver.switchTo().activeElement().sendKeys(value);
    }

    public boolean isOtpFocused() {
        return driver.switchTo().activeElement().equals(otpInput());
    }

    public void pressEnterInOtp() {
        otpInput().sendKeys(Keys.ENTER);
    }

    public boolean isVerifyEnabled() {
        return waits.visible(VERIFY).isEnabled();
    }

    public void clickVerify() {
        waits.clickable(VERIFY).click();
    }

    public void doubleClickVerify() {
        new Actions(driver).doubleClick(waits.clickable(VERIFY)).perform();
    }

    public String resendLabel() {
        return waits.visible(RESEND).getText().trim();
    }

    public boolean isResendEnabled() {
        return waits.visible(RESEND).isEnabled();
    }

    public void clickResend() {
        waits.clickable(RESEND).click();
    }

    /** The resend countdown is 30 s, so allow a little headroom above that. */
    public void waitForResendEnabled() {
        new Waits(driver, 45).until(d -> d.findElement(RESEND).isEnabled());
    }

    public void clickChangeNumber() {
        waits.clickable(CHANGE_NUMBER).click();
    }
}
