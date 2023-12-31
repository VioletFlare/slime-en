package com.github.nekolr.slime.executor.function.extension;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExtension;
import com.github.nekolr.slime.util.FeedUtils;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FeedFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return SyndFeed.class;
    }

    @Comment("Get Author")
    @Example("${feedVar.author()}")
    public static String author(SyndFeed feed) {
        return FeedUtils.getAuthor(feed);
    }

    @Comment("Get Title")
    @Example("${feedVar.title()}")
    public static String title(SyndFeed feed) {
        return FeedUtils.getTitle(feed);
    }

    @Comment("Get generator")
    @Example("${feedVar.generator()}")
    public static String generator(SyndFeed feed) {
        return FeedUtils.getGenerator(feed);
    }

    @Comment("Get Link")
    @Example("${feedVar.link()}")
    public static String link(SyndFeed feed) {
        return FeedUtils.getLink(feed);
    }

    @Comment("Get Involved")
    @Example("${feedVar.webMaster()}")
    public static String webMaster(SyndFeed feed) {
        return FeedUtils.getWebMaster(feed);
    }

    @Comment("Get help")
    @Example("${feedVar.desc()}")
    public static String desc(SyndFeed feed) {
        return FeedUtils.getDescription(feed);
    }

    @Comment("Get Entity Sets")
    @Example("${feedVar.entries()}")
    public static List<SyndEntry> entries(SyndFeed feed) {
        return FeedUtils.getEntries(feed);
    }

    @Comment("Get an entity")
    @Example("${feedVar.entries(0)}")
    public static SyndEntry entries(SyndFeed feed, int index) {
        return FeedUtils.getEntry(feed, index).orElse(null);
    }

}
