package com.github.nekolr.driver;

import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.model.SpiderNode;
import com.github.nekolr.slime.util.IoUtils;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class ChromeDriverProvider implements DriverProvider {

    private static final String USER_AGENT_OPTION = "user-agent=\"%s\"";
    private static final String DISABLED_JAVASCRIPT_OPTION = "–-disable-javascript";
    private static final String IMAGES_DISABLED_OPTION = "blink-settings=imagesEnabled=false";
    private static final String HIDE_SCROLLBARS_OPTION = "--hide-scrollbars";
    private static final String NO_SANDBOX_OPTION = "--no-sandbox";
    private static final String INCOGNITO_OPTION = "--incognito";
    private static final String DISABLE_PLUGINS_OPTION = "--disable-plugins";
    private static final String DISABLE_JAVA_OPTION = "--disable-java";
    private static final String WINDOW_SIZE_OPTION = "--window-size=%s";
    private static final String MAXIMIZED_OPTION = "--start-maximized";
    private static final String DISABLE_GPU_OPTION = "--disable-gpu";

    @Override
    public String support() {
        return Browser.CHROME.browserName();
    }

    @Override
    public WebDriver getWebDriver(SpiderNode node, String proxyStr) throws MalformedURLException {

        ChromeOptions options = new ChromeOptions();

        String userAgent = node.getJsonProperty(USER_AGENT);
        // 1 hour before appointment User-Agent
        if (StringUtils.isNotBlank(userAgent)) {
            options.addArguments(String.format(USER_AGENT_OPTION, userAgent));
        }

        // Use JS
        if (Constants.YES.equals(node.getJsonProperty(JAVASCRIPT_DISABLED))) {
            options.addArguments(DISABLED_JAVASCRIPT_OPTION);
        }

        // No picture loaded
        if (Constants.YES.equals(node.getJsonProperty(IMAGE_DISABLED))) {
            options.addArguments(IMAGES_DISABLED_OPTION);
        }

        // Hide Scrollbars
        if (Constants.YES.equals(node.getJsonProperty(HIDE_SCROLLBAR))) {
            options.addArguments(HIDE_SCROLLBARS_OPTION);
        }

        // Headless Mode
        if (Constants.YES.equals(node.getJsonProperty(HEADLESS))) {
            options.setHeadless(true);
        }

        // Enable sandbox mode
        if (Constants.YES.equals(node.getJsonProperty(NO_SANDBOX))) {
            options.addArguments(NO_SANDBOX_OPTION);
        }

        // Invisible Mode
        if (Constants.YES.equals(node.getJsonProperty(INCOGNITO))) {
            options.addArguments(INCOGNITO_OPTION);
        }

        // The plugin list contains some obsolete plugins. Enabling them will not have any effect.
        if (Constants.YES.equals(node.getJsonProperty(PLUGIN_DISABLE))) {
            options.addArguments(DISABLE_PLUGINS_OPTION);
        }

        // Use Java
        if (Constants.YES.equals(node.getJsonProperty(JAVA_DISABLE))) {
            options.addArguments(DISABLE_JAVA_OPTION);
        }

        // Please set the window size
        String windowSize = node.getJsonProperty(WINDOW_SIZE);
        if (StringUtils.isNotBlank(windowSize)) {
            options.addArguments(String.format(WINDOW_SIZE_OPTION, windowSize));
        }

        // Maximize
        if (Constants.YES.equals(node.getJsonProperty(MAXIMIZED))) {
            options.addArguments(MAXIMIZED_OPTION);
        }

        // Use gpu Accelerator
        if (Constants.YES.equals(node.getJsonProperty(GPU_DISABLE))) {
            options.addArguments(DISABLE_GPU_OPTION);
        }

        // Set other options
        String arguments = node.getJsonProperty(ARGUMENTS);
        if (StringUtils.isNotBlank(arguments)) {
            options.addArguments(Arrays.asList(arguments.split("\r\n")));
        }

        // Set Up Proxy
        if (StringUtils.isNotBlank(proxyStr)) {
            Proxy proxy = new Proxy();
            proxy.setHttpProxy(proxyStr);
            options.setProxy(proxy);
        }

        // Default Remove “chrome Under automatic test software control”  of the following text
        options.setExperimentalOption("useAutomationExtension", false);
        options.setExperimentalOption("excludeSwitches", Collections.singleton("enable-automation"));

        String remoteWebdriverUrl = node.getJsonProperty(REMOTE_WEBDRIVER_URL, DEFAULT_REMOTE_WEBDRIVER_URL);
        WebDriver driver = new CdpRemoteWebDriver(new URL(remoteWebdriverUrl), options);

        InputStream input = ChromeDriverProvider.class.getClassLoader().getResourceAsStream("stealth.min.js");
        String source = IoUtils.readStreamToString(input);

        Map<String, Object> params = new HashMap<>();
        params.put("source", source);

        ((CdpRemoteWebDriver) driver).executeCdpCommand("Page.addScriptToEvaluateOnNewDocument", params);

        return driver;
    }
}
