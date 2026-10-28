@validation @login
Feature: Mobile number validation on the sign-in screen
  As a client entering my mobile number
  I want invalid input rejected and valid input accepted
  So that only well-formed requests reach the OTP service

  Background:
    Given I open the login page

  @smoke @LOGIN-TC-014
  Scenario: Empty mobile number cannot request a code
    When I click Send code
    Then the code request is blocked

  @LOGIN-TC-015
  Scenario: Nine-digit number is rejected
    When I enter the mobile number "987654321"
    Then the code request is blocked

  @LOGIN-TC-016
  Scenario: Eleven-digit number cannot exceed ten digits
    When I enter the mobile number "93078356548"
    Then the mobile number field contains at most 10 digits

  @LOGIN-TC-017
  Scenario: Alphabetic characters are rejected
    When I enter the mobile number "98765ABCDE"
    Then the mobile number field contains only digits
    And the code request is blocked

  @LOGIN-TC-018
  Scenario: Special characters are rejected
    When I enter the mobile number "935657*&%"
    Then the mobile number field contains only digits
    And the code request is blocked

  @LOGIN-TC-019 @needs-clarification
  Scenario: Spaces inside the number are not accepted
    When I enter the mobile number "98765 43210"
    Then the mobile number field contains only digits

  @LOGIN-TC-020
  Scenario: Ten zeros are not accepted
    When I enter the mobile number "0000000000"
    Then the code request is blocked

  @smoke @LOGIN-TC-021
  Scenario: Exactly ten digits with a valid format enables Send code
    When I enter the mobile number "9000000002"
    Then Send code is enabled

  @smoke @LOGIN-TC-022
  Scenario: Pasting a valid number is accepted and validated
    When I paste "9000000002" into the mobile number field
    Then the mobile number field shows "9000000002"
    And Send code is enabled

  @LOGIN-TC-044
  Scenario Outline: Pasted number with country prefix is normalised
    When I paste "<pasted>" into the mobile number field
    Then the mobile number field shows "9876543210"
    And Send code is enabled

    Examples:
      | pasted         |
      | +91 98765 43210 |
      | +919876543210  |
      | 919876543210   |
      | 09876543210    |

  @LOGIN-TC-046
  Scenario: Devanagari digits are rejected
    When I paste "९८७६५४३२१०" into the mobile number field
    Then the mobile number field is empty
    And Send code is disabled

  @LOGIN-TC-047
  Scenario: Leading and trailing spaces are trimmed
    When I paste "  9000000002  " into the mobile number field
    Then the mobile number field shows "9000000002"
    And Send code is enabled

  @LOGIN-TC-052
  Scenario: Pressing Enter with an incomplete number sends nothing
    When I enter the mobile number "98765"
    And I press Enter in the mobile number field
    Then I remain on the login page
    And the code request is blocked

  @LOGIN-TC-053
  Scenario: Numeric keypad is requested for the mobile field
    Then the mobile number field uses a numeric keypad

  @known-defect @LOGIN-TC-045
  Scenario Outline: Numbers that do not start with 6 to 9 are rejected
    When I enter the mobile number "<number>"
    Then the code request is blocked

    Examples:
      | number     |
      | 1234567890 |
      | 5555555555 |

  @known-defect @LOGIN-TC-048
  Scenario: Extra typed digit is ignored once ten digits are present
    When I type "9123456789" key by key
    And I type one more digit "5"
    Then the mobile number field shows "9123456789"

  @known-defect @LOGIN-TC-049
  Scenario: Pasting an eleven-digit number does not silently send to a cut-down number
    When I paste "93078356548" into the mobile number field
    Then the code request is blocked
