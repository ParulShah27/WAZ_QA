@ui @login
Feature: Login page layout, content and accessibility
  As a client opening WealthVerze
  I want the sign-in page to show every required element
  So that I can sign in without confusion

  Background:
    Given I open the login page

  @smoke @LOGIN-TC-001
  Scenario: Login page loads successfully without errors
    Then the login page is displayed without errors

  @smoke @LOGIN-TC-002
  Scenario: WealthVerze logo is displayed
    Then the WealthVerze logo is visible

  @LOGIN-TC-003
  Scenario: Sign in heading is displayed
    Then the sign-in heading reads "Sign in"

  @LOGIN-TC-004
  Scenario: OTP instruction text is displayed
    Then the OTP instruction mentions a six-digit code

  @LOGIN-TC-005
  Scenario: Continue with Google button is displayed
    Then the Continue with Google button is visible

  @smoke @LOGIN-TC-006
  Scenario: Mobile number field is displayed and editable
    Then the mobile number field is visible and editable

  @LOGIN-TC-007
  Scenario: Configured country code is displayed
    Then the country code shows "+91"

  @smoke @LOGIN-TC-008
  Scenario: Send code button is displayed with a readable label
    Then the Send code button is visible with label "Send code"

  @LOGIN-TC-009
  Scenario: Terms of Service link is displayed
    Then the "Terms of Service" link is visible

  @LOGIN-TC-010
  Scenario: Privacy Policy link is displayed
    Then the "Privacy Policy" link is visible

  @LOGIN-TC-011
  Scenario: Theme toggle is displayed
    Then the theme toggle is visible

  @LOGIN-TC-012
  Scenario: reCAPTCHA widget is loaded
    Then the reCAPTCHA widget is loaded

  @LOGIN-TC-042
  Scenario: Mobile number field has focus on page load
    Then the mobile number field has focus

  @LOGIN-TC-043
  Scenario: Mobile number label and placeholder are correct
    Then the mobile number field label is "Mobile number" and placeholder is "98765 43210"

  @LOGIN-TC-037
  Scenario: Terms of Service link opens the terms content
    When I click the "Terms of Service" link
    Then the "Terms of Service" content opens

  @LOGIN-TC-038
  Scenario: Privacy Policy link opens the privacy content
    When I click the "Privacy Policy" link
    Then the "Privacy Policy" content opens

  @LOGIN-TC-109
  Scenario: Theme toggle switches and persists after reload
    Given the theme is set to "light"
    When I toggle the theme
    Then the theme is "dark"
    When I reload the page
    Then the theme is "dark"

  @LOGIN-TC-110
  Scenario: Browser tab title is correct
    Then the browser title is "Wealthverze — Wealth Dashboard"

  @LOGIN-TC-111
  Scenario: Security disclaimer text is displayed
    Then the disclaimer reads "Wealthverze never asks for your bank or demat password. By continuing you agree to the Terms of Service and Privacy Policy."

  @LOGIN-TC-113
  Scenario: Keyboard navigation reaches every control in logical order
    When I press Tab through the page
    Then keyboard focus visits "theme-toggle, google-signin, mobile-input, send-code, terms-link, privacy-link" in order

  @LOGIN-TC-112
  Scenario Outline: Brand panel and form adapt to screen width
    Given the browser window is <width> pixels wide
    Then the brand panel is "<brand_panel>"
    And there is no horizontal scrolling
    And the Send code button is visible with label "Send code"

    Examples:
      | width | brand_panel |
      | 375   | hidden      |
      | 768   | hidden      |
      | 1024  | shown       |
      | 1440  | shown       |
