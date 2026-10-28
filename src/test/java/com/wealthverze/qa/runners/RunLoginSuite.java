package com.wealthverze.qa.runners;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;
import static io.cucumber.junit.platform.engine.Constants.PLUGIN_PROPERTY_NAME;

/**
 * Entry point for the login BDD suite. Select scenarios with -Dcucumber.filter.tags.
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/login")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "com.wealthverze.qa.steps,com.wealthverze.qa.hooks")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME,
        value = "pretty,html:target/cucumber/report.html,json:target/cucumber/report.json")
public class RunLoginSuite {
}
