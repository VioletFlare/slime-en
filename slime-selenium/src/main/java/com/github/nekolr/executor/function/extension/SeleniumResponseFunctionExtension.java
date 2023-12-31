package com.github.nekolr.executor.function.extension;

import com.github.nekolr.io.SeleniumResponse;
import com.github.nekolr.model.WebElements;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.context.SpiderContextHolder;
import com.github.nekolr.slime.executor.FunctionExtension;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SeleniumResponseFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return SeleniumResponse.class;
    }

    @Comment("Based on css Choose the best answer to the question")
    @Example("${resp.selector('div > a')}")
    public static WebElementWrapper selector(SeleniumResponse response, String css) {
        try {
            return new WebElementWrapper(response, response.getDriver().findElement(By.cssSelector(css)));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Comment("Based on css Choose the best answer to the question")
    @Example("${resp.selectors('div > a')}")
    public static WebElements selectors(SeleniumResponse response, String css) {
        try {
            return new WebElements(response, response.getDriver().findElements(By.cssSelector(css)));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Comment("Based on xpath Look in the request results for")
    @Example("${resp.xpaths('//a')}")
    public static WebElements xpaths(SeleniumResponse response, String xpath) {
        try {
            return new WebElements(response, response.getDriver().findElements(By.xpath(xpath)));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Comment("Based on xpath Look in the request results for")
    @Example("${resp.xpath('//a')}")
    public static WebElement xpath(SeleniumResponse response, String xpath) {
        try {
            return new WebElementWrapper(response, response.getDriver().findElement(By.xpath(xpath)));
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    @Comment("执行 js")
    @Example("${resp.executeScript(\"document.write('hello world!')\")}")
    public static Object executeScript(SeleniumResponse response, String script) {
        return executeScript(response, script, null);
    }

    @Comment("执行 js")
    @Example("${resp.executeScript(\"document.write('hello '+arguments[0]+' !')\",\"world\")}")
    public static Object executeScript(SeleniumResponse response, String script, List<Object> arguments) {
        JavascriptExecutor executor;
        try {
            executor = (JavascriptExecutor) response.getDriver();
        } catch (Throwable e) {
            throw new RuntimeException("The following actions are available: js");
        }
        return executor.executeScript(script, arguments);
    }

    @Comment("jump URL")
    @Example("${resp.toUrl(newUrl)}")
    public static SeleniumResponse toUrl(SeleniumResponse response, String newUrl) {
        response.getDriver().get(newUrl);
        return response;
    }

    @Comment("Loading Cookies")
    @Example("${resp.loadCookies()}")
    public static SeleniumResponse loadCookies(SeleniumResponse response) {
        SpiderContext spiderContext = SpiderContextHolder.get();
        Map<String, String> cookieContext = spiderContext.getCookieContext();
        Map<String, String> cookies = response.getCookies();
        if (cookies != null && !cookies.isEmpty()) {
            cookieContext.putAll(cookies);
        }
        return response;
    }
}
