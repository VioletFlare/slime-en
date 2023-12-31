package com.github.nekolr.slime.io;

import com.alibaba.fastjson.JSON;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;

import java.io.InputStream;
import java.util.Map;

public interface SpiderResponse {

    @Comment("Get the return value")
    @Example("${resp.statusCode}")
    int getStatusCode();

    @Comment("Get web page title")
    @Example("${resp.title}")
    String getTitle();

    @Comment("Get Web Page html")
    @Example("${resp.html}")
    String getHtml();

    @Comment("Get json")
    @Example("${resp.json}")
    default Object getJson() {
        return JSON.parse(getHtml());
    }

    @Comment("Get cookies")
    @Example("${resp.cookies}")
    Map<String, String> getCookies();

    @Comment("Get headers")
    @Example("${resp.headers}")
    Map<String, String> getHeaders();

    @Comment("Get byte[]")
    @Example("${resp.bytes}")
    byte[] getBytes();

    @Comment("Get ContentType")
    @Example("${resp.contentType}")
    String getContentType();

    @Comment("Get current url")
    @Example("${resp.url}")
    String getUrl();

    @Example("${resp.setCharset('UTF-8')}")
    default void setCharset(String charset) {
    }

    @Example("${resp.stream}")
    default InputStream getStream() {
        return null;
    }
}
