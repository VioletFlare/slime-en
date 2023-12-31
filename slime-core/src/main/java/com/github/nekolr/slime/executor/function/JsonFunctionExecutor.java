package com.github.nekolr.slime.executor.function;

import com.alibaba.fastjson.JSON;
import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExecutor;
import org.springframework.stereotype.Component;

/**
 * Json 和 String  Configure KSpread...
 */
@Component
@Comment("json Common Methods")
public class JsonFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "json";
    }

    @Comment("Turn string into json 对象")
    @Example("${json.parse('{code : 1}')}")
    public static Object parse(String jsonString) {
        return jsonString != null ? JSON.parse(jsonString) : null;
    }

    @Comment("Object To json String")
    @Example("${json.stringify(objVar)}")
    public static String stringify(Object object) {
        return object != null ? JSON.toJSONString(object) : null;
    }
}
