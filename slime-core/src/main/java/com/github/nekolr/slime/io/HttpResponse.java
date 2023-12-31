package com.github.nekolr.slime.io;

import com.alibaba.fastjson.JSON;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import java.io.InputStream;
import java.util.Map;

/**
 * Respondent Packaging Type
 */
public class HttpResponse implements SpiderResponse {

    /**
     * Respond
     */
    private Response response;

    /**
     * State code
     */
    private int statusCode;

    /**
     * URL
     */
    private String urlLink;

    /**
     * Responding HTML 内容
     */
    private String htmlValue;

    /**
     * Respondent
     */
    private String title;

    /**
     * Responding json 内容
     */
    private Object jsonValue;

    public HttpResponse(Response response) {
        this.response = response;
        this.statusCode = response.statusCode();
        this.urlLink = response.url().toExternalForm();
    }

    @Override
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String getTitle() {
        if (title == null) {
            synchronized (this) {
                title = Jsoup.parse(getHtml()).title();
            }
        }
        return title;
    }

    @Override
    public String getHtml() {
        if (htmlValue == null) {
            synchronized (this) {
                htmlValue = response.body();
            }
        }
        return htmlValue;
    }

    @Override
    public Object getJson() {
        if (jsonValue == null) {
            jsonValue = JSON.parse(getHtml());
        }
        return jsonValue;
    }

    @Override
    public Map<String, String> getCookies() {
        return response.cookies();
    }

    @Override
    public Map<String, String> getHeaders() {
        return response.headers();
    }

    @Override
    public byte[] getBytes() {
        return response.bodyAsBytes();
    }

    @Override
    public String getContentType() {
        return response.contentType();
    }

    @Override
    public void setCharset(String charset) {
        this.response.charset(charset);
    }

    @Override
    public String getUrl() {
        return urlLink;
    }

    @Override
    public InputStream getStream() {
        return response.bodyStream();
    }

}
