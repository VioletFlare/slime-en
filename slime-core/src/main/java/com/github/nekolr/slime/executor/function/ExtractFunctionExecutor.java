package com.github.nekolr.slime.executor.function;

import com.github.nekolr.slime.util.ExtractUtils;
import com.github.nekolr.slime.util.FeedUtils;
import com.rometools.rome.feed.synd.SyndFeed;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExecutor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Comment("Data extraction - Frequently asked questions")
public class ExtractFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "extract";
    }

    @Comment("Based on jsonpath Extract Contents")
    @Example("${extract.jsonpath(resp.json,'$.code')}")
    public static Object jsonpath(Object root, String jsonpath) {
        return ExtractUtils.getValueByJsonPath(root, jsonpath);
    }

    @Comment("Extract based on regular expression")
    @Example("${extract.regx(resp.html,'<title>(.*?)</title>')}")
    public static String regx(String content, String pattern) {
        return ExtractUtils.getFirstMatcher(content, pattern, true);
    }

    @Comment("Extract based on regular expression")
    @Example("${extract.regx(resp.html,'<title>(.*?)</title>',1)}")
    public static String regx(String content, String pattern, int groupIndex) {
        return ExtractUtils.getFirstMatcher(content, pattern, groupIndex);
    }

    @Comment("Extract based on regular expression")
    @Example("${extract.regx(resp.html,'<a href=\"(.*?)\">(.*?)</a>',[1,2])}")
    public static List<String> regx(String content, String pattern, List<Integer> groups) {
        return ExtractUtils.getFirstMatcher(content, pattern, groups);
    }

    @Comment("Extract based on regular expression")
    @Example("${extract.regxs(resp.html,'<h2>(.*?)</h2>')}")
    public static List<String> regxs(String content, String pattern) {
        return ExtractUtils.getMatchers(content, pattern, true);
    }

    @Comment("Extract based on regular expression")
    @Example("${extract.regxs(resp.html,'<h2>(.*?)</h2>',1)}")
    public static List<String> regxs(String content, String pattern, int groupIndex) {
        return ExtractUtils.getMatchers(content, pattern, groupIndex);
    }

    @Comment("Extract based on regular expression")
    @Example("${extract.regxs(resp.html,'<a href=\"(.*?)\">(.*?)</a>',[1,2])}")
    public static List<List<String>> regxs(String content, String pattern, List<Integer> groups) {
        return ExtractUtils.getMatchers(content, pattern, groups);
    }

    @Comment("Based on xpath Extract Contents")
    @Example("${extract.xpath(resp.element(),'//title/text()')}")
    public static String xpath(Element element, String xpath) {
        return ExtractUtils.getValueByXPath(element, xpath);
    }

    @Comment("Based on xpath Extract Contents")
    @Example("${extract.xpath(resp.html,'//title/text()')}")
    public static String xpath(String content, String xpath) {
        return xpath(Jsoup.parse(content), xpath);
    }

    @Comment("Based on xpaths Extract Contents")
    @Example("${extract.xpaths(resp.element(),'//h2/text()')}")
    public static List<String> xpaths(Element element, String xpath) {
        return ExtractUtils.getValuesByXPath(element, xpath);
    }

    @Comment("Based on xpaths Extract Contents")
    @Example("${extract.xpaths(resp.html,'//h2/text()')}")
    public static List<String> xpaths(String content, String xpath) {
        return xpaths(Jsoup.parse(content), xpath);
    }

    @Comment("Based on css Selector Extraction")
    @Example("${extract.selectors(resp.html,'div > a')}")
    public static List<String> selectors(Object object, String selector) {
        return ExtractUtils.getHTMLBySelector(getElement(object), selector);
    }

    @Comment("Based on css Selector Extraction")
    @Example("${extract.selector(resp.html,'div > a','text')}")
    public static Object selector(Object object, String selector, String type) {
        if ("element".equals(type)) {
            return ExtractUtils.getFirstElement(getElement(object), selector);
        } else if ("text".equals(type)) {
            return ExtractUtils.getFirstTextBySelector(getElement(object), selector);
        } else if ("outerhtml".equals(type)) {
            return ExtractUtils.getFirstOuterHTMLBySelector(getElement(object), selector);
        }
        return null;
    }

    @Comment("Based on css Selector Extraction")
    @Example("${extract.selector(resp.html,'div > a','attr','href')}")
    public static String selector(Object object, String selector, String type, String attrValue) {
        if ("attr".equals(type)) {
            return ExtractUtils.getFirstAttrBySelector(getElement(object), selector, attrValue);
        }
        return null;
    }

    @Comment("Based on css Selector Extraction")
    @Example("${extract.selector(resp.html,'div > a')}")
    public static String selector(Object object, String selector) {
        return ExtractUtils.getFirstHTMLBySelector(getElement(object), selector);
    }

    @Comment("Based on css Selector Extraction")
    @Example("${extract.selectors(resp.html,'div > a','element')}")
    public static Object selectors(Object object, String selector, String type) {
        if ("element".equals(type)) {
            return ExtractUtils.getElements(getElement(object), selector);
        } else if ("text".equals(type)) {
            return ExtractUtils.getTextBySelector(getElement(object), selector);
        } else if ("outerhtml".equals(type)) {
            return ExtractUtils.getOuterHTMLBySelector(getElement(object), selector);
        }
        return null;
    }

    @Comment("Based on css Selector Extraction")
    @Example("${extract.selectors(resp.html,'div > a','attr','href')}")
    public static Object selectors(Object object, String selector, String type, String attrValue) {
        if ("attr".equals(type)) {
            return ExtractUtils.getAttrBySelector(getElement(object), selector, attrValue);
        }
        return null;
    }

    @Comment("Build by responding to content feed Assistant Phone")
    @Example("${extract.feed(resp.html)}")
    public static SyndFeed feed(Object xml) {
        return FeedUtils.getFeed((String) xml);
    }

    private static Element getElement(Object object) {
        if (object != null) {
            return object instanceof Element ? (Element) object : Jsoup.parse((String) object);
        }
        return null;
    }
}
