package com.github.nekolr.slime.executor.function.extension;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExtension;
import com.github.nekolr.slime.util.FeedUtils;
import com.rometools.rome.feed.synd.SyndEntry;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class FeedEntryFunctionExtension implements FunctionExtension {

    @Override
    public Class<?> support() {
        return SyndEntry.class;
    }

    @Comment("Get the author of an entity")
    @Example("${entryVar.author()}")
    public static String author(SyndEntry entry) {
        return FeedUtils.getAuthor(entry);
    }

    @Comment("Get Link to Entity")
    @Example("${entryVar.link()}")
    public static String link(SyndEntry entry) {
        return FeedUtils.getLink(entry);
    }

    @Comment("Get the title of an entity")
    @Example("${entryVar.title()}")
    public static String title(SyndEntry entry) {
        return FeedUtils.getTitle(entry);
    }

    @Comment("Get the entities of a concept uri（guid）")
    @Example("${entryVar.uri()}")
    public static String uri(SyndEntry entry) {
        return FeedUtils.getUri(entry);
    }

    @Comment("Get the description of an entity")
    @Example("${entryVar.desc()}")
    public static String desc(SyndEntry entry) {
        return FeedUtils.getDescription(entry);
    }

    @Comment("Get the publish time of an entity")
    @Example("${entryVar.pubDate()}")
    public static Date pubDate(SyndEntry entry) {
        return FeedUtils.getPublishedDate(entry);
    }
}
