package com.github.nekolr.slime.executor.node;

import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.model.SpiderNode;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Repeating Executor
 */
@Component
public class LoopExecutor implements NodeExecutor {

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
    }

    @Override
    public String supportType() {
        return "loop";
    }
}
