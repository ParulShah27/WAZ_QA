# WAZ login BDD suite

Selenium 4 + Cucumber-JVM 7 + JUnit 5, written in Java to match the WAZ backend.
Scenarios come from the login tabs of `Test_Suit_1.xlsx` (LOGIN-TC-001 to 117).
Each scenario carries its workbook ID as a tag, so `docs/COVERAGE_MATRIX.csv` traces
every workbook case to the scenario that automates it or to the reason it is not automated.

## Layout

```
pom.xml                              Maven build (Java 17)
src/test/resources/
  features/login/*.feature           Gherkin, one file per area (6 files, 84 automated cases)
  config/test.properties             environment defaults (no secrets)
  junit-platform.properties          default tag filter, parallel off
src/test/java/com/wealthverze/qa/
  runners/RunLoginSuite.java         JUnit entry point
  hooks/Hooks.java                   clean browser state per scenario, screenshot on failure
  core/                              Config, DriverFactory, Waits, Inputs, Messages, ScenarioContext
  pages/                             page objects. The only place with locators.
  steps/                             thin Java step definitions (Common, PhoneEntry, Otp, Google, Session, Layout)
.github/workflows/login-bdd.yml      CI pipeline
docs/COVERAGE_MATRIX.csv             workbook -> scenario traceability
docs/build_coverage_matrix.py        regenerates the matrix from the workbook and feature tags
```

## Run locally

Prerequisites: JDK 17+, Maven 3.9+, Google Chrome. Selenium Manager fetches chromedriver.

```bash
# start the backend (8080) and the frontend (5173) first, then:
mvn -B test                                                   # default: automatable, fast, no special data
mvn -B test -Dcucumber.filter.tags="@smoke"                   # PR smoke
mvn -B test -Dcucumber.filter.tags="@known-defect"            # tracked defects (expected to fail today)
mvn -B test -Dapp.url=https://uat.example.com -Dsecure.origin=true
mvn -B test -Dheadless=false                                  # watch the browser
```

Config keys can be set with `-D` or environment variables (`APP_URL`, `MOBILE_FAMILY_HEAD`, `OTP_TEST`, ...).

## Tags

| Tag | Meaning | Run by default |
|---|---|---|
| `@smoke` | fast gate for PRs | only with `@smoke` filter |
| `@regression` | full automated set | yes |
| `@slow` | waits of 30 s or more (idle timeout, resend countdown) | no (nightly) |
| `@destructive` | locks the test account after repeated wrong OTPs | no (own CI job) |
| `@known-defect` | behaviour the workbook marks as a bug | no (report-only CI job) |
| `@needs-test-data` | needs staff, Google or duplicate-number accounts not yet provided | no |
| `@needs-clarification` | expected result is ambiguous in the workbook | no |
| `@pending-feature` | feature not yet built (rate limiting on Send code) | no |
| `@requires-environment-control` | needs the backend or OTP service stopped | no |

## Frontend contract (action needed)

The step definitions find elements through `data-testid` attributes. The values are
set in `pages/*.java` and are placeholders until someone confirms them against the Vite
build. The frontend team needs to add these attributes (or tell us the real ones):

- Phone screen: `brand-panel`, `brand-logo`, `signin-heading`, `signin-subtitle`, `mobile-input`, `mobile-label`, `country-code`, `send-code`, `send-code-spinner`, `google-signin`, `terms-link`, `privacy-link`, `theme-toggle`, `recaptcha-widget`, `signin-disclaimer`, `auth-error` (role="alert")
- OTP screen: `otp-heading`, `otp-sent-to`, `otp-input` (inputmode="numeric"), `verify-sign-in`, `resend-code`, `change-number`
- Authenticated shell: `app-shell`, `dashboard-root`, `family-switch`, `user-greeting`, `profile-trigger`, `sign-out`, `page-not-found`
- Link step: `link-heading`, `link-text`, `link-google-button`, `link-not-now`
- Session-ended screen: `session-ended-message`

Also set `html[data-theme]` (or the `dark` class) so the theme assertions can read the state.

## Assumptions and open items

1. **Code access.** The sandbox used to build this could not reach `D:\WealthVerze\...`. The suite is built from the workbook and the stated setup (Vite on 5173, backend on 8080, test number 9000000002 with OTP 123456). Confirm the locators on the first run; the failure screenshots make mismatches quick to fix.
2. **Firebase test numbers.** Test numbers and codes must be configured in the Firebase console for the test project. reCAPTCHA must be bypassed for test numbers, otherwise the OTP step cannot be automated.
3. **Build status.** The Java code was checked for syntax with the JDK compiler, and every Gherkin step was matched to a Java definition. It has not been run with Maven against the app, because Maven Central was unreachable from the build sandbox. The first `mvn test` on the CI runner is the real compile.
4. **Known defects.** LOGIN-TC-045 (accepts 1234567890), 048 (extra digit corrupts the number) and 049 (11-digit paste is cut and sent) are tagged `@known-defect`. They fail until fixed, and the pipeline reports them without failing the build.
5. **Ambiguous expectations.** TC-019 (spaces) and TC-058 (wrong OTP flow: its resend expectation conflicts with the 30 s countdown) are tagged `@needs-clarification`.
6. **Not automated, with reasons.** See `docs/COVERAGE_MATRIX.csv`. Most need staff, Google or duplicate-number accounts, an environment that can be stopped, or a manual browser check.
7. **Browsers.** Chrome only today. Cross-browser (TC-115) is a CI matrix to add once the Chrome run is green.

## CI setup (GitHub Actions)

Repository **variables**: `BACKEND_REPO`, `FRONTEND_REPO` (e.g. `org/WAZ`, `org/WazWebDashboard`), optionally `BACKEND_START_CMD` (default `./mvnw -q spring-boot:run`) and `TEST_RUNNER` (a self-hosted Windows runner label if you run on Windows; the workflow uses bash, so switch to pwsh equivalents on Windows).

Repository **secrets**: `APP_REPO_TOKEN` (read access to both app repos), `FIREBASE_SERVICE_ACCOUNT_JSON` (backend, test project), `VITE_FIREBASE_API_KEY`, `VITE_FIREBASE_AUTH_DOMAIN`, `VITE_FIREBASE_PROJECT_ID`, `VITE_FIREBASE_APP_ID`.

Triggers: PRs and pushes to `main`/`develop` run `@smoke`. The nightly schedule runs regression including `@slow`, then `@destructive`. Known defects run last and report only.

Gate merges on the PR smoke job. Keep the nightly job red until someone owns each failure.
