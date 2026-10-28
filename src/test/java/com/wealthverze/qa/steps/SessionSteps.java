package com.wealthverze.qa.steps;

import com.wealthverze.qa.core.Config;
import com.wealthverze.qa.core.DriverFactory;
import com.wealthverze.qa.core.ScenarioContext;
import com.wealthverze.qa.core.Waits;
import com.wealthverze.qa.pages.AuthFlow;
import com.wealthverze.qa.pages.DashboardPage;
import com.wealthverze.qa.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.net.URI;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class SessionSteps {

    private static final Set<String> ALLOWED_BROWSER_KEYS = new HashSet<>(Arrays.asList("wv-theme", "waz.tab.session"));

    private WebDriver driver() {
        return DriverFactory.get();
    }

    private DashboardPage dashboard() {
        return new DashboardPage(driver());
    }

    @Given("I am signed in as the family head")
    public void givenSignedInFamilyHead() {
        AuthFlow.signIn(Config.familyHeadMobile());
    }

    @When("I sign in as the family head")
    public void whenSignInFamilyHead() {
        AuthFlow.signIn(Config.familyHeadMobile());
    }

    @Given("I am signed in as the family member")
    public void givenSignedInFamilyMember() {
        AuthFlow.signIn(Config.familyMemberMobile());
    }

    @When("I sign in as the family member")
    public void whenSignInFamilyMember() {
        AuthFlow.signIn(Config.familyMemberMobile());
    }

    @Given("I am signed out")
    public void signedOut() {
        driver().manage().deleteAllCookies();
        driver().get(Config.appUrl());
        new LoginPage(driver()).open();
    }

    @When("I open {string}")
    public void openPath(String path) {
        driver().get(Config.appUrl() + path);
    }

    @When("I open the application URL in a new tab")
    public void openNewTab() {
        ScenarioContext ctx = ScenarioContext.get();
        ctx.originalWindow(driver().getWindowHandle());
        ctx.windowsBefore(driver().getWindowHandles());
        ((JavascriptExecutor) driver()).executeScript("window.open(arguments[0], '_blank');", Config.appUrl());
    }

    @Then("the new tab shows the dashboard")
    public void newTabShowsDashboard() {
        ScenarioContext ctx = ScenarioContext.get();
        new Waits(driver()).until(d -> d.getWindowHandles().size() > ctx.windowsBefore().size());
        for (String handle : driver().getWindowHandles()) {
            if (!ctx.windowsBefore().contains(handle)) {
                driver().switchTo().window(handle);
            }
        }
        dashboard().waitUntilSignedIn();
        assertThat(dashboard().isSignedIn()).isTrue();
    }

    @When("I switch back to the first tab")
    public void switchBack() {
        driver().switchTo().window(ScenarioContext.get().originalWindow());
    }

    @When("I sign out from the profile menu")
    public void signOutFromMenu() {
        dashboard().signOut();
    }

    @When("I press browser Back")
    public void browserBack() {
        driver().navigate().back();
    }

    @When("I reload the page")
    public void reloadPage() {
        driver().navigate().refresh();
    }

    @Then("I remain signed in on the same page")
    public void remainSignedIn() {
        dashboard().waitUntilSignedIn();
        assertThat(dashboard().isSignedIn()).isTrue();
        assertThat(new LoginPage(driver()).isOnPhoneScreen()).isFalse();
    }

    @Then("the dashboard is still shown")
    public void dashboardStillShown() {
        assertThat(dashboard().isSignedIn()).isTrue();
    }

    @Then("the Family switch is visible")
    public void familySwitchVisible() {
        assertThat(dashboard().hasFamilySwitch()).isTrue();
    }

    @Then("the Family switch is not visible")
    public void familySwitchNotVisible() {
        assertThat(dashboard().hasFamilySwitch()).isFalse();
    }

    @Then("the page shows the not-found screen")
    public void notFoundShown() {
        new Waits(driver()).until(d -> dashboard().isNotFoundShown());
        assertThat(dashboard().isNotFoundShown()).isTrue();
    }

    @Then("I land on {string}")
    public void landOn(String expected) {
        new Waits(driver()).until(d -> pathAndQuery(d).equals(expected));
        assertThat(pathAndQuery(driver())).isEqualTo(expected);
    }

    @When("I remember the greeting")
    public void rememberGreeting() {
        ScenarioContext.get().greeting(dashboard().greeting());
    }

    @Then("the greeting does not show the previous user's name")
    public void greetingChanged() {
        assertThat(dashboard().greeting()).isNotEqualTo(ScenarioContext.get().greeting());
    }

    @Then("the session cookie {string} is HttpOnly, SameSite Lax, and not persistent")
    public void sessionCookieAttributes(String name) {
        Cookie cookie = driver().manage().getCookieNamed(name);
        assertThat(cookie).as("cookie %s exists", name).isNotNull();
        assertThat(cookie.isHttpOnly()).as("HttpOnly").isTrue();
        assertThat(cookie.getSameSite()).as("SameSite").isEqualTo("Lax");
        assertThat(cookie.getExpiry()).as("session cookie has no Expires/Max-Age").isNull();
        if (Config.secureOrigin()) {
            assertThat(cookie.isSecure()).as("Secure on HTTPS").isTrue();
        }
    }

    @Then("JavaScript cannot read the cookie {string}")
    public void jsCannotReadCookie(String name) {
        String cookies = String.valueOf(((JavascriptExecutor) driver()).executeScript("return document.cookie;"));
        assertThat(cookies).doesNotContain(name);
    }

    /**
     * Only the theme preference and the tab-session marker may exist. No token, OTP, or phone number.
     */
    @Then("browser storage holds no token, OTP or phone number")
    public void storageHoldsNoSecrets() {
        Object raw = ((JavascriptExecutor) driver()).executeScript(
                "const keys = []; const values = [];"
                        + "for (const s of [window.localStorage, window.sessionStorage]) {"
                        + "  for (let i = 0; i < s.length; i++) {"
                        + "    const k = s.key(i); keys.push(k); values.push(s.getItem(k));"
                        + "  }"
                        + "}"
                        + "return keys.join(',') + '|' + values.join(',');");
        String[] parts = String.valueOf(raw).split("\\|", -1);
        for (String key : parts[0].split(",")) {
            if (!key.isBlank()) {
                assertThat(ALLOWED_BROWSER_KEYS).as("unexpected browser storage key").contains(key);
            }
        }
        assertThat(parts.length > 1 ? parts[1] : "").doesNotContain(Config.familyHeadMobile());
    }

    /**
     * Deliberate wait: the idle timer has no observable front-end event to wait on.
     * Runs only from the @slow tag, never on PR builds.
     */
    @When("I leave the tab idle for 5 minutes and 10 seconds")
    public void idleFiveMinutes() throws InterruptedException {
        Thread.sleep(310_000L);
    }

    @Then("I see the session-ended message {string}")
    public void sessionEndedMessage(String expected) {
        assertThat(new LoginPage(driver()).text(By.cssSelector("[data-testid='session-ended-message']")))
                .contains(expected);
    }

    private String pathAndQuery(WebDriver d) {
        URI uri = URI.create(d.getCurrentUrl());
        String query = uri.getRawQuery();
        return uri.getRawPath() + (query == null ? "" : "?" + query);
    }
}
