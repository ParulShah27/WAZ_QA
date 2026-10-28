@session @login
Feature: Session handling, access control and browser security
  As a client
  I want my session to be protected and my data kept private
  So that nobody else can see my portfolio

  @LOGIN-TC-035
  Scenario: Signed-in user opening the login page is sent to the dashboard
    Given I am signed in as the family head
    When I open "/login"
    Then I am on the dashboard

  @smoke @LOGIN-TC-036
  Scenario: Signed-out user is sent to login from a protected page
    Given I am signed out
    When I open "/dashboard"
    Then I am redirected to the login page

  @LOGIN-TC-072
  Scenario: Family head sees the Family switch on the dashboard
    Given I am signed in as the family head
    Then I am on the dashboard
    And the Family switch is visible

  @LOGIN-TC-073
  Scenario: Family member sees only their own dashboard
    Given I am signed in as the family member
    Then I am on the dashboard
    And the Family switch is not visible

  @LOGIN-TC-080
  Scenario Outline: Client cannot open staff pages by URL
    Given I am signed in as the family head
    When I open "<path>"
    Then the page shows the not-found screen

    Examples:
      | path   |
      | /book  |
      | /team  |
      | /admin |

  @LOGIN-TC-081
  Scenario: User returns to the requested page after signing in
    Given I am signed out
    When I open "/holdings?x=1"
    Then I am redirected to the login page
    When I sign in as the family head
    Then I land on "/holdings?x=1"

  @LOGIN-TC-082
  Scenario: Unknown page before login lands on the dashboard after sign-in
    Given I am signed out
    When I open "/some-unknown-page"
    And I sign in as the family head
    Then I am on the dashboard

  @LOGIN-TC-096
  Scenario: Sign out ends the session and Back does not reveal the dashboard
    Given I am signed in as the family head
    When I sign out from the profile menu
    Then I am on the login page
    When I press browser Back
    Then the dashboard is not shown

  @LOGIN-TC-097
  Scenario: Reloading the page keeps the user signed in
    Given I am signed in as the family head
    When I reload the page
    Then I remain signed in on the same page

  @LOGIN-TC-101
  Scenario: A second tab opens signed in and the first tab stays signed in
    Given I am signed in as the family head
    When I open the application URL in a new tab
    Then the new tab shows the dashboard
    When I switch back to the first tab
    Then the dashboard is still shown

  @LOGIN-TC-104
  Scenario: Next user does not see the previous user's data
    Given I am signed in as the family head
    When I remember the greeting
    And I sign out from the profile menu
    And I sign in as the family member
    Then the greeting does not show the previous user's name

  @LOGIN-TC-105
  Scenario: Session cookie has the required security attributes
    Given I am signed in as the family head
    Then the session cookie "waz_session" is HttpOnly, SameSite Lax, and not persistent

  @LOGIN-TC-106
  Scenario: JavaScript cannot read the session cookie
    Given I am signed in as the family head
    Then JavaScript cannot read the cookie "waz_session"

  @LOGIN-TC-107
  Scenario: Browser storage holds no token, OTP or phone number
    Given I am signed in as the family head
    Then browser storage holds no token, OTP or phone number

  @slow @LOGIN-TC-098
  Scenario: Session ends after five minutes of inactivity
    Given I am signed in as the family head
    When I leave the tab idle for 5 minutes and 10 seconds
    Then I see the session-ended message "Your session has ended"
