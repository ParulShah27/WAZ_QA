package com.wealthverze.qa.steps;

import com.wealthverze.qa.core.DriverFactory;
import com.wealthverze.qa.core.Messages;
import com.wealthverze.qa.pages.AuthFlow;
import com.wealthverze.qa.pages.LoginPage;
import com.wealthverze.qa.pages.OtpPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static org.assertj.core.api.Assertions.assertThat;

public class OtpSteps {

    private OtpPage otp() {
        return new OtpPage(DriverFactory.get());
    }

    private LoginPage login() {
        return new LoginPage(DriverFactory.get());
    }

    @Given("I have requested an OTP for mobile {string}")
    public void requestOtp(String mobile) {
        AuthFlow.requestOtp(mobile);
    }

    @Then("I see the OTP screen")
    public void otpScreenShown() {
        otp().waitUntilShown();
        assertThat(otp().isShown()).isTrue();
    }

    @Then("the OTP screen says {string}")
    public void sentToText(String expected) {
        assertThat(otp().sentTo()).isEqualTo(expected);
    }

    @Then("the OTP heading reads {string}")
    public void otpHeading(String expected) {
        assertThat(otp().heading()).isEqualTo(expected);
    }

    @When("I enter the OTP {string}")
    public void enterOtp(String value) {
        otp().enterOtp(value);
    }

    @When("I paste the OTP {string}")
    public void pasteOtp(String value) {
        otp().pasteOtp(value);
    }

    @When("I type digits {string} without clicking")
    public void typeWithoutClicking(String value) {
        otp().typeWithoutClicking(value);
    }

    @Then("the OTP field contains {string}")
    public void otpFieldContains(String expected) {
        assertThat(otp().otpValue()).isEqualTo(expected);
    }

    @Then("the OTP field value is {string}")
    public void otpFieldValue(String expected) {
        assertThat(otp().otpValue()).isEqualTo(expected);
    }

    @When("I press Enter in the OTP field")
    public void pressEnterInOtp() {
        otp().pressEnterInOtp();
    }

    @When("I click Verify and sign in")
    public void clickVerify() {
        otp().clickVerify();
    }

    @When("I double-click Verify and sign in")
    public void doubleClickVerify() {
        otp().doubleClickVerify();
    }

    @Then("the Verify and sign in button is disabled")
    public void verifyDisabled() {
        assertThat(otp().isVerifyEnabled()).isFalse();
    }

    @Then("the Verify and sign in button is enabled")
    public void verifyEnabled() {
        assertThat(otp().isVerifyEnabled()).isTrue();
    }

    @Then("the Continue with Google button is not shown on the OTP screen")
    public void googleNotShownOnOtp() {
        assertThat(otp().isShown()).isTrue();
        assertThat(login().isGoogleShown()).isFalse();
    }

    @Then("the resend control reads {string}")
    public void resendReads(String expected) {
        assertThat(otp().resendLabel()).isEqualTo(expected);
    }

    @Then("the resend control is enabled")
    public void resendEnabled() {
        assertThat(otp().isResendEnabled()).isTrue();
    }

    @When("I wait for the resend countdown to finish")
    public void waitForResend() {
        otp().waitForResendEnabled();
    }

    @When("I click Resend code")
    public void clickResend() {
        otp().clickResend();
    }

    @Then("the resend countdown restarts")
    public void resendRestarts() {
        assertThat(otp().resendLabel()).startsWith("Resend in");
        assertThat(otp().isResendEnabled()).isFalse();
    }

    @When("I click Change number")
    public void clickChangeNumber() {
        otp().clickChangeNumber();
    }

    @Then("I am back on the mobile number screen with the number {string}")
    public void backOnPhoneScreen(String number) {
        assertThat(login().isOnPhoneScreen()).isTrue();
        assertThat(login().mobileValue()).isEqualTo(number);
    }

    @Then("the typed code is cleared")
    public void typedCodeCleared() {
        assertThat(otp().isShown()).isFalse();
    }

    @Then("the OTP field is cleared after a wrong code")
    public void otpClearedAfterWrong() {
        assertThat(otp().otpValue()).isEmpty();
    }

    @When("I replace the mobile number with {string}")
    public void replaceMobile(String number) {
        login().enterMobile(number);
    }

    /**
     * Submits distinct wrong codes until the backend reports the attempt limit.
     * Stops as soon as the limit message appears so later steps see the real state.
     */
    @When("I submit wrong OTPs until the attempts are blocked")
    public void submitWrongUntilBlocked() {
        String[] wrongCodes = {"000000", "111111", "222222", "333333", "444444", "555555", "666666", "777777"};
        for (String code : wrongCodes) {
            otp().enterOtp(code);
            if (!otp().isVerifyEnabled()) {
                break;
            }
            otp().clickVerify();
            if (login().isErrorShown() && login().errorText().contains(Messages.TOO_MANY_ATTEMPTS)) {
                break;
            }
        }
    }

}
