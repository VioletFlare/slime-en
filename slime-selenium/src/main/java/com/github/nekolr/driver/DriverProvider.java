package com.github.nekolr.driver;

import com.github.nekolr.slime.model.SpiderNode;
import org.openqa.selenium.WebDriver;

import java.net.MalformedURLException;

public interface DriverProvider {

    /**
     * Default Remote WebDriver Address
     */
    String DEFAULT_REMOTE_WEBDRIVER_URL = "http://localhost:4444";

    /**
     * Remote WebDriver Address
     */
    String REMOTE_WEBDRIVER_URL = "remote-webdriver-url";

    /**
     * Use js
     */
    String JAVASCRIPT_DISABLED = "javascript-disabled";

    /**
     * User-Agent
     */
    String USER_AGENT = "user-agent";

    /**
     * Headless Mode
     */
    String HEADLESS = "headless";

    /**
     * No picture loaded
     */
    String IMAGE_DISABLED = "image-disabled";

    /**
     * Hide Scrollbars（Sometimes you need to deal with a page that has a scrollbar hidden）
     */
    String HIDE_SCROLLBAR = "hide-scrollbar";

    /**
     * The plugin list contains some obsolete plugins. Enabling them will not have any effect.
     */
    String PLUGIN_DISABLE = "plugin-disable";

    /**
     * Use java
     */
    String JAVA_DISABLE = "java-disable";

    /**
     * Invisible Mode（No trace mode）
     */
    String INCOGNITO = "incognito";

    /**
     * Enable sandbox mode
     */
    String NO_SANDBOX = "no-sandbox";

    /**
     * Size of the assistant window
     */
    String WINDOW_SIZE = "window-size";

    /**
     * Maximize
     */
    String MAXIMIZED = "maximized";

    /**
     * Use gpu Accelerator
     */
    String GPU_DISABLE = "gpu-disable";

    /**
     * Other Parameters
     */
    String ARGUMENTS = "arguments";

    /**
     * Please translate the following text to english
     */
    String support();

    /**
     * Get WebDriver
     *
     * @param node     15th Last
     * @param proxyStr 代理
     * @return WebDriver
     */
    WebDriver getWebDriver(SpiderNode node, String proxyStr) throws MalformedURLException;
}
