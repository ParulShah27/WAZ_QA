package com.wealthverze.qa.core;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.logging.LogType;

import java.time.Duration;
import java.util.Map;

/**
 * One browser per scenario thread. Selenium Manager resolves chromedriver,
 * so no manual driver download is needed on CI or developer machines.
 */
public final class DriverFactory {

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverFactory() {
    }

    public static WebDriver get() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            if (Config.bool("headless")) {
                options.addArguments("--headless=new");
            }
            options.addArguments("--window-size=1440,900", "--disable-notifications",
                    "--no-sandbox", "--disable-dev-shm-usage");
            // Keep the page's console so a failure report can show what the app logged.
            options.setCapability("goog:loggingPrefs", Map.of(LogType.BROWSER, "ALL"));
            driver = new ChromeDriver(options);
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            DRIVER.set(driver);
        }
        return driver;
    }

    public static void quit() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            try {
                driver.quit();
            } finally {
                DRIVER.remove();
            }
        }
    }
}
