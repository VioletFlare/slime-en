package com.github.nekolr.slime.executor.function;

import org.apache.commons.codec.binary.Base64;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExecutor;
import org.springframework.stereotype.Component;

/**
 * String content and Base64  Configure KSpread...
 */
@Component
@Comment("base64 Common Methods")
public class Base64FunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "base64";
    }

    @Comment("Based on byte[] Conduct base64 Code")
    @Example("${base64.encode(resp.bytes)}")
    public static String encode(byte[] bytes) {
        return bytes != null ? Base64.encodeBase64String(bytes) : null;
    }

    @Comment("Based on byte[] Conduct base64 Code")
    @Example("${base64.encode(resp.bytes,'UTF-8')}")
    public static String encode(String content, String charset) {
        return encode(StringFunctionExecutor.bytes(content, charset));
    }

    @Comment("Based on String Conduct base64 Code")
    @Example("${base64.encode(resp.html)}")
    public static String encode(String content) {
        return encode(StringFunctionExecutor.bytes(content));
    }

    @Comment("Based on byte[] Conduct base64 Code")
    @Example("${base64.encodeBytes(resp.bytes)}")
    public static byte[] encodeBytes(byte[] bytes) {
        return bytes != null ? Base64.encodeBase64(bytes) : null;
    }

    @Comment("Based on String Conduct base64 Code")
    @Example("${base64.encodeBytes(resp.html,'UTF-8')}")
    public static byte[] encodeBytes(String content, String charset) {
        return encodeBytes(StringFunctionExecutor.bytes(content, charset));
    }

    @Comment("Based on String Conduct base64 Code")
    @Example("${base64.encodeBytes(resp.html)}")
    public static byte[] encodeBytes(String content) {
        return encodeBytes(StringFunctionExecutor.bytes(content));
    }

    @Comment("Based on String Conduct base64 Decrypt")
    @Example("${base64.decode(resp.html)}")
    public static byte[] decode(String base64) {
        return base64 != null ? Base64.decodeBase64(base64) : null;
    }

    @Comment("Based on byte[] Conduct base64 Decrypt")
    @Example("${base64.decode(resp.bytes)}")
    public static byte[] decode(byte[] base64) {
        return base64 != null ? Base64.decodeBase64(base64) : null;
    }

    @Comment("Based on String Conduct base64 Decrypt")
    @Example("${base64.decodeString(resp.html)}")
    public static String decodeString(String base64) {
        return base64 != null ? new String(Base64.decodeBase64(base64)) : null;
    }

    @Comment("Based on byte[] Conduct base64 Decrypt")
    @Example("${base64.decodeString(resp.bytes)}")
    public static String decodeString(byte[] base64) {
        return base64 != null ? new String(Base64.decodeBase64(base64)) : null;
    }

    @Comment("Based on byte[] Conduct base64 Decrypt")
    @Example("${base64.decodeString(resp.bytes,'UTF-8')}")
    public static String decodeString(byte[] base64, String charset) {
        return base64 != null ? StringFunctionExecutor.newString(Base64.decodeBase64(base64), charset) : null;
    }
}
