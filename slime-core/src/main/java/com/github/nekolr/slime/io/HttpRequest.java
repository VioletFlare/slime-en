package com.github.nekolr.slime.io;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

/**
 * Request object packaging class
 */
public class HttpRequest {

    private Connection connection = null;

    public static HttpRequest create() {
        return new HttpRequest();
    }

    public HttpRequest url(String url) {
        this.connection = Jsoup.connect(url);
        // Default GET
        this.connection.method(Method.GET);
        // Request timed out 30s
        this.connection.timeout(30000);
        return this;
    }

    public HttpRequest headers(Map<String, String> headers) {
        this.connection.headers(headers);
        return this;
    }

    public HttpRequest header(String key, String value) {
        this.connection.header(key, value);
        return this;
    }

    public HttpRequest header(String key, Object value) {
        if (value != null) {
            this.connection.header(key, value.toString());
        }
        return this;
    }

    public HttpRequest cookies(Map<String, String> cookies) {
        this.connection.cookies(cookies);
        return this;
    }

    public HttpRequest cookie(String name, String value) {
        if (value != null) {
            this.connection.cookie(name, value);
        }
        return this;
    }

    public HttpRequest contentType(String contentType) {
        this.connection.header("Content-Type", contentType);
        return this;
    }

    public HttpRequest data(String key, String value) {
        this.connection.data(key, value);
        return this;
    }

    public HttpRequest data(String key, Object value) {
        if (value != null) {
            this.connection.data(key, value.toString());
        }
        return this;
    }

    public HttpRequest data(String key, String filename, InputStream is) {
        this.connection.data(key, filename, is);
        return this;
    }

    public HttpRequest requestBody(Object body) {
        if (body != null) {
            this.connection.requestBody(body.toString());
        }
        return this;
    }

    public HttpRequest data(Map<String, String> data) {
        this.connection.data(data);
        return this;
    }

    public HttpRequest method(String method) {
        this.connection.method(Method.valueOf(method));
        return this;
    }

    public HttpRequest followRedirects(boolean followRedirects) {
        this.connection.followRedirects(followRedirects);
        return this;
    }

    public HttpRequest timeout(int timeout) {
        this.connection.timeout(timeout);
        return this;
    }

    public HttpRequest proxy(String host, int port) {
        this.connection.proxy(host, port);
        return this;
    }

    public HttpResponse execute() throws IOException {
        // 忽略 ContentType
        this.connection.ignoreContentType(true);
        // Ignore request error
        this.connection.ignoreHttpErrors(true);
        // Speak body The maximum number of bytes to translate，0 For unlimited
        this.connection.maxBodySize(0);

        Response response = connection.execute();
        return new HttpResponse(response);
    }
}
