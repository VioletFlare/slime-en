package com.github.nekolr.driver;

import com.github.nekolr.slime.constant.Constants;
import com.github.nekolr.slime.model.SpiderNode;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.remote.Browser;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

@Component
public class FireFoxDriverProvider implements DriverProvider {

    @Override
    public String support() {
        return Browser.FIREFOX.browserName();
    }

    @Override
    public WebDriver getWebDriver(SpiderNode node, String proxyStr) throws MalformedURLException {

        FirefoxOptions options = new FirefoxOptions();

        FirefoxProfile profile = new FirefoxProfile();

        if (StringUtils.isNotBlank(proxyStr)) {
            String[] hp = proxyStr.split(":");
            profile.setPreference("network.proxy.type", 1);
            profile.setPreference("network.proxy.http", hp[0]);
            profile.setPreference("network.proxy.http_port", NumberUtils.toInt(hp[1], 8080));
        }

        // 1 hour before appointment User-Agent
        String userAgent = node.getJsonProperty(USER_AGENT);
        if (StringUtils.isNotBlank(userAgent)) {
            profile.setPreference("general.useragent.override", userAgent);
        }

        // Headless Mode
        if (Constants.YES.equals(node.getJsonProperty(HEADLESS))) {
            options.setHeadless(true);
        }

        // Whether to enable js firefox 必须启用 javascript
        // profile.setPreference("javascript.enabled",!"1".equals(node.getJsonProperty(JAVASCRIPT_DISABLED)));

        // Loading images is forbidden
        if (Constants.YES.equals(node.getJsonProperty(IMAGE_DISABLED))) {
            profile.setPreference("permissions.default.image", 2);
        }

        // Please set the window size
        String windowSize = node.getJsonProperty(WINDOW_SIZE);
        if (StringUtils.isNotBlank(windowSize)) {
            options.addArguments("--window-size=" + windowSize);
        }

        // Set other options
        String arguments = node.getJsonProperty(ARGUMENTS);
        if (StringUtils.isNotBlank(arguments)) {
            options.addArguments(Arrays.asList(arguments.split("\r\n")));
        }

        String preferences = node.getJsonProperty("preferences");
        if (StringUtils.isNotBlank(preferences)) {
            Arrays.asList(preferences.split("\r\n")).forEach(preference -> {
                int index = preference.indexOf("=");
                if (index > -1 && preference.length() > index + 1) {
                    String key = preference.substring(0, index);
                    String value = preference.substring(index + 1);
                    if (StringUtils.isNotBlank(value)) {
                        if ("true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value)) {
                            profile.setPreference(key, "true".equalsIgnoreCase(value));
                        } else if ("0".equals(value) || NumberUtils.toInt(value, 0) != 0) {
                            profile.setPreference(key, NumberUtils.toInt(value, 0));
                        } else {
                            profile.setPreference(key, value);
                        }
                    }
                }
            });
        }
        options.setProfile(profile);

        String remoteWebdriverUrl = node.getJsonProperty(REMOTE_WEBDRIVER_URL, DEFAULT_REMOTE_WEBDRIVER_URL);
        WebDriver webDriver = new RemoteWebDriver(new URL(remoteWebdriverUrl), options);

        // Maximize
        if (Constants.YES.equals(node.getJsonProperty(MAXIMIZED))) {
            webDriver.manage().window().maximize();
        }

        return webDriver;
    }
}
