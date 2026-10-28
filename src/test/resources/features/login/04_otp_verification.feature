@otp @login
Feature: Verifying the one-time code
  As a client who has received a code
  I want the correct code to sign me in and wrong codes to be handled clearly
  So that my account stays secure and usable

  Background:
    Given I have requested an OTP for mobile "9000000002"

  @smoke @LOGIN-TC-028
  Scenario: Valid OTP signs the user in
    When I enter the OTP "123456"
    And I click Verify and sign in
    Then I am on the dashboard

  @smoke @LOGIN-TC-029
  Scenario: Wrong OTP is rejected and the user is not signed in
    When I enter the OTP "000000"
    And I click Verify and sign in
    Then I see the error "That code is not right. Check it and try again."
    And I see the OTP screen
    And I am not signed in

  @LOGIN-TC-031
  Scenario Outline: OTP field accepts only up to six digits
    When I enter the OTP "<input>"
    Then the OTP field value is "<expected>"

    Examples:
      | input   | expected |
      | 12ab!@  | 12       |
      | 1234567 | 123456   |
      | abcdef  |          |

  @LOGIN-TC-032
  Scenario: Verify and sign in stays disabled until six digits are entered
    When I enter the OTP "1234"
    Then the Verify and sign in button is disabled
    When I enter the OTP "123456"
    Then the Verify and sign in button is enabled

  @LOGIN-TC-041
  Scenario: Pressing Enter submits a valid OTP
    When I enter the OTP "123456"
    And I press Enter in the OTP field
    Then I am on the dashboard

  @LOGIN-TC-054
  Scenario: OTP screen heading and destination number are correct
    Then the OTP heading reads "Enter the code"
    And the OTP screen says "Sent to +919000000002."

  @LOGIN-TC-055
  Scenario: Continue with Google is hidden on the OTP screen
    Then the Continue with Google button is not shown on the OTP screen

  @LOGIN-TC-056
  Scenario: OTP field receives focus automatically
    When I type digits "123456" without clicking
    Then the OTP field contains "123456"

  @LOGIN-TC-057
  Scenario Outline: Pasted OTP is cleaned to six digits
    When I paste the OTP "<pasted>"
    Then the OTP field value is "<expected>"

    Examples:
      | pasted  | expected |
      | 123456  | 123456   |
      | 123 456 | 123456   |
      | 1234567 | 123456   |

  @LOGIN-TC-114
  Scenario: Wrong OTP error is announced to screen readers
    When I enter the OTP "000000"
    And I click Verify and sign in
    Then I see the error "That code is not right. Check it and try again."
    And the error message is announced to screen readers

  @LOGIN-TC-058 @needs-clarification
  Scenario: Wrong OTP clears the field and keeps resend available
    When I enter the OTP "000000"
    And I click Verify and sign in
    Then I see the error "That code is not right. Check it and try again."
    And the OTP field is cleared after a wrong code
    And the resend control is enabled

  @destructive @LOGIN-TC-059
  Scenario: Repeated wrong OTPs are blocked
    When I submit wrong OTPs until the attempts are blocked
    Then I see the error "Too many attempts from this device. Wait a few minutes before trying again."

  @LOGIN-TC-065
  Scenario: Double-clicking Verify signs in once without error
    When I enter the OTP "123456"
    And I double-click Verify and sign in
    Then I am on the dashboard
    And no error message is shown

  @regression @LOGIN-TC-061
  Scenario: Resend countdown runs for 30 seconds then enables Resend code
    Then the resend control reads "Resend in 30s"
    When I wait for the resend countdown to finish
    Then the resend control reads "Resend code"
    And the resend control is enabled

  @slow @LOGIN-TC-033
  Scenario: Resend code requests a new code after the countdown
    When I wait for the resend countdown to finish
    And I click Resend code
    Then the resend countdown restarts

  @LOGIN-TC-034
  Scenario: Change number returns to the mobile entry screen
    When I click Change number
    Then I am back on the mobile number screen with the number "9000000002"

  @LOGIN-TC-063
  Scenario: Change number clears the code and error but keeps the number
    When I enter the OTP "000000"
    And I click Verify and sign in
    Then I see the error "That code is not right. Check it and try again."
    When I enter the OTP "12"
    And I click Change number
    Then I am back on the mobile number screen with the number "9000000002"
    And the typed code is cleared
    And no error message is shown
    And the Continue with Google button is visible

  @LOGIN-TC-064
  Scenario: Changing the number sends a code to the new number
    When I click Change number
    And I replace the mobile number with "9000000001"
    And I click Send code
    Then the OTP screen says "Sent to +919000000001."
    When I enter the OTP "123456"
    And I click Verify and sign in
    Then I am on the dashboard
