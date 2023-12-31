package com.github.nekolr.slime.executor.function.extension;

import com.github.nekolr.slime.util.ExtractUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExtension;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ElementFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return Element.class;
    }

    @Comment("Based on xpath Extract Contents")
    @Example("${elementVar.xpath('//title/text()')}")
    public static String xpath(Element element, String xpath) {
        return ExtractUtils.getValueByXPath(element, xpath);
    }


    @Comment("Based on xpath Extract Contents")
    @Example("${elementVar.xpaths('//h2/text()')}")
    public static List<String> xpaths(Element element, String xpath) {
        return ExtractUtils.getValuesByXPath(element, xpath);
    }

    @Comment("Extract based on regular expression")
    @Example("${elementVar.regx('<title>(.*?)</title>')}")
    public static String regx(Element element, String regx) {
        return ExtractUtils.getFirstMatcher(element.html(), regx, true);
    }

    @Comment("Extract based on regular expression")
    @Example("${elementVar.regx('<title>(.*?)</title>',1)}")
    public static String regx(Element element, String regx, int groupIndex) {
        return ExtractUtils.getFirstMatcher(element.html(), regx, groupIndex);
    }

    @Comment("Extract based on regular expression")
    @Example("${elementVar.regx('<a href=\"(.*?)\">(.*?)</a>',[1,2])}")
    public static List<String> regx(Element element, String regx, List<Integer> groups) {
        return ExtractUtils.getFirstMatcher(element.html(), regx, groups);
    }

    @Comment("Extract based on regular expression")
    @Example("${elementVar.regxs('<h2>(.*?)</h2>')}")
    public static List<String> regxs(Element element, String regx) {
        return ExtractUtils.getMatchers(element.html(), regx, true);
    }

    @Comment("Extract based on regular expression")
    @Example("${elementVar.regxs('<h2>(.*?)</h2>',1)}")
    public static List<String> regxs(Element element, String regx, int groupIndex) {
        return ExtractUtils.getMatchers(element.html(), regx, groupIndex);
    }

    @Comment("Extract based on regular expression")
    @Example("${elementVar.regxs('<a href=\"(.*?)\">(.*?)</a>',[1,2])}")
    public static List<List<String>> regxs(Element element, String regx, List<Integer> groups) {
        return ExtractUtils.getMatchers(element.html(), regx, groups);
    }

    @Comment("Based on css Selector Extraction")
    @Example("${elementVar.selector('div > a')}")
    public static Element selector(Element element, String cssQuery) {
        return element.selectFirst(cssQuery);
    }

    @Comment("Based on css Selector Extraction")
    @Example("${elementVar.selectors('div > a')}")
    public static Elements selectors(Element element, String cssQuery) {
        return element.select(cssQuery);
    }

    @Comment("Get Peers")
    @Example("${elementVar.subling()}")
    public static Elements subling(Element element) {
        return element.siblingElements();
    }

    @Comment("Get parent node")
    @Example("${elementVar.parent()}")
    public static Element parent(Element element) {
        return element.parent();
    }

    @Comment("Get parent node")
    @Example("${elementVar.parents()}")
    public static Elements parents(Element element) {
        return element.parents();
    }
}
