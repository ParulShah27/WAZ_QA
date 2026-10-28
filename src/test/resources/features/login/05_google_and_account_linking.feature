@google @login
Feature: Sign-in with Google and linking accounts
  As a client who uses Google or my mobile number
  I want to link both methods safely
  So that either one signs me in to the same account

  @LOGIN-TC-039
  Scenario: Continue with Google starts the Google sign-in window
    Given I open the login page
    When I click Continue with Google
    Then a Google sign-in window opens

  @LOGIN-TC-089
  Scenario: Double-clicking Continue with Google opens one window only
    Given I open the login page
    When I double-click Continue with Google
    Then only one Google sign-in window is opened

  @needs-test-data @LOGIN-TC-090
  Scenario: Link Google step appears after OTP sign-in when Google is not linked
    Given I have requested an OTP for mobile "9000000002"
    When I enter the OTP "123456"
    And I click Verify and sign in
    Then the account link step reads "One more thing" with text "Also sign in with Google?" and buttons "Link Google account" and "Not now"

  @needs-test-data @LOGIN-TC-092
  Scenario: Not now skips linking and reaches the dashboard
    Given I have requested an OTP for mobile "9000000002"
    When I enter the OTP "123456"
    And I click Verify and sign in
    And I click Not now
    Then I am on the dashboard without the account link step

  @needs-test-data @LOGIN-TC-095
  Scenario: Link step is skipped when both methods are already linked
    Given I have requested an OTP for mobile "9000000002"
    When I enter the OTP "123456"
    And I click Verify and sign in
    Then I am on the dashboard without the account link step
