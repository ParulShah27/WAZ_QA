@otp @login
Feature: Requesting a one-time code
  As a client with a registered mobile number
  I want a code sent to me, and clear feedback when that is not possible
  So that I can start signing in

  Background:
    Given I open the login page

  @smoke @LOGIN-TC-013
  Scenario: Registered mobile number starts the OTP request
    When I enter the mobile number "9000000002"
    And I click Send code
    Then I see the OTP screen

  @smoke @LOGIN-TC-024
  Scenario: Code is delivered to the registered mobile number
    When I enter the mobile number "9000000002"
    And I click Send code
    Then I see the OTP screen
    And the OTP screen says "Sent to +919000000002."

  @LOGIN-TC-023
  Scenario: Unregistered mobile number shows the account message
    When I enter the mobile number "9307835654"
    And I click Send code
    Then I see the error "We couldn't sign you in with these details. Please contact your advisor."
    And I remain on the login page

  @LOGIN-TC-050
  Scenario: Error message clears when the mobile number is edited
    When I enter the mobile number "9307835654"
    And I click Send code
    Then I see the error "We couldn't sign you in with these details. Please contact your advisor."
    When I delete one digit from the mobile number
    Then no error message is shown

  @LOGIN-TC-025
  Scenario: Send code shows a loading state while the request is in progress
    When I enter the mobile number "9000000002"
    And I click Send code
    Then the Send code button shows a loading state

  @LOGIN-TC-051
  Scenario: Other controls are disabled while the code is being sent
    When I enter the mobile number "9000000002"
    And I click Send code and then try the other controls while it is sending
    Then only the Send code button shows a spinner and the other controls are disabled

  @LOGIN-TC-040
  Scenario: Pressing Enter submits a valid mobile number
    When I enter the mobile number "9000000002"
    And I press Enter in the mobile number field
    Then I see the OTP screen

  @pending-feature @LOGIN-TC-026
  Scenario: Rapid repeated clicks on Send code send only one request
    When I enter the mobile number "9000000002"
    And I click Send code 5 times quickly
    Then I see the OTP screen
