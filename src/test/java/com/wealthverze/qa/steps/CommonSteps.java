package com.wealthverze.qa.steps;

import com.wealthverze.qa.core.DriverFactory;
import com.wealthverze.qa.core.ScenarioContext;
import com.wealthverze.qa.core.Waits;
import com.wealthverze.qa.pages.DashboardPage;
import com.wealthverze.qa.pages.LoginPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

public class CommonSteps {

    private WebDriver driver() {
        return DriverFactory.get();
    }

    private LoginPage login() {
        return new LoginPage(driver());
    }

    @Given("I open the login page")
    public void openLoginPage() {
        login().open();
    }

    @Then("the login page is displayed without errors")
    public void loginPageDisplayedWithoutErrors() {
        assertThat(login().isOnPhoneScreen()).isTrue();
        assertThat(login().isErrorShown()).isFalse();
    }

    @Then("the WealthVerze logo is visible")
    public void logoVisible() {
        assertThat(login().isDisplayed(LoginPage.LOGO)).isTrue();
    }

    @Then("the sign-in heading reads {string}")
    public void headingReads(String expected) {
        assertThat(login().text(LoginPage.HEADING)).isEqualTo(expected);
    }

    @Then("the OTP instruction mentions a six-digit code")
    public void otpInstructionMentionsSixDigitCode() {
        assertThat(login().text(LoginPage.SUBTITLE)).matches("(?is).*(six|6)[- ]?digit code.*");
    }

    @Then("the Continue with Google button is visible")
    public void googleVisible() {
        assertThat(login().isGoogleShown()).isTrue();
    }

    @Then("the mobile number field is visible and editable")
    public void mobileVisibleAndEditable() {
        assertThat(login().isDisplayed(LoginPage.MOBILE_INPUT)).isTrue();
        assertThat(login().isMobileEnabled()).isTrue();
    }

    @Then("the country code shows {string}")
    public void countryCodeShows(String expected) {
        assertThat(login().text(LoginPage.COUNTRY_CODE)).contains(expected);
    }

    @Then("the Send code button is visible with label {string}")
    public void sendCodeLabel(String expected) {
        assertThat(login().isDisplayed(LoginPage.SEND_CODE)).isTrue();
        assertThat(login().text(LoginPage.SEND_CODE)).isEqualTo(expected);
    }

    @Then("the {string} link is visible")
    public void linkVisible(String name) {
        assertThat(login().isDisplayed(linkFor(name))).isTrue();
    }

    @Then("the theme toggle is visible")
    public void themeToggleVisible() {
        assertThat(login().isDisplayed(LoginPage.THEME_TOGGLE)).isTrue();
    }

    @Then("the reCAPTCHA widget is loaded")
    public void recaptchaLoaded() {
        assertThat(login().isDisplayed(LoginPage.RECAPTCHA)).isTrue();
    }

    @Then("the disclaimer reads {string}")
    public void disclaimerReads(String expected) {
        assertThat(login().text(LoginPage.DISCLAIMER)).isEqualTo(expected);
    }

    @Then("the browser title is {string}")
    public void browserTitle(String expected) {
        assertThat(driver().getTitle()).isEqualTo(expected);
    }

    @When("I click the {string} link")
    public void clickLegalLink(String name) {
        ScenarioContext.get().windowsBefore(driver().getWindowHandles());
        login().clickLink(linkFor(name));
    }

    /**
     * Legal pages may open in a new tab or in place. Accept either, then check the content.
     */
    @Then("the {string} content opens")
    public void legalContentOpens(String name) {
        Set<String> before = ScenarioContext.get().windowsBefore();
        new Waits(driver()).until(d -> d.getWindowHandles().size() > before.size()
                || d.findElement(By.tagName("body")).getText().contains(name));
        for (String handle : driver().getWindowHandles()) {
            if (!before.contains(handle)) {
                driver().switchTo().window(handle);
            }
        }
        assertThat(driver().findElement(By.tagName("body")).getText()).contains(name);
    }

    @Then("I see the error {string}")
    public void seeError(String expected) {
        // The error follows an async round trip (Firebase or the BFF), so wait for it.
        new Waits(driver()).until(d -> login().isErrorShown());
        assertThat(login().isErrorShown()).as("error alert is shown").isTrue();
        assertThat(login().errorText()).contains(expected);
    }

    @Then("the error message is announced to screen readers")
    public void errorAnnounced() {
        assertThat(login().errorRole()).isEqualTo("alert");
    }

    @Then("no error message is shown")
    public void noErrorShown() {
        assertThat(login().isErrorShown()).isFalse();
    }

    @Then("I am on the login page")
    public void onLoginPage() {
        assertThat(login().isOnPhoneScreen()).isTrue();
    }

    @Then("I remain on the login page")
    public void remainOnLoginPage() {
        assertThat(login().isOnPhoneScreen()).isTrue();
    }

    @Then("I am redirected to the login page")
    public void redirectedToLogin() {
        new Waits(driver()).until(d -> login().isOnPhoneScreen());
    }

    @Then("I am on the dashboard")
    public void onDashboard() {
        new DashboardPage(driver()).waitUntilLoaded();
    }

    @Then("the dashboard is not shown")
    public void dashboardNotShown() {
        assertThat(new DashboardPage(driver()).isLoaded()).isFalse();
    }

    @Then("I am not signed in")
    public void notSignedIn() {
        assertThat(new DashboardPage(driver()).isSignedIn()).isFalse();
    }

    private By linkFor(String name) {
        return name.toLowerCase().contains("terms") ? LoginPage.TERMS_LINK : LoginPage.PRIVACY_LINK;
    }
}
