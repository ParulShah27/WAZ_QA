package com.wealthverze.qa.pages;

import com.wealthverze.qa.core.Config;
import com.wealthverze.qa.core.DriverFactory;
import com.wealthverze.qa.core.ScenarioContext;
import com.wealthverze.qa.core.Waits;

/**
 * Reusable sign-in journeys. Steps call these instead of repeating UI clicks.
 */
public final class AuthFlow {

    private AuthFlow() {
    }

    public static void requestOtp(String mobile) {
        LoginPage login = new LoginPage(DriverFactory.get());
        login.open();
        login.enterMobile(mobile);
        login.clickSendCode();
        new OtpPage(DriverFactory.get()).waitUntilShown();
        ScenarioContext.get().mobile(mobile);
    }

    /**
     * Full sign-in with the test number and fixed test OTP, ending on the authenticated shell.
     * An account with only one method linked is offered the link step first; this declines it,
     * since the link step has its own scenarios and here it only stands between us and the shell.
     */
    public static void signIn(String mobile) {
        requestOtp(mobile);
        OtpPage otp = new OtpPage(DriverFactory.get());
        otp.enterOtp(Config.testOtp());
        otp.clickVerify();
        LoginPage login = new LoginPage(DriverFactory.get());
        new Waits(DriverFactory.get()).until(d ->
                login.isDisplayed(DashboardPage.APP_SHELL) || login.isDisplayed(AccountLinkPage.NOT_NOW));
        if (login.isDisplayed(AccountLinkPage.NOT_NOW)) {
            new AccountLinkPage(DriverFactory.get()).clickNotNow();
        }
        new DashboardPage(DriverFactory.get()).waitUntilSignedIn();
    }
}
