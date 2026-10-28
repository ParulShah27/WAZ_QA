package com.wealthverze.qa.core;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Resolution order: -D system property, then environment variable, then config/test.properties.
 */
public final class Config {

    private static final Properties FILE = load();

    private Config() {
    }

    private static Properties load() {
        Properties props = new Properties();
        try (InputStream in = Config.class.getClassLoader().getResourceAsStream("config/test.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read config/test.properties", e);
        }
        return props;
    }

    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys;
        }
        String env = System.getenv(key.toUpperCase().replace('.', '_').replace('-', '_'));
        if (env != null && !env.isBlank()) {
            return env;
        }
        return FILE.getProperty(key);
    }

    public static int intValue(String key, int fallback) {
        String v = get(key);
        return v == null ? fallback : Integer.parseInt(v.trim());
    }

    public static boolean bool(String key) {
        return Boolean.parseBoolean(get(key));
    }

    public static String appUrl() {
        return get("app.url").replaceAll("/+$", "");
    }

    public static String familyHeadMobile() {
        return get("mobile.family-head");
    }

    public static String familyMemberMobile() {
        return get("mobile.family-member");
    }

    public static String unregisteredMobile() {
        return get("mobile.unregistered");
    }

    public static String testOtp() {
        return get("otp.test");
    }

    public static boolean secureOrigin() {
        return bool("secure.origin");
    }
}
