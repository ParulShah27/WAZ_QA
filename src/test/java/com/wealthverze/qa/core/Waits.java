package com.wealthverze.qa.core;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Function;

/**
 * Explicit waits only. No Thread.sleep and no implicit waits.
 */
public final class Waits {

    private final WebDriver driver;
    private final int seconds;

    public Waits(WebDriver driver) {
        this(driver, Config.intValue("explicit.wait.seconds", 15));
    }

    public Waits(WebDriver driver, int seconds) {
        this.driver = driver;
        this.seconds = seconds;
    }

    public WebElement visible(By locator) {
        return driverWait().until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public WebElement clickable(By locator) {
        return driverWait().until(ExpectedConditions.elementToBeClickable(locator));
    }

    public <V> V until(Function<WebDriver, V> condition) {
        return driverWait().until(condition);
    }

    private WebDriverWait driverWait() {
        return new WebDriverWait(driver, Duration.ofSeconds(seconds));
    }
}
