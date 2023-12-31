package com.github.nekolr.slime.executor.function.extension;

import com.github.nekolr.slime.util.ExtractUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExtension;
import com.github.nekolr.slime.io.SpiderResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
public class ResponseFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return SpiderResponse.class;
    }

    @Comment("Turn request results into Element 对象")
    @Example("${resp.element()}")
    public static Element element(SpiderResponse response) {
        return Jsoup.parse(response.getHtml(), response.getUrl());
    }

    @Comment("Based on xpath Look in the request results for")
    @Example("${resp.xpath('//title/text()')}")
    public static String xpath(SpiderResponse response, String xpath) {
        return ExtractUtils.getValueByXPath(element(response), xpath);
    }

    @Comment("Based on xpath Look in the request results for")
    @Example("${resp.xpaths('//a/@href')}")
    public static List<String> xpaths(SpiderResponse response, String xpath) {
        return ExtractUtils.getValuesByXPath(element(response), xpath);
    }

    @Comment("Extract the contents of the match expression into the request.")
    @Example("${resp.regx('<title>(.*?)</title>')}")
    public static String regx(SpiderResponse response, String pattern) {
        return ExtractUtils.getFirstMatcher(response.getHtml(), pattern, true);
    }

    @Comment("Extract the contents of the match expression into the request.")
    @Example("${resp.regx('<title>(.*?)</title>',1)}")
    public static String regx(SpiderResponse response, String pattern, int groupIndex) {
        return ExtractUtils.getFirstMatcher(response.getHtml(), pattern, groupIndex);
    }

    @Comment("Extract the contents of the match expression into the request.")
    @Example("${resp.regx('<a href=\"(.*?)\">(.*?)</a>',[1,2])}")
    public static List<String> regx(SpiderResponse response, String pattern, List<Integer> groups) {
        return ExtractUtils.getFirstMatcher(response.getHtml(), pattern, groups);
    }

    @Comment("Extract the contents of the match expression into the request.")
    @Example("${resp.regxs('<h2>(.*?)</h2>')}")
    public static List<String> regxs(SpiderResponse response, String pattern) {
        return ExtractUtils.getMatchers(response.getHtml(), pattern, true);
    }

    @Comment("Extract the contents of the match expression into the request.")
    @Example("${resp.regxs('<h2>(.*?)</h2>',1)}")
    public static List<String> regxs(SpiderResponse response, String pattern, int groupIndex) {
        return ExtractUtils.getMatchers(response.getHtml(), pattern, groupIndex);
    }

    @Comment("Extract the contents of the match expression into the request.")
    @Example("${resp.regxs('<a href=\"(.*?)\">(.*?)</a>',[1,2])}")
    public static List<List<String>> regxs(SpiderResponse response, String pattern, List<Integer> groups) {
        return ExtractUtils.getMatchers(response.getHtml(), pattern, groups);
    }

    @Comment("Based on css Choose the best answer to the question")
    @Example("${resp.selector('div > a')}")
    public static Element selector(SpiderResponse response, String selector) {
        return ElementFunctionExtension.selector(element(response), selector);
    }

    @Comment("Based on css Choose the best answer to the question")
    @Example("${resp.selectors('div > a')}")
    public static Elements selectors(SpiderResponse response, String selector) {
        return ElementFunctionExtension.selectors(element(response), selector);
    }

    @Comment("Based on jsonpath Extract archives asking the destination folder")
    @Example("${resp.jsonpath('$.code')}")
    public static Object jsonpath(SpiderResponse response, String path) {
        return ExtractUtils.getValueByJsonPath(response.getJson(), path);
    }

    @Comment("Get Links on Page")
    @Example("${resp.links()}")
    public static List<String> links(SpiderResponse response) {
        return ExtractUtils.getAttrBySelector(element(response), "a", "abs:href")
                .stream()
                .filter(link -> StringUtils.isNotBlank(link))
                .collect(Collectors.toList());
    }

    @Comment("Get Links on Page")
    @Example("${resp.links('https://www\\.xxx\\.com/xxxx/(.*?)')}")
    public static List<String> links(SpiderResponse response, String regx) {
        Pattern pattern = Pattern.compile(regx);
        return links(response)
                .stream()
                .filter(link -> pattern.matcher(link).matches())
                .collect(Collectors.toList());
    }

    @Comment("Get all image links on the current page")
    @Example("${resp.images()}")
    public static List<String> images(SpiderResponse response) {
        return ExtractUtils.getAttrBySelector(element(response), "img", "src")
                .stream()
                .filter(link -> StringUtils.isNotBlank(link))
                .collect(Collectors.toList());
    }
}
