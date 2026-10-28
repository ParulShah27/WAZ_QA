package com.wealthverze.qa.hooks;

import com.wealthverze.qa.core.Config;
import com.wealthverze.qa.core.DriverFactory;
import com.wealthverze.qa.core.ScenarioContext;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.logging.LogType;

import java.util.stream.Collectors;

/**
 * Every scenario starts signed out with empty browser state, so scenarios are
 * independent of execution order.
 */
public class Hooks {

    @Before
    public void startScenario() {
        ScenarioContext.reset();
        WebDriver driver = DriverFactory.get();
        driver.get(Config.appUrl());
        driver.manage().deleteAllCookies();
        ((JavascriptExecutor) driver).executeScript("window.localStorage.clear(); window.sessionStorage.clear();");
        driver.get(Config.appUrl());
    }

    @After
    public void finishScenario(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                byte[] png = ((TakesScreenshot) DriverFactory.get()).getScreenshotAs(OutputType.BYTES);
                scenario.attach(png, "image/png", "failure-screenshot");
                String console = DriverFactory.get().manage().logs().get(LogType.BROWSER).getAll().stream()
                        .map(e -> e.getLevel() + " " + e.getMessage())
                        .collect(Collectors.joining("\n"));
                scenario.attach(console, "text/plain", "browser-console");
            }
        } finally {
            DriverFactory.quit();
        }
    }
}
