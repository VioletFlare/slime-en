package com.github.nekolr.slime.executor.node;

import com.github.nekolr.slime.context.SpiderContext;
import com.github.nekolr.slime.executor.NodeExecutor;
import com.github.nekolr.slime.model.SpiderNode;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Wait for the execution to complete
 */
@Component
public class JoinExecutor implements NodeExecutor {

    /**
     * Variable completed node cache
     */
    private Map<String, Map<String, Object>> cachedVariables = new HashMap<>();

    @Override
    public void execute(SpiderNode node, SpiderContext context, Map<String, Object> variables) {

    }

    @Override
    public boolean allowExecuteNext(SpiderNode node, SpiderContext context, Map<String, Object> variables) {
        String key = context.getId() + "-" + node.getNodeId();
        synchronized (cachedVariables) {
            boolean isDone = node.isDone();
            Map<String, Object> cached = cachedVariables.get(key);
            // All nodes are uncompleted
            if (!isDone) {
                if (cached == null) {
                    cached = new HashMap<>();
                    cachedVariables.put(key, cached);
                }
                cached.putAll(variables);
            } else if (cached != null) {
                // Save the current variable into the cache，Pass to Next Level
                variables.putAll(cached);
                cachedVariables.remove(key);
            }
            return isDone;
        }
    }

    @Override
    public String supportType() {
        return "join";
    }
}
