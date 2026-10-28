package com.wealthverze.qa.steps;

import com.wealthverze.qa.core.DriverFactory;
import com.wealthverze.qa.core.ScenarioContext;
import com.wealthverze.qa.pages.LoginPage;
import com.wealthverze.qa.pages.OtpPage;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class PhoneEntrySteps {

    private LoginPage login() {
        return new LoginPage(DriverFactory.get());
    }

    @When("I enter the mobile number {string}")
    public void enterMobile(String number) {
        login().enterMobile(number);
        ScenarioContext.get().mobile(number);
    }

    @When("I paste {string} into the mobile number field")
    public void pasteMobile(String text) {
        login().pasteMobile(text);
    }

    @When("I type {string} key by key")
    public void typeKeyByKey(String text) {
        login().typeKeyByKey(text);
    }

    @When("I type one more digit {string}")
    public void typeOneMoreDigit(String digit) {
        login().typeAtEnd(digit);
    }

    @When("I delete one digit from the mobile number")
    public void deleteOneDigit() {
        login().deleteLastDigit();
    }

    @When("I click Send code")
    public void clickSendCode() {
        login().clickSendCodeIfEnabled();
    }

    @When("I click Send code {int} times quickly")
    public void clickSendCodeRapidly(int times) {
        for (int i = 0; i < times; i++) {
            login().clickSendCodeIfEnabled();
        }
    }

    @When("I click Send code and then try the other controls while it is sending")
    public void clickSendThenTryOthers() {
        LoginPage login = login();
        login.clickSendCode();
        login.tryOtherControlsWhileSending();
    }

    @When("I press Enter in the mobile number field")
    public void pressEnter() {
        login().pressEnterInMobile();
    }

    @Then("the mobile number field shows {string}")
    public void mobileShows(String expected) {
        assertThat(login().mobileValue()).isEqualTo(expected);
    }

    @Then("the mobile number field contains only digits")
    public void mobileOnlyDigits() {
        assertThat(login().mobileValue()).matches("\\d*");
    }

    @Then("the mobile number field contains at most {int} digits")
    public void mobileAtMost(int max) {
        String value = login().mobileValue();
        assertThat(value).matches("\\d*");
        assertThat(value.length()).isLessThanOrEqualTo(max);
    }

    @Then("the mobile number field is empty")
    public void mobileEmpty() {
        assertThat(login().mobileValue()).isEmpty();
    }

    @Then("the mobile number field has focus")
    public void mobileHasFocus() {
        assertThat(login().isMobileFocused()).isTrue();
    }

    @Then("the mobile number field uses a numeric keypad")
    public void numericKeypad() {
        LoginPage login = login();
        String mode = login.mobileInputMode();
        boolean numeric = "numeric".equals(mode) || "tel".equals(mode) || "tel".equals(login.mobileType());
        assertThat(numeric).as("inputmode=%s type=%s", mode, login.mobileType()).isTrue();
    }

    @Then("the mobile number field label is {string} and placeholder is {string}")
    public void labelAndPlaceholder(String label, String placeholder) {
        LoginPage login = login();
        assertThat(login.mobileLabel()).isEqualTo(label);
        assertThat(login.mobilePlaceholder()).isEqualTo(placeholder);
    }

    @Then("Send code is enabled")
    public void sendCodeEnabled() {
        assertThat(login().isSendCodeEnabled()).isTrue();
    }

    @Then("Send code is disabled")
    public void sendCodeDisabled() {
        assertThat(login().isSendCodeEnabled()).isFalse();
    }

    /**
     * The spec allows either a disabled button or a visible error. Either is acceptable,
     * but no OTP screen may appear.
     */
    @Then("the code request is blocked")
    public void codeRequestBlocked() {
        LoginPage login = login();
        assertThat(new OtpPage(DriverFactory.get()).isShown()).as("OTP screen must not appear").isFalse();
        assertThat(!login.isSendCodeEnabled() || login.isErrorShown())
                .as("Send code disabled or error shown").isTrue();
    }

    @Then("the Send code button shows a loading state")
    public void sendCodeLoading() {
        LoginPage login = login();
        assertThat(login.isSendCodeLoading() || !login.isSendCodeEnabled())
                .as("spinner or disabled state while sending").isTrue();
    }

    @Then("only the Send code button shows a spinner and the other controls are disabled")
    public void onlySendCodeLoading() {
        LoginPage login = login();
        assertThat(login.isSendCodeLoading() || !login.isSendCodeEnabled()).isTrue();
        assertThat(login.isMobileEnabled()).isFalse();
        assertThat(login.isGoogleEnabled()).isFalse();
    }
}
