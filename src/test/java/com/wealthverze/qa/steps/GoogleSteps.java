package com.wealthverze.qa.steps;

import com.wealthverze.qa.core.DriverFactory;
import com.wealthverze.qa.core.ScenarioContext;
import com.wealthverze.qa.core.Waits;
import com.wealthverze.qa.pages.AccountLinkPage;
import com.wealthverze.qa.pages.DashboardPage;
import com.wealthverze.qa.pages.LoginPage;
import org.openqa.selenium.interactions.Actions;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class GoogleSteps {

    private LoginPage login() {
        return new LoginPage(DriverFactory.get());
    }

    @When("I click Continue with Google")
    public void clickGoogle() {
        ScenarioContext.get().windowsBefore(DriverFactory.get().getWindowHandles());
        login().clickGoogle();
    }

    @When("I double-click Continue with Google")
    public void doubleClickGoogle() {
        ScenarioContext.get().windowsBefore(DriverFactory.get().getWindowHandles());
        new Actions(DriverFactory.get()).doubleClick(login().googleButton()).perform();
    }

    @Then("a Google sign-in window opens")
    public void googleWindowOpens() {
        int before = ScenarioContext.get().windowsBefore().size();
        new Waits(DriverFactory.get()).until(d -> d.getWindowHandles().size() > before);
    }

    /**
     * A double click must not open a second popup. Give the second click a short window to misbehave.
     */
    @Then("only one Google sign-in window is opened")
    public void onlyOneGoogleWindow() {
        int before = ScenarioContext.get().windowsBefore().size();
        new Waits(DriverFactory.get(), 5).until(d -> d.getWindowHandles().size() > before);
        assertThat(DriverFactory.get().getWindowHandles().size()).isEqualTo(before + 1);
        assertThat(login().isErrorShown()).isFalse();
    }

    @Then("the account link step reads {string} with text {string} and buttons {string} and {string}")
    public void linkStepReads(String heading, String text, String linkButton, String notNow) {
        AccountLinkPage link = new AccountLinkPage(DriverFactory.get());
        assertThat(link.isShown()).isTrue();
        assertThat(link.heading()).isEqualTo(heading);
        assertThat(link.text()).isEqualTo(text);
        assertThat(link.linkGoogleLabel()).isEqualTo(linkButton);
        assertThat(link.notNowLabel()).isEqualTo(notNow);
    }

    @When("I click Not now")
    public void clickNotNow() {
        new AccountLinkPage(DriverFactory.get()).clickNotNow();
    }

    @Then("I am on the dashboard without the account link step")
    public void dashboardWithoutLinkStep() {
        DashboardPage dashboard = new DashboardPage(DriverFactory.get());
        dashboard.waitUntilLoaded();
        assertThat(new AccountLinkPage(DriverFactory.get()).isShown()).isFalse();
    }
}
