package com.github.nekolr.slime.executor.function;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExecutor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * List Class Instance
 * Added similar python 的 split Method
 */
@Component
@Comment("list Common Methods")
public class ListFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "list";
    }

    @Comment("Get list The length of the text")
    @Example("${list.length(listVar)}")
    public static int length(List<?> list) {
        return list != null ? list.size() : 0;
    }

    /**
     * @param list 原 List
     * @param len  How many pieces go into the split
     * @return List<List < ?>> Shared Music
     */
    @Comment("分割 List")
    @Example("${list.split(listVar,10)}")
    public static List<List<?>> split(List<?> list, int len) {
        List<List<?>> result = new ArrayList<>();
        if (list == null || list.size() == 0 || len < 1) {
            return result;
        }
        int size = list.size();
        int count = (size + len - 1) / len;
        for (int i = 0; i < count; i++) {
            List<?> subList = list.subList(i * len, ((i + 1) * len > size ? size : len * (i + 1)));
            result.add(subList);
        }
        return result;
    }

    @Comment("Intercept List")
    @Example("${list.sublist(listVar,fromIndex,toIndex)}")
    public static List<?> sublist(List<?> list, int fromIndex, int toIndex) {
        return list != null ? list.subList(fromIndex, toIndex) : new ArrayList<>();
    }

    @Comment("Filter Strings list 元素")
    @Example("${listVar.filterStr(pattern)}")
    public static List<String> filterStr(List<String> list, String pattern) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<String> result = new ArrayList<>(list.size());
        for (String item : list) {
            if (Pattern.matches(pattern, item)) {
                result.add(item);
            }
        }
        return result;
    }

}
