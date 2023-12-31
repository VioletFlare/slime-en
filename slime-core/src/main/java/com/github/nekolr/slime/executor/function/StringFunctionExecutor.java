package com.github.nekolr.slime.executor.function;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExecutor;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * String Class Instance
 */
@Component
@Comment("string Common Methods")
public class StringFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "string";
    }

    @Comment("Character Ranges")
    @Example("${string.substring(str,5)}")
    public static String substring(String content, int beginIndex) {
        return content != null ? content.substring(beginIndex) : null;
    }

    @Comment("Character Ranges")
    @Example("${string.substring(str,0,str.length() - 1)}")
    public static String substring(String content, int beginIndex, int endIndex) {
        return content != null ? content.substring(beginIndex, endIndex) : null;
    }

    @Comment("Lower Case")
    @Example("${string.lower(str)}")
    public static String lower(String content) {
        return content != null ? content.toLowerCase() : null;
    }

    @Comment("Turn string to upper case")
    @Example("${string.upper(str)}")
    public static String upper(String content) {
        return content != null ? content.toUpperCase() : null;
    }

    @Comment("Finds the position of the specified character in a string.")
    @Example("${string.indexOf(content,str)}")
    public static int indexOf(String content, String str) {
        return content != null ? content.indexOf(str) : -1;
    }

    @Comment("Find the last occurrence of a specified character in a string")
    @Example("${string.lastIndexOf(content,str)}")
    public static int lastIndexOf(String content, String str) {
        return content != null ? content.lastIndexOf(str) : -1;
    }

    @Comment("Finds the position of the specified character in a string.")
    @Example("${string.indexOf(content,str,fromIndex)}")
    public static int indexOf(String content, String str, int fromIndex) {
        return content != null ? content.indexOf(str, fromIndex) : -1;
    }

    @Comment("Turn string into int")
    @Example("${string.toInt(value)}")
    public static int toInt(String value) {
        return Integer.parseInt(value);
    }

    @Comment("Turn string into Integer")
    @Example("${string.toInt(value,defaultValue)}")
    public static Integer toInt(String value, Integer defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return defaultValue;
        }
    }

    @Comment("What is your name?")
    @Example("${string.replace(content,source,target)}")
    public static String replace(String content, String source, String target) {
        return content != null ? content.replace(source, target) : null;
    }

    @Comment("Regex Match")
    @Example("${string.replaceAll(content,regx,target)}")
    public static String replaceAll(String content, String regx, String target) {
        return content != null ? content.replaceAll(regx, target) : null;
    }

    @Comment("Regex Match")
    @Example("${string.replaceFirst(content,regx,target)}")
    public static String replaceFirst(String content, String regx, String target) {
        return content != null ? content.replaceFirst(regx, target) : null;
    }

    @Comment("Regex Match")
    @Example("${string.length(content)}")
    public static int length(String content) {
        return content != null ? content.length() : -1;
    }

    @Comment("Remove Spaces From Both Sides Of String")
    @Example("${string.trim(content)}")
    public static String trim(String content) {
        return content != null ? content.trim() : null;
    }

    @Comment("Shared Secret")
    @Example("${string.split(content,regx)}")
    public static List<String> split(String content, String regx) {
        return content != null ? Arrays.asList(content.split(regx)) : new ArrayList<>(0);
    }

    @Comment("Get string value byte[]")
    @Example("${string.bytes(content)}")
    public static byte[] bytes(String content) {
        return content != null ? content.getBytes() : null;
    }

    @Comment("Get string value byte[]")
    @Example("${string.bytes(content,charset)}")
    public static byte[] bytes(String content, String charset) {
        try {
            return content != null ? content.getBytes(charset) : null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Comment("byte[] 转 String")
    @Example("${string.newString(bytes)}")
    public static String newString(byte[] bytes) {
        return bytes != null ? new String(bytes) : null;
    }

    @Comment("byte[] 转 String")
    @Example("${string.newString(bytes,charset)}")
    public static String newString(byte[] bytes, String charset) {
        try {
            return bytes != null ? new String(bytes, charset) : null;
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    @Comment("Are two strings equal?")
    @Example("${string.equals(chars1,chars2)}")
    public static boolean equals(String str1, String str2) {
        return Objects.equals(str1, str2);
    }

    @Comment("Generating UUID")
    @Example("${string.uuid()}")
    public static String uuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    @Comment("Generate multiple UUID")
    @Example("${string.uuid(size)}")
    public static List<String> uuids(Integer size) {
        List<String> ids = new ArrayList<String>();
        for (int i = 0; i < size; i++) {
            ids.add(UUID.randomUUID().toString().replace("-", ""));
        }
        return ids;
    }

}
