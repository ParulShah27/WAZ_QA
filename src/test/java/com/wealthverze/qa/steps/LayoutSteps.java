package com.wealthverze.qa.steps;

import com.wealthverze.qa.core.DriverFactory;
import com.wealthverze.qa.core.ScenarioContext;
import com.wealthverze.qa.core.Waits;
import com.wealthverze.qa.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.interactions.Actions;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class LayoutSteps {

    private WebDriver driver() {
        return DriverFactory.get();
    }

    private LoginPage login() {
        return new LoginPage(driver());
    }

    /**
     * Sizes the viewport, not the window: the window includes borders and the scrollbar, so a
     * 1024px window gives a viewport just under the 1024px breakpoint the layout is built on.
     */
    @Given("the browser window is {int} pixels wide")
    public void setWidth(int width) {
        driver().manage().window().setSize(new Dimension(width, 900));
        long viewport = (Long) ((JavascriptExecutor) driver()).executeScript(
                "return document.documentElement.clientWidth;");
        if (viewport != width) {
            driver().manage().window().setSize(new Dimension(width + (int) (width - viewport), 900));
        }
    }

    @Then("the brand panel is {string}")
    public void brandPanel(String state) {
        boolean shown = "shown".equals(state);
        // Layout reflows after a resize, so allow a short settle window.
        new Waits(driver(), 5).until(d -> login().isDisplayed(LoginPage.BRAND_PANEL) == shown);
        assertThat(login().isDisplayed(LoginPage.BRAND_PANEL)).isEqualTo(shown);
    }

    @Then("there is no horizontal scrolling")
    public void noHorizontalScroll() {
        Boolean fits = (Boolean) ((JavascriptExecutor) driver()).executeScript(
                "return document.documentElement.scrollWidth <= document.documentElement.clientWidth;");
        assertThat(fits).isTrue();
    }

    /**
     * Presses Tab from the top of the page and records the data-testid of each focused element.
     */
    @When("I press Tab through the page")
    public void tabThroughPage() {
        List<String> order = ScenarioContext.get().focusOrder();
        order.clear();
        Actions actions = new Actions(driver());
        for (int i = 0; i < 8; i++) {
            actions.sendKeys(Keys.TAB).perform();
            Object id = ((JavascriptExecutor) driver()).executeScript(
                    "return document.activeElement ? (document.activeElement.getAttribute('data-testid') || '') : '';");
            order.add(String.valueOf(id));
        }
    }

    @Then("keyboard focus visits {string} in order")
    public void focusVisitsInOrder(String expected) {
        List<String> wanted = Arrays.stream(expected.split(",")).map(String::trim).toList();
        List<String> actual = ScenarioContext.get().focusOrder();
        int next = 0;
        for (String id : actual) {
            if (next < wanted.size() && id.equals(wanted.get(next))) {
                next++;
            }
        }
        assertThat(next).as("focus order %s, got %s", wanted, actual).isEqualTo(wanted.size());
    }

    @Given("the theme is set to {string}")
    public void setTheme(String target) {
        if (!target.equals(login().theme())) {
            login().toggleTheme();
        }
        assertThat(login().theme()).isEqualTo(target);
    }

    @When("I toggle the theme")
    public void toggleTheme() {
        login().toggleTheme();
    }

    @Then("the theme is {string}")
    public void themeIs(String expected) {
        new Waits(driver(), 5).until(d -> expected.equals(login().theme()));
        assertThat(login().theme()).isEqualTo(expected);
    }

}
