package com.github.nekolr.slime.executor.function.extension;

import com.github.nekolr.slime.util.ExtractUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExtension;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ElementsFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return Elements.class;
    }

    @Comment("Based on xpath Extract Contents")
    @Example("${elementsVar.xpath('//title/text()')}")
    public static String xpath(Elements elements, String xpath) {
        return ExtractUtils.getValueByXPath(elements, xpath);
    }

    @Comment("Based on xpath Extract Contents")
    @Example("${elementsVar.xpaths('//h2/text()')}")
    public static List<String> xpaths(Elements elements, String xpath) {
        return ExtractUtils.getValuesByXPath(elements, xpath);
    }

    @Comment("Extract based on regular expression")
    @Example("${elementsVar.regx('<title>(.*?)</title>')}")
    public static String regx(Elements elements, String regx) {
        return ExtractUtils.getFirstMatcher(elements.html(), regx, true);
    }

    @Comment("Extract based on regular expression")
    @Example("${elementsVar.regx('<title>(.*?)</title>',1)}")
    public static String regx(Elements elements, String regx, int groupIndex) {
        return ExtractUtils.getFirstMatcher(elements.html(), regx, groupIndex);
    }

    @Comment("Extract based on regular expression")
    @Example("${elementsVar.regx('<a href=\"(.*?)\">(.*?)</a>',[1,2])}")
    public static List<String> regx(Elements elements, String regx, List<Integer> groups) {
        return ExtractUtils.getFirstMatcher(elements.html(), regx, groups);
    }

    @Comment("Extract based on regular expression")
    @Example("${elementsVar.regxs('<h2>(.*?)</h2>')}")
    public static List<String> regxs(Elements elements, String regx) {
        return ExtractUtils.getMatchers(elements.html(), regx, true);
    }

    @Comment("Extract based on regular expression")
    @Example("${elementsVar.regxs('<h2>(.*?)</h2>',1)}")
    public static List<String> regxs(Elements elements, String regx, int groupIndex) {
        return ExtractUtils.getMatchers(elements.html(), regx, groupIndex);
    }

    @Comment("Extract based on regular expression")
    @Example("${elementsVar.regxs('<a href=\"(.*?)\">(.*?)</a>',[1,2])}")
    public static List<List<String>> regxs(Elements elements, String regx, List<Integer> groups) {
        return ExtractUtils.getMatchers(elements.html(), regx, groups);
    }

    @Comment("Based on css Selector Extraction")
    @Example("${elementsVar.selector('div > a')}")
    public static Element selector(Elements elements, String selector) {
        Elements foundElements = elements.select(selector);
        if (foundElements.size() > 0) {
            return foundElements.get(0);
        }
        return null;
    }

    @Comment("All attr")
    @Example("${elementsVar.attrs('href')}")
    public static List<String> attrs(Elements elements, String key) {
        List<String> list = new ArrayList<>(elements.size());
        for (Element element : elements) {
            list.add(element.attr(key));
        }
        return list;
    }

    @Comment("All value")
    @Example("${elementsVar.vals()}")
    public static List<String> vals(Elements elements) {
        List<String> list = new ArrayList<>(elements.size());
        for (Element element : elements) {
            list.add(element.val());
        }
        return list;
    }

    @Comment("All text")
    @Example("${elementsVar.texts()}")
    public static List<String> texts(Elements elements) {
        List<String> list = new ArrayList<>(elements.size());
        for (Element element : elements) {
            list.add(element.text());
        }
        return list;
    }

    @Comment("All html")
    @Example("${elementsVar.htmls()}")
    public static List<String> htmls(Elements elements) {
        List<String> list = new ArrayList<>(elements.size());
        for (Element element : elements) {
            list.add(element.html());
        }
        return list;
    }

    @Comment("All outerHtml")
    @Example("${elementsVar.outerHtmls()}")
    public static List<String> outerHtmls(Elements elements) {
        List<String> list = new ArrayList<>(elements.size());
        for (Element element : elements) {
            list.add(element.outerHtml());
        }
        return list;
    }

    @Comment("All ownTexts")
    @Example("${elementsVar.ownTexts()}")
    public static List<String> ownTexts(Elements elements) {
        List<String> list = new ArrayList<>(elements.size());
        for (Element element : elements) {
            list.add(element.ownText());
        }
        return list;
    }

    @Comment("All wholeText")
    @Example("${elementsVar.wholeTexts()}")
    public static List<String> wholeTexts(Elements elements) {
        List<String> list = new ArrayList<>(elements.size());
        for (Element element : elements) {
            list.add(element.wholeText());
        }
        return list;
    }

    @Comment("Based on css Selector Extraction")
    @Example("${elementsVar.selectors('div > a')}")
    public static Elements selectors(Elements elements, String selector) {
        return elements.select(selector);
    }

    @Comment("Get parent node")
    @Example("${elementsVar.parents()}")
    public static Elements parents(Elements elements) {
        return elements.parents();
    }

}
