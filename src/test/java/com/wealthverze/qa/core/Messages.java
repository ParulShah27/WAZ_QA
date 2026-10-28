package com.wealthverze.qa.core;

/**
 * User-facing copy from the test workbook. A change here should be a deliberate,
 * reviewed change against the product content spec.
 */
public final class Messages {

    public static final String ACCOUNT_REJECTED =
            "We couldn't sign you in with these details. Please contact your advisor.";
    public static final String WRONG_OTP = "That code is not right. Check it and try again.";
    public static final String TOO_MANY_ATTEMPTS =
            "Too many attempts from this device. Wait a few minutes before trying again.";
    public static final String SESSION_ENDED_IDLE = "Your session has ended";

    private Messages() {
    }
}
