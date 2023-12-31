package com.github.nekolr.slime.executor.function;

import com.github.nekolr.slime.util.FileUtils;
import org.apache.commons.io.IOUtils;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExecutor;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * File Read/ Write
 */
@Component
@Comment("file Common Methods")
public class FileFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "file";
    }

    /**
     * @param path            File Path/Name
     * @param createDirectory Do you want to create
     * @return File Files
     */
    private static File getFile(String path, boolean createDirectory) {
        File f = new File(path);
        if (createDirectory && !f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        return f;
    }

    @Comment("Write file")
    @Example("${file.write('e:/result.html',resp.html,false)}")
    public static void write(String path, String content, boolean append) throws IOException {
        write(path, content, Charset.defaultCharset().name(), append);
    }

    @Comment("Write file")
    @Example("${file.write('e:/result.html',resp.html,'UTF-8',false)}")
    public static void write(String path, String content, String charset, boolean append) throws IOException {
        write(path, StringFunctionExecutor.bytes(content, charset), append);
    }

    @Comment("Write file")
    @Example("${file.write('e:/result.html',resp.bytes,false)}")
    public static void write(String path, byte[] bytes, boolean append) throws IOException {
        write(path, new ByteArrayInputStream(bytes), append);
    }

    @Comment("Write file")
    @Example("${file.write('e:/result.html',resp.stream,false)}")
    public static void write(String path, InputStream stream, boolean append) throws IOException {
        try (FileOutputStream fos = new FileOutputStream(getFile(path, true), append)) {
            IOUtils.copyLarge(stream, fos);
        }
    }

    @Comment("Write file")
    @Example("${file.write('e:/result.html',resp.bytes,false)}")
    public static void write(String path, InputStream stream) throws IOException {
        write(path, stream, false);
    }

    @Comment("Write file")
    @Example("${file.write('e:/result.html',resp.html)}")
    public static void write(String path, String content) throws IOException {
        write(path, content, false);
    }

    @Comment("Write file")
    @Example("${file.write('e:/result.html',resp.html,'UTF-8')}")
    public static void write(String path, String content, String charset) throws IOException {
        write(path, content, charset, false);
    }

    @Comment("Write file")
    @Example("${file.write('e:/result.html',resp.bytes)}")
    public static void write(String path, byte[] bytes) throws IOException {
        write(path, bytes, false);
    }

    @Comment("Download Url Translation")
    @Example("${file.download('e:/downloadPath',urls)}")
    public static void download(String path, List<String> urls) throws IOException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                FileUtils.downloadFile(path, url, "", true, false);
            }
        }
    }

    @Comment("Download Url Translation，Do not translate the following text")
    @Example("${file.download('e:/downloadPath',urls,false)}")
    public static void download(String path, List<String> urls, boolean saveOriginPath) throws IOException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                FileUtils.downloadFile(path, url, "", true, saveOriginPath);
            }
        }
    }

    @Comment("Delay（Seconds）Download Url Translation")
    @Example("${file.download('e:/downloadPath',urls,[1000,4000])}")
    public static void download(String path, List<String> urls, List<Integer> randomRange)
            throws IOException, InterruptedException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                Long sleepMillis = RandomUtils.nextLong(randomRange.get(0), randomRange.get(1));
                TimeUnit.MILLISECONDS.sleep(sleepMillis);
                FileUtils.downloadFile(path, url, "", false, false);
            }
        }
    }

    @Comment("Delay（Seconds）Download Url Translation")
    @Example("${file.download('e:/downloadPath',urls,[1000,4000],false)}")
    public static void download(String path, List<String> urls, List<Integer> randomRange, boolean saveOriginPath)
            throws IOException, InterruptedException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                Long sleepMillis = RandomUtils.nextLong(randomRange.get(0), randomRange.get(1));
                TimeUnit.MILLISECONDS.sleep(sleepMillis);
                FileUtils.downloadFile(path, url, "", false, saveOriginPath);
            }
        }
    }

    @Comment("Download through proxy Url Translation")
    @Example("${file.download('e:/downloadPath','127.0.0.1:9999',urls)}")
    public static void download(String path, String proxy, List<String> urls) throws IOException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                FileUtils.downloadFile(path, url, proxy, true, false);
            }
        }
    }

    @Comment("Download through proxy Url Translation")
    @Example("${file.download('e:/downloadPath','127.0.0.1:9999',urls,false)}")
    public static void download(String path, String proxy, List<String> urls, boolean saveOriginPath) throws IOException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                FileUtils.downloadFile(path, url, proxy, true, saveOriginPath);
            }
        }
    }

    @Comment("Use proxy and random delay（Seconds）Download Link Url Translation")
    @Example("${file.download('e:/downloadPath','127.0.0.1:9999',urls,[1000,4000])}")
    public static void download(String path, String proxy, List<String> urls, List<Integer> randomRange)
            throws IOException, InterruptedException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                Long sleepMillis = RandomUtils.nextLong(randomRange.get(0), randomRange.get(1));
                TimeUnit.MILLISECONDS.sleep(sleepMillis);
                FileUtils.downloadFile(path, url, proxy, false, false);
            }
        }
    }

    @Comment("Use proxy and random delay（Seconds）Download Link Url Translation")
    @Example("${file.download('e:/downloadPath','127.0.0.1:9999',urls,[1000,4000],false)}")
    public static void download(String path, String proxy, List<String> urls, List<Integer> randomRange, boolean saveOriginPath)
            throws IOException, InterruptedException {
        if (!CollectionUtils.isEmpty(urls)) {
            for (String url : urls) {
                Long sleepMillis = RandomUtils.nextLong(randomRange.get(0), randomRange.get(1));
                TimeUnit.MILLISECONDS.sleep(sleepMillis);
                FileUtils.downloadFile(path, url, proxy, false, saveOriginPath);
            }
        }
    }

    @Comment("Download Url Translation")
    @Example("${file.download('e:/downloadPath',url)}")
    public static void download(String path, String url) throws IOException {
        if (url != null) {
            FileUtils.downloadFile(path, url, "", true, false);
        }
    }

    @Comment("Download Url Translation")
    @Example("${file.download('e:/downloadPath',url,false)}")
    public static void download(String path, String url, boolean saveOriginPath) throws IOException {
        if (url != null) {
            FileUtils.downloadFile(path, url, "", true, saveOriginPath);
        }
    }

    @Comment("Download through proxy Url Translation")
    @Example("${file.download('e:/downloadPath','127.0.0.1:9999',url)}")
    public static void download(String path, String proxy, String url) throws IOException {
        if (url != null) {
            FileUtils.downloadFile(path, url, proxy, true, false);
        }
    }

    @Comment("Download through proxy Url Translation")
    @Example("${file.download('e:/downloadPath','127.0.0.1:9999',url,false)}")
    public static void download(String path, String proxy, String url, boolean saveOriginPath) throws IOException {
        if (url != null) {
            FileUtils.downloadFile(path, url, proxy, true, saveOriginPath);
        }
    }

    @Comment("read file")
    @Example("${file.bytes('e:/result.html')}")
    public static byte[] bytes(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(getFile(path, false))) {
            return IOUtils.toByteArray(fis);
        }
    }

    @Comment("read file")
    @Example("${file.string('e:/result.html','UTF-8')}")
    public static String string(String path, String charset) throws IOException {
        return StringFunctionExecutor.newString(bytes(path), charset);
    }

    @Comment("read file")
    @Example("${file.string('e:/result.html')}")
    public static String string(String path) throws IOException {
        return StringFunctionExecutor.newString(bytes(path), Charset.defaultCharset().name());
    }

}
