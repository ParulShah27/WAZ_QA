package com.wealthverze.qa.core;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Input helpers. Paste is simulated with a real ClipboardEvent so the app's
 * onPaste normalisation is exercised rather than bypassed. A script-dispatched
 * paste has no default action, so when the app does not handle the event the
 * text is inserted with insertText, which fires the same input event a real
 * paste does (and so reaches React's onChange).
 */
public final class Inputs {

    private Inputs() {
    }

    public static void clear(WebElement element) {
        element.sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.DELETE);
    }

    public static void replace(WebElement element, String text) {
        element.click();
        clear(element);
        element.sendKeys(text);
    }

    public static void typeKeyByKey(WebElement element, String text) {
        for (char c : text.toCharArray()) {
            element.sendKeys(String.valueOf(c));
        }
    }

    public static void paste(WebDriver driver, WebElement element, String text) {
        element.click();
        clear(element);
        ((JavascriptExecutor) driver).executeScript(
                "const el = arguments[0];"
                        + "const dt = new DataTransfer();"
                        + "dt.setData('text/plain', arguments[1]);"
                        + "const handled = !el.dispatchEvent(new ClipboardEvent('paste',"
                        + " {clipboardData: dt, bubbles: true, cancelable: true}));"
                        + "if (!handled) { el.focus(); document.execCommand('insertText', false, arguments[1]); }",
                element, text);
    }
}
