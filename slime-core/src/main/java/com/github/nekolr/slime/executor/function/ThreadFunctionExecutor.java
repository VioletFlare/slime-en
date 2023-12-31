package com.github.nekolr.slime.executor.function;

import com.github.nekolr.slime.annotation.Comment;
import com.github.nekolr.slime.annotation.Example;
import com.github.nekolr.slime.executor.FunctionExecutor;
import org.springframework.stereotype.Component;

@Component
@Comment("thread Common Methods")
public class ThreadFunctionExecutor implements FunctionExecutor {

    @Override
    public String getFunctionPrefix() {
        return "thread";
    }

    @Comment("Threads")
    @Example("${thread.sleep(1000L)}")
    public static void sleep(Long sleepTime) {
        try {
            Thread.sleep(sleepTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
