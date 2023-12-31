package com.github.nekolr.slime.executor.function;

import org.apache.commons.lang3.StringUtils;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExecutor;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * url Character set to use in the encoding/Decrypt Default charset（UTF-8）
 */
@Component
public class UrlFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "url";
    }

    @Comment("Get url Parameters")
    @Example("${url.parameter('https://www.baidu.com/s?wd=spider-flow','wd')}")
    public static String parameter(String url, String key) {
        return parameterMap(url).get(key);
    }

    @Comment("Get url All parameters")
    @Example("${url.parameterMap('https://www.baidu.com/s?wd=spider-flow&abbr=sf')}")
    public static Map<String, String> parameterMap(String url) {
        Map<String, String> map = new HashMap<String, String>();
        int index = url.indexOf("?");
        if (index != -1) {
            String param = url.substring(index + 1);
            if (StringUtils.isNotBlank(param)) {
                String[] params = param.split("&");
                for (String item : params) {
                    String[] kv = item.split("=");
                    if (kv.length > 0) {
                        if (StringUtils.isNotBlank(kv[0])) {
                            String value = "";
                            if (kv.length > 1 && StringUtils.isNotBlank(kv[1])) {
                                int kv1Index = kv[1].indexOf("#");
                                if (kv1Index != -1) {
                                    value = kv[1].substring(0, kv1Index);
                                } else {
                                    value = kv[1];
                                }
                            }
                            map.put(kv[0], value);
                        }
                    }
                }
            }
        }
        return map;
    }

    @Comment("url Code")
    @Example("${url.encode('https://www.baidu.com/s?wd=spider-flow')}")
    public static String encode(String url) {
        return encode(url, Charset.defaultCharset().name());
    }

    @Comment("url Code")
    @Example("${url.encode('https://www.baidu.com/s?wd=spider-flow','UTF-8')}")
    public static String encode(String url, String charset) {
        try {
            return url != null ? URLEncoder.encode(url, charset) : null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Comment("url Decrypt")
    @Example("${url.decode(strVar)}")
    public static String decode(String url) {
        return decode(url, Charset.defaultCharset().name());
    }

    @Comment("url Decrypt")
    @Example("${url.decode(strVar,'UTF-8')}")
    public static String decode(String url, String charset) {
        try {
            return url != null ? URLDecoder.decode(url, charset) : null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
