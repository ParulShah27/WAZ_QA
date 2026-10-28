package com.wealthverze.qa.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * State shared between steps of one scenario. Thread-local so parallel runs
 * (if enabled later) do not interfere.
 */
public final class ScenarioContext {

    private static final ThreadLocal<ScenarioContext> CURRENT = ThreadLocal.withInitial(ScenarioContext::new);

    private String mobile;
    private String greeting;
    private String originalWindow;
    private final List<String> focusOrder = new ArrayList<>();
    private Set<String> windowsBefore = new HashSet<>();

    private ScenarioContext() {
    }

    public static ScenarioContext get() {
        return CURRENT.get();
    }

    public static void reset() {
        CURRENT.remove();
    }

    public String mobile() {
        return mobile;
    }

    public void mobile(String mobile) {
        this.mobile = mobile;
    }

    public String greeting() {
        return greeting;
    }

    public void greeting(String greeting) {
        this.greeting = greeting;
    }

    public String originalWindow() {
        return originalWindow;
    }

    public void originalWindow(String handle) {
        this.originalWindow = handle;
    }

    public List<String> focusOrder() {
        return focusOrder;
    }

    public Set<String> windowsBefore() {
        return windowsBefore;
    }

    public void windowsBefore(Set<String> handles) {
        this.windowsBefore = new HashSet<>(handles);
    }
}
